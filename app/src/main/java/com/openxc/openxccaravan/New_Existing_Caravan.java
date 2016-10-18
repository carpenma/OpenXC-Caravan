package com.openxc.openxccaravan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.openxcplatform.openxccaravan.R;

public class New_Existing_Caravan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__existing__caravan);
    }

    public void NewCaravan(View view) {
        // Goto Caravan Creation Activity
    }

    public void ExistingCaravan(View view) {
        // Goto Available Caravans Activity
    }
}
