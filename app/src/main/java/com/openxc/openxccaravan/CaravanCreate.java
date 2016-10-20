package com.openxc.openxccaravan;

import android.app.Activity;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.openxcplatform.openxccaravan.R;

public class CaravanCreate extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caravan_create);

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
                }
            }
        });
    }
    public void StartCaravan(View view) {

    }
}
