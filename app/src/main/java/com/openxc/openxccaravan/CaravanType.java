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

import com.openxc.VehicleManager;
import com.openxc.messages.SimpleVehicleMessage;
import com.openxc.messages.VehicleMessage;
import com.openxcplatform.openxccaravan.R;

import java.util.HashMap;
import java.util.Map;

public class CaravanType extends Activity {
    private static final String TAG = "CaravanTypeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caravan_type);

        Intent vehManager = new Intent(this, VehicleManager.class);
        bindService(vehManager, mConnection, Context.BIND_AUTO_CREATE);

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

    public void NewCaravan(View view) {
        Map extras = new HashMap();
        extras.put("role", "HOST");
        // Tell the V2X we are a host
        SimpleVehicleMessage newMessage = new SimpleVehicleMessage(Long.valueOf(0), "caravan_msg", "role", extras);
        Log.v(TAG, newMessage.toString());
        mVehicleManager.send(newMessage);
        // Go to caravan creation activity
        Intent CaravanCreate = new Intent(this, CaravanCreate.class);
        startActivity(CaravanCreate);
    }

    public void ExistingCaravan(View view) {
        Map extras = new HashMap();
        extras.put("role", "MEMBER");
        // Tell the V2X we are a member
        SimpleVehicleMessage newMessage = new SimpleVehicleMessage(Long.valueOf(0), "caravan_msg", "role", extras);
        Log.v(TAG, newMessage.toString());
        mVehicleManager.send(newMessage);
        // Go to available caravans activity
        Intent AvailableCaravans = new Intent(this, AvailableCaravans.class);
        startActivity(AvailableCaravans);

    }
}
