package com.medroid.acnescanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class ComapreActivity extends AppCompatActivity {

    private TouchImageView today;
    private SeekBar mSeekBar;
    private TouchImageView yesterday;
    private Spinner spinner;
    private ArrayList<String> spinnerArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comapre);


        today = (TouchImageView) findViewById(R.id.up);
        yesterday = (TouchImageView) findViewById(R.id.down);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinnerArray = new ArrayList<String>();

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
                        setImage(yesterday, objects.get(0).getString("path"));
                    } else {
                        setImage(yesterday, objects.get(1).getString("path"));
                    }

                    today.setRotation(-90);
                    yesterday.setRotation(-90);

                    //set spinner values
                    for (ParseObject obj : objects) {
                        spinnerArray.add(obj.getString("date").substring(0, 10));
                    }
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
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
                //does not work. need to debug. check if its reach here
                Toast.makeText(getApplicationContext(), (String)parent.getItemAtPosition(position),Toast.LENGTH_LONG);
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
                    setProgressBarColor(mSeekBar, Color.rgb(
                            255 - (255 / 100 * (100 - progress * 2)),
                            255, 0));

                } else {
                    setProgressBarColor(mSeekBar, Color.rgb(255,
                            255 - (255 / 100 * (progress - 50) * 2), 0));

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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

    }


    public void setImage(TouchImageView image, String path)
    {
        Bitmap bmp = BitmapFactory.decodeFile(path);
        image.setImageBitmap(bmp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comapre, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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
}
