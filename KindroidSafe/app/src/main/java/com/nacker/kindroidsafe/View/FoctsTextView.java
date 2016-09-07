package com.nacker.kindroidsafe.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 能够获取焦点的自定义TextView
 * Created by nacker on 16/9/7.
 */
public class FoctsTextView extends TextView {
    //使用在通过java代码创建控件
    public FoctsTextView(Context context) {
        super(context);
    }

    //由系统调用(带属性+上下文环境构造方法)
    public FoctsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //由系统调用(带属性+上下文环境构造方法+布局文件中定义样式文件构造方法)
    public FoctsTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //重写获取焦点的方法,由系统调用,调用的时候默认就能获取焦点
    @Override
    public boolean isFocused() {
        return true;
    }

}
