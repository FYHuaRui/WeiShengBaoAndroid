package com.fangyuan.weipanbao.ui;

import android.app.Application;

import com.yolanda.nohttp.NoHttp;

/**
 * Created by Administrator on 2016/12/22.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NoHttp.initialize(this);
    }
}
