package com.medroid.acnescanner;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
//import android.app.*;
import android.app.Activity;
import android.view.animation.AccelerateDecelerateInterpolator;


public class ProgressChartActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_chart);

        PieGraph pg = (PieGraph)findViewById(R.id.graph);
        PieSlice slice = new PieSlice();
        slice.setColor(Color.parseColor("#99CC00"));
        slice.setValue(2);
        pg.addSlice(slice);
        slice = new PieSlice();
        slice.setColor(Color.parseColor("#FFBB33"));
        slice.setValue(3);
        pg.addSlice(slice);
        slice = new PieSlice();
        slice.setColor(Color.parseColor("#AA66CC"));
        slice.setValue(8);
        pg.addSlice(slice);


        //for the hole in the middle
        pg.setInnerCircleRatio(150);


        //for animation
//        for (PieSlice s : pg.getSlices())
//            s.setGoalValue((float)Math.random() * 10);
//        pg.setDuration(1000);//default if unspecified is 300 ms
//        pg.setInterpolator(new AccelerateDecelerateInterpolator());//default if unspecified is linear; constant speed
//
//        pg.animateToGoalValues();

//        setFragment();

    }


    // This could be moved into an abstract BaseActivity
    // class for being re-used by several instances
//    protected void setFragment() {
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        PieFragment hello = new PieFragment();
//        fragmentTransaction.add(R.id.FragmentContainer, hello, "HELLO");
//        fragmentTransaction.commit();
//    }
}
