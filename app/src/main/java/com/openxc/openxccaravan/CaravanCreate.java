package com.openxc.openxccaravan;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.openxc.VehicleManager;
import com.openxc.messages.SimpleVehicleMessage;
import com.openxcplatform.openxccaravan.R;

import java.util.HashMap;
import java.util.Map;

public class CaravanCreate extends Activity {
    private static final String TAG = "CaravanCreateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caravan_create);

        Intent vehManager = new Intent(this, VehicleManager.class);
        bindService(vehManager, mConnection, Context.BIND_AUTO_CREATE);

        final EditText caravan_name = (EditText)findViewById(R.id.caravan_name);
        CompoundButton passwordSwitch = (CompoundButton)findViewById(R.id.pw_protected);
        final EditText password = (EditText)findViewById(R.id.password);
        final EditText password_repeat = (EditText)findViewById(R.id.password_repeat);
        final EditText max_members = (EditText)findViewById(R.id.max_members);

        //Set the password boxes (in)active based on the sswitch setting
        passwordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    password.setEnabled(true);
                    password_repeat.setEnabled(true);
                }
                else {
                    password.setEnabled(false);
                    password_repeat.setEnabled(false);
                    password.setText("");
                    password_repeat.setText("");
                }
            }
        });
    }

    public void onPause() {
        super.onPause();

        if(mVehicleManager != null) {
            Log.i(TAG, "Unbinding from Vehicle Manager");
            unbindService(mConnection);
            mVehicleManager = null;
        }
    }

    public void onResume() {
        super.onResume();

        if(mVehicleManager == null) {
            Intent intent = new Intent(this, VehicleManager.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    VehicleManager mVehicleManager;
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            mVehicleManager = ((VehicleManager.VehicleBinder)service).getService();
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.w(TAG, "VehicleManager Service disconnected");
            mVehicleManager = null;
        }
    };

    public void MakeToast (String text, int duration) {
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context,text,duration);
        toast.show();
    }

    public void StartCaravan(View view) {
        final EditText name = (EditText) findViewById(R.id.caravan_name);
        CompoundButton passwordSwitch = (CompoundButton) findViewById(R.id.pw_protected);
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText password_repeat = (EditText) findViewById(R.id.password_repeat);
        final EditText max = (EditText) findViewById(R.id.max_members);


        if (name.getText().toString().length() == 0) {
            MakeToast("Please provide a caravan name", 3);
        }

        else if (max.getText().toString().length() == 0 || !(max.getText().toString().trim().matches("^[0-9]*$") && Integer.parseInt(max.getText().toString()) <= 16 && Integer.parseInt(max.getText().toString()) > 1)) {
            MakeToast("Caravan max size must be between 1 and 16",3);
        }

        else if (passwordSwitch.isChecked() && password.getText().toString().equals(password_repeat.getText().toString())) {
            MakeToast("Password and retype password do not match!",3);
        }

        else {
            Map caravan_data = new HashMap();
            caravan_data.put("pretty", name.getText().toString().replace(" ","%20").trim());
            if (passwordSwitch.isChecked() && password.getText().toString().length() == 0) { caravan_data.put("protected", false); }
            else { caravan_data.put("protected", passwordSwitch.isChecked()); }
            caravan_data.put("protected", passwordSwitch.isChecked());
            caravan_data.put("pw",password.getText().toString().replace(" ","%20").trim());
            caravan_data.put("max", max.getText().toString().trim());

            // Send the data
            SimpleVehicleMessage newMessage = new SimpleVehicleMessage(Long.valueOf(0), "caravan_msg", "setup_caravan", caravan_data);
            Log.v(TAG, newMessage.toString());
            mVehicleManager.send(newMessage);
            // Change the view
            Intent HostWaiting = new Intent(this, HostWaiting.class);
            startActivity(HostWaiting);
        }
    }
}
