package com.medroid.acnescanner;

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
import android.widget.SeekBar;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class ComapreActivity extends AppCompatActivity {

    private TouchImageView today;
    private SeekBar mSeekBar;
    private TouchImageView yesterday;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comapre);


        today = (TouchImageView) findViewById(R.id.up);
        yesterday = (TouchImageView) findViewById(R.id.down);


        ParseQuery<ParseObject> query = ParseQuery.getQuery("images");
        query.fromLocalDatastore();
        query.orderByDescending("updatedAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {

                    Log.i("today path : ", objects.get(0).getString("path"));
                    Bitmap bmp = BitmapFactory.decodeFile(objects.get(0).getString("path"));
                    Bitmap bmp2;
                    if (objects.size() == 1) {
                        bmp2 = BitmapFactory.decodeFile(objects.get(0).getString("path"));
                    } else {
                        bmp2 = BitmapFactory.decodeFile(objects.get(1).getString("path"));
                    }


                    today.setImageBitmap(bmp);
                    yesterday.setImageBitmap(bmp2);


                    today.setRotation(-90);
                    yesterday.setRotation(-90);


                } else {
                    e.printStackTrace();
                }
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
}
