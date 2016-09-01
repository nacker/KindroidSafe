package com.nacker.kindroidsafe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.nacker.kindroidsafe.R;

public class SplashActivity extends AppCompatActivity {

    //requestWindowFeature(Window.FEATURE_NO_TITLE); 去除当前Activity头Title
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
    }
}
