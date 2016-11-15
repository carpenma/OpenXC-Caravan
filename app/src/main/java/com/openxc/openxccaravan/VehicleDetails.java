package com.openxc.openxccaravan;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.openxc.VehicleManager;
import com.openxc.messages.SimpleVehicleMessage;
import com.openxc.messages.VehicleMessage;
import com.openxcplatform.openxccaravan.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class VehicleDetails extends Activity {
    private static final String TAG = "VehicleDetailsActivity";
    private VehicleManager mVehicleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);

        Spinner makesSpinner = (Spinner)findViewById(R.id.vehicle_make);
        final ArrayAdapter<String> makesSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.makes));
        makesSpinner.setAdapter(makesSpinnerArrayAdapter);

        // This should eventually be moved to the onSelected method of the makes spinner so it populates appropriate models
        Spinner modelsSpinner = (Spinner)findViewById(R.id.vehicle_model);
        final ArrayAdapter<String> modelsSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.models_ford));
        modelsSpinner.setAdapter(modelsSpinnerArrayAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        // When the activity goes into the background or exits, we want to make
        // sure to unbind from the service to avoid leaking memory
        if(mVehicleManager != null) {
            Log.i(TAG, "Unbinding from Vehicle Manager");
            // Remember to remove your listeners, in typical Android
            // fashion.
            mVehicleManager.removeListener(VehicleMessage.class, mListener);
            unbindService(mConnection);
            mVehicleManager = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // When the activity starts up or returns from the background,
        // re-connect to the VehicleManager so we can receive updates.
        if(mVehicleManager == null) {
            Intent intent = new Intent(this, VehicleManager.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private VehicleMessage.Listener mListener = new VehicleMessage.Listener() {
        @Override
        public void receive(final VehicleMessage message) {
            VehicleDetails.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (message.asNamedMessage().getName().equals("caravan_msg")) {
                        Log.i(TAG,"Caravan Message Received!");
                    }
                }
            });
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the VehicleManager service is
        // established, i.e. bound.
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i(TAG, "Bound to VehicleManager");
            // When the VehicleManager starts up, we store a reference to it
            // here in "mVehicleManager" so we can call functions on it
            // elsewhere in our code.
            mVehicleManager = ((VehicleManager.VehicleBinder) service)
                    .getService();

            // Elsewhere in your activity, register this listener to receive all CAN
            // messages from the VI
            mVehicleManager.addListener(SimpleVehicleMessage.class, mListener);
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.w(TAG, "VehicleManager Service disconnected unexpectedly");
            mVehicleManager = null;
        }
    };

    public void MakeToast (String text, int duration) {
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context,text,duration);
        toast.show();
    }

    public void SaveVehicle(View view){
        // Probably do some kind of simple validation?
        EditText VehName = (EditText)findViewById(R.id.vehicle_name);
        EditText VehYear = (EditText)findViewById(R.id.vehicle_year);
        EditText NumPass = (EditText)findViewById(R.id.num_passengers);
        Spinner VehMake = (Spinner) findViewById(R.id.vehicle_make);
        Spinner VehModel = (Spinner) findViewById(R.id.vehicle_model);
        // Validate input
        int cur_year = Calendar.getInstance().get(Calendar.YEAR);
        if (VehYear.getText().toString().length() == 0 || !(VehYear.getText().toString().trim().matches("^[0-9]*$") && Integer.parseInt(VehYear.getText().toString()) > 1980 && Integer.parseInt(VehYear.getText().toString()) < cur_year + 2)) {
            MakeToast("Model year must be between 1980 and "+(cur_year + 2), 3);
        }
        else if (NumPass.getText().toString().length() == 0 ||  !(NumPass.getText().toString().trim().matches("^[0-9]*$") && Integer.parseInt(NumPass.getText().toString()) <= 20 && Integer.parseInt(NumPass.getText().toString()) > 0)) {
            MakeToast("Number of passengers must be between 1 and 20!", 3);
        }
        else if (VehName.getText().toString().length() == 0) {
            MakeToast("Please provide a vehicle name", 3);
        }
        else {
            Map vehicle_data = new HashMap();
            vehicle_data.put("pretty", VehName.getText().toString().replace(" ","%20").trim());
            vehicle_data.put("year", VehYear.getText().toString());
            vehicle_data.put("make", VehMake.getSelectedItem().toString());
            vehicle_data.put("model", VehModel.getSelectedItem().toString());
            vehicle_data.put("num_pass", NumPass.getText().toString().trim());
            // Actually send the data
            SimpleVehicleMessage newMessage = new SimpleVehicleMessage(new Long(0), "caravan_msg", "veh_setup", vehicle_data);
            Log.v(TAG, newMessage.toString());
            mVehicleManager.send(newMessage);

            // Load the next activity (selecting caravan type)
            Intent CaravanType = new Intent(this, CaravanType.class);
            startActivity(CaravanType);
        }
    }
}
