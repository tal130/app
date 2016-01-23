package com.medroid.acnescanner;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/**
 * Created by tal on 16/12/2015.
 */
public class SaveDialogBox extends DialogFragment{
    double precentage= 0.0;

    private Activity act= null;
        public SaveDialogBox(){super();}

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //return super.onCreateDialog(savedInstanceState);
            precentage = getArguments().getDouble("precentage");
            act = getActivity();


            // Create handleMessage function


            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            //message need to taken from SQL
            builder.setTitle("Save")
                    .setMessage("save daily progress?")
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ParseObject parse = new ParseObject("Status");
                            parse.put("date", new Date());
                            parse.put("precent",precentage);

                            parse.pinInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e==null)
                                    {
                                        act.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast toast = Toast.makeText(act, "saved", Toast.LENGTH_LONG);
                                                toast.show();
                                            }
                                        });
                                        Log.i("save precentage","");
                                    }
                                    else
                                    {
                                        Log.e(e.getMessage(),"");
                                    }
                                }
                            });
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                        }
                    });/*.setNeutralButton("", new OnClickListener() { //todo nice to have a automatic save option

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //run youtube
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(printurl)));

                }
            });*/




            return builder.create();
        }


    }

