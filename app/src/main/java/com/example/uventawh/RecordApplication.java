package com.example.uventawh;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

public class RecordApplication extends MultiDexApplication {

    private static RecordApplication application;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        application = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Start service
        //startService(new Intent(this, RecordService.class));
    }

    public static RecordApplication getInstance() {
        return application;
    }
}