package com.c.zhbj74.base;

import android.app.Activity;
import android.content.pm.LauncherApps;
import android.view.View;

/*
* 菜单详情页基类
* */
public abstract class BaseMenuDatailPager {
    public Activity mActivity;
    public View mRootView;

    public BaseMenuDatailPager(Activity activity){
        mActivity = activity;
        mRootView = initView();
    }

    //初始化布局，必须子类实现
    public abstract View initView();

    //初始化数据
    public void initData(){

    }
}
