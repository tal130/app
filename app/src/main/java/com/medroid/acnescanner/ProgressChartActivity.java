package com.medroid.acnescanner;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
//import android.app.*;
import android.app.Activity;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.medroid.acnescanner.vizualize.PieGraph;
import com.medroid.acnescanner.vizualize.PieSlice;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;


public class ProgressChartActivity extends Activity{
    private PieSlice slice;
    private PieGraph pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_chart);

        pg = (PieGraph)findViewById(R.id.graph);


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Status");
        query.fromLocalDatastore();
        query.orderByDescending("date");
        query.findInBackground(new FindCallback<ParseObject>() {
                                   @Override
                                   public void done(List<ParseObject> objects, ParseException e) {
                                       if(e==null)
                                       {
                                           HashMap<Integer,String> monthmap = new HashMap<Integer, String>();
                                           for (int i=1;i<32;i++)
                                               monthmap.put(i,"#aaaa11");
                                           for (ParseObject obj : objects) {
                                               if (obj.getDouble("precent") > 50)
                                                   monthmap.put(obj.getDate("date").getDay(),"#99CC00");
                                               else
                                                   monthmap.put(obj.getDate("date").getDay(), "#FF0000");


                                           }
                                           for (int i=1;i<32;i++)
                                           {
                                               slice = new PieSlice();
                                               slice.setColor(Color.parseColor(monthmap.get(i)));
                                               slice.setValue(1);
                                               pg.addSlice(slice);
                                           }


                                       }
                                       else
                                       {
                                           e.printStackTrace();
                                       }
                                   }
                               });


//
//        slice.setColor(Color.parseColor("#99CC00"));
//        slice.setValue(2);
//        pg.addSlice(slice);
//        slice = new PieSlice();
//        slice.setColor(Color.parseColor("#FFBB33"));
//        slice.setValue(3);
//        pg.addSlice(slice);
//        slice = new PieSlice();
//        slice.setColor(Color.parseColor("#FF0000"));
//        slice.setValue(8);
//        pg.addSlice(slice);


        //for the hole size in the middle
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
