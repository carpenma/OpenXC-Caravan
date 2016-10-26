package com.openxc.openxccaravan;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.openxc.VehicleManager;
import com.openxc.messages.SimpleVehicleMessage;
import com.openxc.messages.VehicleMessage;
import com.openxcplatform.openxccaravan.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AvailableCaravans extends ListActivity {
    private VehicleManager mVehicleManager;
    private static final String TAG = "AvailableCaravansAct";
    ArrayList<Available> available_list = new ArrayList<>();
    AvailableAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_caravans);

        // Custom array adapter required most likely
        final ListView available = (ListView) findViewById(android.R.id.list);
        final EditText password = (EditText) findViewById(R.id.password);
        adapter = new AvailableAdapter(AvailableCaravans.this, R.layout.available_caravan_row, available_list);

        available.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        available.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.setSelectedIdx(i);
                Log.i(TAG,"Item set selected: "+i);
                if(available_list.get(i).locked) { password.setEnabled(true); }
                else { password.setEnabled(false); }
            }
        });
        available.setAdapter(adapter);

        Intent vehManager = new Intent(this, VehicleManager.class);
        bindService(vehManager, mConnection, Context.BIND_AUTO_CREATE);

        // Send request for caravans.. After a delay/if the required connections have been made
        /*SimpleVehicleMessage newMessage = new SimpleVehicleMessage(Long.valueOf(0), "caravan_msg", "list_caravans");
        Log.v(TAG, newMessage.toString());
        mVehicleManager.send(newMessage);*/
    }

    public void onPause() {
        super.onPause();

        if(mVehicleManager != null) {
            Log.i(TAG, "MRC: Unbinding from Vehicle Manager - 2");
            mVehicleManager.removeListener(VehicleMessage.class, mListener);
            unbindService(mConnection);
            mVehicleManager = null;
        }
    }

    public void onResume() {
        super.onResume();
        Log.i(TAG, "MRC: (Re)Binding to Vehicle Manager");
        if(mVehicleManager == null) {
            Intent intent = new Intent(this, VehicleManager.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private VehicleMessage.Listener mListener = new VehicleMessage.Listener() {
        @Override
        public void receive(final VehicleMessage message) {
        Log.i(TAG, "MRC: Running receive message function");
        AvailableCaravans.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message.asNamedMessage().getName().equals("caravan_data") & message.asSimpleMessage().getValue().equals("caravans")) {
                    // Pull the data out of the packet, break it into pieces, and populate the array that gets loaded into the listview
                    Map extras = message.getExtras();
                    Log.i(TAG,extras.toString());
                    for (int i=0 ; i < extras.size() ; i++) {
                        try {
                            JSONObject details = (new JSONObject(extras.get(extras.keySet().toArray()[i]).toString())); // This is horribly inefficient, fix eventually - MRC
                            available_list.add(new Available(details.getString("pretty"), details.getInt("count"), details.getInt("max"), details.getBoolean("protected")));
                            adapter.notifyDataSetChanged();
                            Log.i(TAG,available_list.toString());
                            Log.i(TAG,available_list.get(0).pretty_name);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            mVehicleManager = ((VehicleManager.VehicleBinder)service).getService();
            mVehicleManager.addListener(SimpleVehicleMessage.class, mListener);
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.w(TAG, "VehicleManager Service disconnected");
            mVehicleManager = null;
        }
    };

    public void JoinCaravan(View view) {

        final EditText password = (EditText) findViewById(R.id.password);
        final ListView available = (ListView) findViewById(android.R.id.list);

        Log.i(TAG,"Selected: "+adapter.getSelectedIdx());

        Map join_data = new HashMap();
        join_data.put("pretty", available_list.get(adapter.getSelectedIdx()).pretty_name);
        join_data.put("pw", password.getText().toString());

        // Send caravan request
        SimpleVehicleMessage newMessage = new SimpleVehicleMessage(Long.valueOf(0), "caravan_msg", "join_caravan", join_data);
        Log.v(TAG, newMessage.toString());
        mVehicleManager.send(newMessage);

        // Show loading popup?
    }

    public void RefreshList(View view) {
        // Clear the listview
        available_list.clear();
        Map extras = new HashMap();

        // Send request for caravans to the V2X
        SimpleVehicleMessage newMessage = new SimpleVehicleMessage(Long.valueOf(0), "caravan_msg", "list_caravans", extras);
        Log.v(TAG, newMessage.toString());
        mVehicleManager.send(newMessage);
    }
}
