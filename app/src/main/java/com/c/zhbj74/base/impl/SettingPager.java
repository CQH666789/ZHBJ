package com.c.zhbj74.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.c.zhbj74.base.BasePager;

public class SettingPager extends BasePager {

    public SettingPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        System.out.println("设置初始化啦");
        TextView view = new TextView(mActivity);
        view.setText("设置");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);

        flContent.addView(view);
        tvTitle.setText("设置");

        //隐藏菜单按钮
        btnMenu.setVisibility(View.GONE);
    }
}
