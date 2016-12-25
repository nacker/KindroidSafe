package com.nacker.kindroidsafe.activity;

import android.app.Application;

import org.xutils.x;

/**
 * Created by nacker on 2016/12/25.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
    }
}
