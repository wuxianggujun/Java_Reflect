package com.wuxianggujun.reflection;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * bug测试类
 */
public class BugClass {

    public BugClass(Context context){
        Toast.makeText(context,"你很优秀！但代码任然有bug?",Toast.LENGTH_SHORT).show();
    }
}

