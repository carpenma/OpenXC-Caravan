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

public class HostWaiting extends ListActivity {
    private VehicleManager mVehicleManager;
    private static final String TAG = "HostWaitingAct";
    ArrayList<Member> member_list = new ArrayList<>();
    MemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_waiting);

        final ListView members = (ListView) findViewById(android.R.id.list);
        adapter = new MemberAdapter(HostWaiting.this, R.layout.caravan_member_row, member_list);

        members.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        members.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.setSelectedIdx(i);
                Log.i(TAG,"Item set selected: "+i);
            }
        });
        members.setAdapter(adapter);

        Intent vehManager = new Intent(this, VehicleManager.class);
        bindService(vehManager, mConnection, Context.BIND_AUTO_CREATE);
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
            HostWaiting.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (message.asNamedMessage().getName().equals("caravan_data")) {
                        if (message.asSimpleMessage().getValue().equals("new_member")) {
                            Map extras = message.getExtras();
                            try {
                                JSONObject details = new JSONObject(extras);
                                member_list.add(new Member(details.getString("pretty"),details.getString("make"), details.getString("model"),details.getInt("year")));
                                adapter.notifyDataSetChanged();
                                Log.i(TAG,"Member List: "+member_list);
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
}