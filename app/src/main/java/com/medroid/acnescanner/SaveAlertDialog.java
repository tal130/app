package com.medroid.acnescanner;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.Date;

/**
 * Created by tal on 17/12/2015.
 * alert Dialog
 */
public class SaveAlertDialog extends DialogFragment {
    private String m_Text = "";
    double precentage= 0.0;
    private Activity act= null;

    public SaveAlertDialog() {super();}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        precentage = getArguments().getDouble("precentage");
        act = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle("Save").setMessage("save daily progress?");

// Set up the input
        final EditText input = new EditText(act);
        input.setHint("enter text");
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                ParseObject parse = new ParseObject("Status");
                parse.put("date", new Date());
                parse.put("precent", precentage);
                parse.put("reason", m_Text);


                parse.pinInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    StartAct();


                                }
                            });
                            Log.i("save precentage", "");
                        } else {
                            Log.e(e.getMessage(), "");
                        }
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();
    }


    public void StartAct()
    {
        Toast toast = Toast.makeText(act, "saved " + m_Text, Toast.LENGTH_LONG);
        toast.show();

    }

}


