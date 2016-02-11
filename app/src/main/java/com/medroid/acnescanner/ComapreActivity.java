package com.medroid.acnescanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class ComapreActivity extends BaseActivity{

    private TouchImageView today;
    private SeekBar mSeekBar;
    private TouchImageView yesterday;
    private Spinner spinner;
    private ArrayList<String> spinnerArray;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comapre);

        // Obtain the shared Tracker instance.
        Myapplication application = (Myapplication) getApplication();
        mTracker = application.getDefaultTracker();

        today = (TouchImageView) findViewById(R.id.up);
        yesterday = (TouchImageView) findViewById(R.id.down);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinnerArray = new ArrayList<>();

        spinner.setVisibility(View.INVISIBLE);

        Toast.makeText(this, "Set progress bar for better or worse", Toast.LENGTH_LONG).show();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("images");
        query.fromLocalDatastore();
        query.orderByDescending("date");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {

                    Log.i("today path : ", objects.get(0).getString("path"));
                    setImage(today, objects.get(0).getString("path"));
                    if (objects.size() == 1) {
                        yesterday.setImageResource(R.drawable.avatar_new);
                    } else {
                        setImage(yesterday, objects.get(1).getString("path"));
                    }

                    today.setRotation(-90);
                    yesterday.setRotation(-90);

                    //set spinner values
                    List<ParseObject> temp = objects.subList(1,objects.size());
//                    for(int i=1;i<objects.size();i++)
                    for (ParseObject obj : temp) {
                        spinnerArray.add(obj.getString("date").substring(0, 10));
                    }
                    if (spinnerArray.size() == 0)
                        spinner.setVisibility(View.INVISIBLE);
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, spinnerArray){

                        @Override
                        public boolean isEnabled(int position) {
                            return position != 1;
                        }

                        @Override
                        public boolean areAllItemsEnabled() {
                            return false;
                        }

                        @Override   //set the color of the text in the spinner
                        public View getDropDownView(int position, View convertView, ViewGroup parent){
                            View v = convertView;
                            if (v == null) {
                                Context mContext = this.getContext();
                                LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                v = vi.inflate(R.layout.spinner_item, null);
                            }

                            TextView tv = (TextView) v.findViewById(R.id.temp);
                            tv.setText(spinnerArray.get(position));
                            tv.setTextColor(Color.BLACK);
                            return v;
                        }
                    };
                    //selected item will look like a spinner set from XML
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerArrayAdapter);

                } else {
                    e.printStackTrace();
                }
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("images");
                query.fromLocalDatastore();
                query.whereContains("date", (String) parent.getItemAtPosition(position));
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            setImage(yesterday, objects.get(0).getString("path"));
                            yesterday.setRotation(-90);
                        }
                        else
                        {
                            e.printStackTrace();
                        }

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSeekBar = (SeekBar)findViewById(R.id.seekBar);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                if (progress <= 50) {
                    setProgressBarColor(mSeekBar, Color.rgb(255,
                            255 - (255 / 100 * (50 - progress) * 2), 0));

                } else {
                    setProgressBarColor(mSeekBar, Color.rgb(
                            255 - (255 / 100 * ( progress * 2 - 100)),
                            255, 0));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SaveAlertDialog save= new SaveAlertDialog();
                FragmentTransaction ft = getFragmentManager().beginTransaction();

                // Supply num input as an argument.
                Bundle args = new Bundle();
                args.putDouble("precentage", progressChanged);
                save.setArguments(args);


                save.show(ft, "dialog");

            }
        });


    }

    /**
     * this method is used to change the color of the seek bar
     *
     * @param seakBar
     *            the seekbar whose color has to be changed
     * @param newColor
     *            the color which has to be changed
     */
    public void setProgressBarColor(SeekBar seakBar, int newColor) {
        LayerDrawable ld = (LayerDrawable) seakBar.getProgressDrawable();
        ClipDrawable d1 = (ClipDrawable) ld
                .findDrawableByLayerId(R.id.progressshape);
        d1.setColorFilter(newColor, PorterDuff.Mode.SRC_IN);


        LayerDrawable ld1 = (LayerDrawable) seakBar.getProgressDrawable();
        ClipDrawable d12 = (ClipDrawable) ld1
                .findDrawableByLayerId(R.id.background);
        d12.setColorFilter(newColor, PorterDuff.Mode.SRC_IN);
    }


    public void setImage(TouchImageView image, String path)
    {
        Bitmap bmp = BitmapFactory.decodeFile(path);
        image.setImageBitmap(bmp);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.menu_comapre, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onBackPressed() {
        //close the application.
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTracker.setScreenName("CompareActivity~");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
