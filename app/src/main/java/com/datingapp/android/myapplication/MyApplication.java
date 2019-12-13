package com.datingapp.android.myapplication;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.datingapp.android.common.Common;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 *
 * Application of App
 * */
public class MyApplication extends MultiDexApplication {

    private static MyApplication mInstance;

    public void onCreate(){
        super.onCreate();

        FirebaseApp.initializeApp(this);
        // line used for store the firebase data offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Common.myApp = this;
        mInstance = this;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

}
