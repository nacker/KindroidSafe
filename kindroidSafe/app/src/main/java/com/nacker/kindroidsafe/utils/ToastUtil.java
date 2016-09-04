package com.nacker.kindroidsafe.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by nacker on 16/9/2.
 */
public class ToastUtil {

    /**
     * @param ctx    上下文环境
     * @param msg    打印文本内容
     */
    public static void show(Context ctx, String msg) {
        Toast.makeText(ctx,msg,0).show();
    }


}
