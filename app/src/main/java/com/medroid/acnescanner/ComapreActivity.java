package com.medroid.acnescanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class ComapreActivity extends ActionBarActivity {

    private ImageView today;
    private ImageView yesterday;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comapre);

        Bundle extras = getIntent().getExtras();

        String todayPath = extras.getString("today","TODO");
        String yesterdayPath = extras.getString("yesterday","TODO");

        today = (ImageView) findViewById(R.id.up);
        yesterday = (ImageView) findViewById(R.id.down);

        Bitmap bmp = BitmapFactory.decodeFile(todayPath);
        Bitmap bmp2 = BitmapFactory.decodeFile(yesterdayPath);

        today.setImageBitmap(bmp);
        yesterday.setImageBitmap(bmp2);

        today.setRotation(-90);
        yesterday.setRotation(-90);
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
