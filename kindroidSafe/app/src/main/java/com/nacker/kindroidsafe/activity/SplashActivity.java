package com.nacker.kindroidsafe.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;

import com.nacker.kindroidsafe.R;

public class SplashActivity extends AppCompatActivity {

    private TextView textView;

    //requestWindowFeature(Window.FEATURE_NO_TITLE); 去除当前Activity头Title
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getSupportActionBar() != null){
//            getSupportActionBar().hide();
//        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        // 初始化UI
        initUI();

        // 初始化数据
        initData();
    }

    // 初始化UI
    private void initUI(){
        textView = (TextView)findViewById(R.id.tv_version_name);


    }
    // 初始化数据
    private void initData(){
        // 应用版本名称
        textView.setText("版本名称:"+getVersionName());
    }

    /**
     * 获取版本名称
     * @return 应用版本名称
     */
    private int getVersionName(){
        // 1.包管理者对象
        PackageManager pm = getPackageManager();
        // 2.获取指定包名的基本信息(版本名称,版本号)
        try {
           PackageInfo packageInfo = pm.getPackageInfo(getPackageName(),0);
            // 3.获取版本名称
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
