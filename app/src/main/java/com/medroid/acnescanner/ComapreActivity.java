package com.medroid.acnescanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.ParseException;
import java.util.List;


public class ComapreActivity extends ActionBarActivity {

    private TouchImageView today;
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
                    Bitmap bmp2 = null;
                    if(objects.size() == 1)
                    {
                        bmp2 = BitmapFactory.decodeFile(objects.get(0).getString("path"));
                    }
                    else {
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
