package com.medroid.acnescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.medroid.acnescanner.vizualize.feedback;

/**
 * Created by tal on 30/06/2015.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getSupportActionBar().setTitle("Acne Scanner");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Handle presses on the action bar items
        switch (id) {
            case R.id.action_massage:
                openChartActivity();
                return true;
            case R.id.action_feedback:
                openFeedbackActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openChartActivity() {
        //show massages from people who want to join
//        Bundle bundle=new Bundle();
//        bundle.putString("type","message");
        Intent intent = new Intent(this, ProgressChartActivity.class);
//        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void openFeedbackActivity() {
        Intent intent = new Intent(this, feedback.class);
        startActivity(intent);
    }



    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();        //to call onCreateOptionsMenu function every time i return to activity to update the alert icon
    }

}
