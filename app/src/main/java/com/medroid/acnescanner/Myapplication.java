package com.medroid.acnescanner;


import android.app.Application;

import com.parse.Parse;
import com.facebook.FacebookSdk;

/**
 * Created by tal on 13/12/2015.
 */
public class Myapplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


        // [Optional] Power your app with Local Datastore. For more info, go to
        // https://parse.com/docs/android/guide#local-datastore
        Parse.enableLocalDatastore(this);

        Parse.initialize(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
