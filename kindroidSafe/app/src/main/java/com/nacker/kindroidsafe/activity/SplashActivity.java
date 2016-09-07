package com.nacker.kindroidsafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nacker.kindroidsafe.R;
import com.nacker.kindroidsafe.utils.StreamUtil;
import com.nacker.kindroidsafe.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



/**
 * Created by nacker on 16/9/4.
 */
public class SplashActivity extends Activity {
    protected static final String tag = "SplashActivity";
    /**
     * 更新新版本的状态码
     */
    protected static final int UPDATE_VERSION = 100;
    /**
     * 进入应用程序主界面状态码
     */
    protected static final int ENTER_HOME = 101;

    /**
     * url地址出错状态码
     */
    protected static final int URL_ERROR = 102;
    protected static final int IO_ERROR = 103;
    protected static final int JSON_ERROR = 104;

    private TextView textView;

    private int mLocalVersionCode;
    private String mVersionDes;
    private String mDownloadUrl;


    RelativeLayout rl_root;

    private Handler mHandler = new Handler(){
        @Override
        // alt+ctrl+向下箭头,向下拷贝相同代码
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    //弹出对话框,提示用户更新
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    //进入应用程序主界面,activity跳转过程
                    enterHome();
                    break;
                case URL_ERROR:
                    ToastUtil.show(getApplicationContext(), "url异常");
                    enterHome();
                    break;
                case IO_ERROR:
                    ToastUtil.show(getApplicationContext(), "读取异常");
                    enterHome();
                    break;
                case JSON_ERROR:
                    ToastUtil.show(getApplicationContext(), "json解析异常");
                    enterHome();
                    break;
            }
        }
    };

    //requestWindowFeature(Window.FEATURE_NO_TITLE); 去除当前Activity头Title
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getSupportActionBar() != null){
//            getSupportActionBar().hide();
//        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        x.view().inject(this);

        // 初始化UI
        initUI();

        // 初始化数据
        initData();

        // 初始化动画
        initAnimation();
    }

    /**
     * 添加淡入的动画效果
     */
    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(3000);

        rl_root.startAnimation(alphaAnimation);
    }

    // 初始化UI
    private void initUI(){
        textView = (TextView)findViewById(R.id.tv_version_name);

        rl_root = (RelativeLayout) findViewById(R.id.rl_root);


    }
    // 初始化数据
    private void initData(){
        // 1.应用版本名称
        textView.setText("版本名称:"+getVersionName());

        // 检测(本地版本号和服务器版本号比对)是否有更新,如果有更新,提示用户下载(member)
        // 2.获取本地版本号
        mLocalVersionCode = getVersionCode();

        // 3.获取服务器版本号(客户端发请求,服务端给响应,(json,xml))
        //http://www.oxxx.com/update74.json?key=value  返回200 请求成功,流的方式将数据读取下来
        //json中内容包含:
		/* 更新版本的版本名称
		 * 新版本的描述信息
		 * 服务器版本号
		 * 新版本apk下载地址*/
        checkVersion();
    }

    /**
     * 弹出对话框,提示用户更新
     */
    protected void showUpdateDialog() {
        // 对话框,是依赖Activity存在的
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置左上角的图标
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("版本更新");
        // 设置描述内容
        builder.setMessage(mVersionDes);

        // 立即更新
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 下载APK
                downloadApk();
            };
        });

        // 稍后在说
        builder.setNegativeButton("稍后在说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 取消对话框,进入主界面
                enterHome();

            }
        });

        //点击取消事件监听
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //即使用户点击取消,也需要让其进入应用程序主界面
                enterHome();
                dialog.dismiss();
            }
        });

        builder.show();
    }

    protected void downloadApk() {
        // apk下载地址,放置apk的所在路径

        // 1.判断sd卡是否可用,是否挂在上
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            // 2.获取sdk路径
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "kindroidSafe.apk";

            //3,发送请求,获取apk,并且放置到指定路径
            RequestParams params = new RequestParams(mDownloadUrl);
            //设置断点续传
            params.setAutoResume(true);
            params.setSaveFilePath(path);

//            params.setSaveFilePath(Environment.getExternalStorageDirectory()+"/app");


            x.http().post(params, new Callback.ProgressCallback<File>() {


                @Override
                public void onSuccess(File result) {

                    Log.i(tag,"下载成功---");
                    File file = result;

                    // 提示用户安装
                    installApk(file);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.i(tag,"下载失败---");
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }

                @Override
                public void onWaiting() {

                }

                @Override
                public void onStarted() {

                }

                /**
                 * @param total 下载APK总大小
                 * @param current 当前下载位置
                 * @param isDownloading 是否正在下载
                 */
                @Override
                public void onLoading(long total, long current, boolean isDownloading) {

                    Log.i(tag,"下载中---");

                    Log.i("nacker","current<<"+current + "total<<"+total);
                }
            });

        }
    }


    /**
     * 安装APK
     * @param file 文件路径
     */
    protected void installApk(File file) {
        //系统应用界面,源码,安装apk入口
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
		/*//文件作为数据源
		intent.setData(Uri.fromFile(file));
		//设置安装的类型
		intent.setType("application/vnd.android.package-archive");*/

        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        //startActivity(intent);
        startActivityForResult(intent, 0);
    }

    /**
     * 开启一个Activity后,返回结果调用方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 进入应用主界面
    protected void enterHome() {
        Intent intent = new Intent(this, HomeAcivity.class);
        startActivity(intent);
        //在开启一个新的界面后,将导航界面关闭(导航界面只可见一次)
        finish();
    }

    private void checkVersion() {
        new Thread(){
            public void run() {
                // 发送请求获取数据,参数则为请求json的地址
                // http://192.168.13.99:8080/update.json
                // 仅限于模拟器访问电脑tomcat
                Message msg = Message.obtain();
                // 获取现在的时间戳
                long startTime = System.currentTimeMillis();

                try {
                    // 1.url地址
                    URL url = new URL("http://10.0.2.2:8080/update.json");
                    // 2.开启一个链接
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    // 3.设置超时

                    // 请求超时
                    connection.setConnectTimeout(2000);
                    // 读取超时
                    connection.setReadTimeout(2000);

                    // 默认就是get请求方式
//                    connection.setRequestMethod("POST");
                    // 4.获取请求成功相应码
                    if (connection.getResponseCode() == 200){
                        // 以流的形式,将数据获取下来
                        InputStream is = connection.getInputStream();
                        // 将流转成字符串(工具类)
                        String json = StreamUtil.streamToString(is);
                        Log.i(tag, json);

                        // 解析JSOn
                        JSONObject jsonObject = new JSONObject(json);

                        //debug调试,解决问题
                        String versionName = jsonObject.getString("versionName");
                        mVersionDes = jsonObject.getString("versionDes");
                        String versionCode = jsonObject.getString("versionCode");
                        mDownloadUrl = jsonObject.getString("downloadUrl");

                        //日志打印
                        Log.i(tag, versionName);
                        Log.i(tag, mVersionDes);
                        Log.i(tag, versionCode);
                        Log.i(tag, mDownloadUrl);

                        //8,比对版本号(服务器版本号>本地版本号,提示用户更新)
                        if(mLocalVersionCode<Integer.parseInt(versionCode)){
                            //提示用户更新,弹出对话框(UI),消息机制
                            msg.what = UPDATE_VERSION;
                        }else{
                            //进入应用程序主界面
                            msg.what = ENTER_HOME;
                        }
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what = URL_ERROR;

                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = IO_ERROR;

                }catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = JSON_ERROR;

                }finally {
                    //指定睡眠时间,请求网络的时长超过4秒则不做处理
                    //请求网络的时长小于4秒,强制让其睡眠满4秒钟
                    long endTime = System.currentTimeMillis();
                    if (endTime-startTime<4000){
                        try {
                            Thread.sleep(4000-(endTime-startTime));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                }

            };
        }.start();

    }


    /**
     * 返回版本号
     * @return
     * 非0 则代表获取成功
     */
    private int getVersionCode() {
        //1,包管理者对象packageManager
        PackageManager pm = getPackageManager();
        //2,从包的管理者对象中,获取指定包名的基本信息(版本名称,版本号),传0代表获取基本信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            //3,获取版本名称
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取版本名称:清单文件中
     * @return	应用版本名称	返回null代表异常
     */
    private String getVersionName(){
        //1,包管理者对象packageManager
        PackageManager pm = getPackageManager();
        //2,从包的管理者对象中,获取指定包名的基本信息(版本名称,版本号),传0代表获取基本信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            //3,获取版本名称
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
