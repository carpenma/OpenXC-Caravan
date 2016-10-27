package com.openxc.openxccaravan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by MCARPE53 on 10/27/2016.
 */

public class JoinSuccessDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Join request successful!")
                .setPositiveButton("Let's Go!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Open the main activity
                        Intent MainActivity = new Intent(getActivity(), MainActivity.class);
                        startActivity(MainActivity);
                    }});
        // Create the AlertDialog object and return it
        return builder.create();
    }
}