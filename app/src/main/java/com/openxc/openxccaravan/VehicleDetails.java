package com.openxc.openxccaravan;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.openxc.messages.SimpleVehicleMessage;
import com.openxcplatform.openxccaravan.R;;

import java.util.HashMap;
import java.util.Map;

public class VehicleDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);
    }

    public void SaveVehicle(View view){
        // Probably do some kind of simple validation?
        EditText VehName = (EditText)findViewById(R.id.vehicle_name);
        //EditText VehYear = (EditText)findViewById(R.id.vehicle_year);
        Spinner VehMake = (Spinner) findViewById(R.id.vehicle_make);
        Spinner VehModel = (Spinner) findViewById(R.id.vehicle_model);
        Map vehicle_data = new HashMap();
        vehicle_data.put("pretty", VehName.getText().toString());
        vehicle_data.put("year", "0000");
        vehicle_data.put("make", VehMake.toString());
        vehicle_data.put("model", VehModel.toString());
        // Actually send the data
        SimpleVehicleMessage newMessage = new SimpleVehicleMessage(new Long(0), "caravan_message", "veh_setup", vehicle_data);
        Log.v(TAG, newMessage.toString());
        mVehicleManager.send(newMessage);

        // Load the next activity (selecting caravan type)
        Intent CaravanType = new Intent(this, New_Existing_Caravan.class);
        startActivity(CaravanType);
    }
}
