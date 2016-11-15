package com.openxc.openxccaravan;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.os.IBinder;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.openxc.VehicleManager;
import com.openxc.messages.SimpleVehicleMessage;
import com.openxc.messages.VehicleMessage;
import com.openxcplatform.openxccaravan.R;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,MessageFragment.OnMessageSendListener {

    private static final String TAG = "MainActivity";
    private VehicleManager mVehicleManager;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        displaySelectedScreen(0);
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
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (message.asNamedMessage().getName().equals("caravan_msg")) {
                        Log.i(TAG,"Caravan Message Received!");
                        if (message.asSimpleMessage().getValue().equals("text_rcv")) {
                            Log.i(TAG, "New text message received!");
                            MessageFragment msg_frag = (MessageFragment) getFragmentManager().findFragmentById();
                        }
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

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        displaySelectedScreen(position);    // update the main content by replacing fragments
    }

    private void displaySelectedScreen(int itemId) {
        Fragment fragment;
        CharSequence title = "Main";

        switch(itemId) {
            case 0:
                fragment = new MainFragment();
                break;
            case 1:
                fragment = new MessageFragment();
                title = "Messages";
                break;
            case 2:
                fragment = new PollFragment();
                title = "Polls";
                break;
            case 3:
                fragment = new SettingsFragment();
                title = "Settings";
                break;
            default:
                fragment = new MainFragment();
                break;
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container,fragment);
        ft.addToBackStack(null);
        ft.commit();

        ActionBar ab = getActionBar();
        if(ab != null) {
            ab.setDisplayShowTitleEnabled(true);
            ab.setTitle(title);
        }
    }

    public void sendMessage(String msg) {
        Map msg_data = new HashMap();
        msg_data.put("to", "*");
        msg_data.put("body", msg.replace(" ","%20"));  // Special characters that might make the JSON parsers upset?
        SimpleVehicleMessage newMessage = new SimpleVehicleMessage(new Long(0), "caravan_msg", "text_msg", msg_data);
        Log.v(TAG, newMessage.toString());
        mVehicleManager.send(newMessage);
    }
}
