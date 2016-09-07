package com.nacker.kindroidsafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nacker.kindroidsafe.R;

/**
 * Created by nacker on 16/9/4.
 */
public class HomeAcivity extends Activity {

    private GridView gv_home;
    private String[] mTitleStrs;
    private int[] mDrawableIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        initUI();

        // 初始化数据方法
        initData();
    }

    private void initData() {

        mTitleStrs = new String[]{"手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};
        mDrawableIds = new int[]{
            R.drawable.home_apps, R.drawable.home_safe,
                R.drawable.home_apps, R.drawable.home_safe,
                R.drawable.home_apps, R.drawable.home_safe,
                R.drawable.home_netmanager, R.drawable.home_settings,
                R.drawable.home_callmsgsafe,
        };

        gv_home.setAdapter(new MyAdapter());
    }

    private void initUI() {

        gv_home = (GridView) findViewById(R.id.gv_home);

    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // 条目的总数 文字组数 == 图片张数
            return mTitleStrs.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitleStrs[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(),R.layout.gridview_item,null);

            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title);

            tv_title.setText(mTitleStrs[position]);
            iv_icon.setBackgroundResource(mDrawableIds[position]);

            return view;
        }
    }
}
