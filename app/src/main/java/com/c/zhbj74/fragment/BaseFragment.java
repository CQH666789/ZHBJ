package com.c.zhbj74.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.c.zhbj74.R;

public abstract class BaseFragment extends Fragment {

    public Activity mActivity;

    //Fragment创建
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();  //获取当前fragment所依赖的activity对象
    }

    //初始化fragment布局
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = initView();
        // Inflate the layout for this fragment
        return view;
    }

    //fragment所依赖的activity的onCreate方法执行结束
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //初始化数据
        initData();
    }

    //初始化布局，必须由子类实现
    public abstract View initView();
    //初始化数据，必须由子类实现
    public abstract void initData();
}