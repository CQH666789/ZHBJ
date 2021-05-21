package com.c.zhbj74.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.c.zhbj74.MainActivity;
import com.c.zhbj74.base.BaseMenuDatailPager;
import com.c.zhbj74.base.BasePager;
import com.c.zhbj74.base.impl.menu.InteractMenuDetailPager;
import com.c.zhbj74.base.impl.menu.NewsMenuDetailPager;
import com.c.zhbj74.base.impl.menu.PhotosMenuDetailPager;
import com.c.zhbj74.base.impl.menu.TopicMenuDetailPager;
import com.c.zhbj74.domain.NewsMenu;
import com.c.zhbj74.fragment.LeftMenuFragment;
import com.c.zhbj74.global.GlobalConstants;
import com.c.zhbj74.utils.CacheUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;

public class NewsCenterPager extends BasePager {
    private ArrayList<BaseMenuDatailPager> mMenuDetailPagers; //菜单详情页集合
    private NewsMenu mNewsData;  //分类信息网络数据

    public NewsCenterPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        System.out.println("新闻中心初始化啦");
        /*
        TextView view = new TextView(mActivity);
        view.setText("新闻中心");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);

        flContent.addView(view);
        */
        tvTitle.setText("新闻");

        //显示菜单按钮
        btnMenu.setVisibility(View.VISIBLE);

        /*
        * 先判断有没有缓存，如果有的话，就加载缓存
        * */
        String cache = CacheUtils.getCache(GlobalConstants.CATEGORY_URL, mActivity);
        if (!TextUtils.isEmpty(cache)){
            System.out.println("发现缓存啦。。。");
            processData(cache);
        }else {
            getDataFromServer ();
        }
    }

    //请求服务器获取数据（需要权限）
    //开源框架：XUtils
    private void getDataFromServer () {
        
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, GlobalConstants.CATEGORY_URL, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                //请求成功
                String result = responseInfo.result; //获取服务器返回结果
                System.out.println("服务器返回结果" + result);
                
                processData(result);

                //写缓存
                CacheUtils.setCache(GlobalConstants.CATEGORY_URL,result,mActivity);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                //请求失败
                error.printStackTrace();
                Toast.makeText(mActivity,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    //解析数据
    protected void processData(String json) {
        Gson gson = new Gson();
        mNewsData = gson.fromJson(json, NewsMenu.class);

        System.out.println("解析结果" + mNewsData);

        //获取侧边栏对象
        MainActivity mainUI = (MainActivity) mActivity;
        LeftMenuFragment fragment = mainUI.getLeftMenuFragment();

        //给侧边栏设置数据
        fragment.setMenuData(mNewsData.data);

        //初始化4个菜单详情页
        mMenuDetailPagers = new ArrayList<>();
        mMenuDetailPagers.add(new NewsMenuDetailPager(mActivity,mNewsData.data.get(0).children));
        mMenuDetailPagers.add(new TopicMenuDetailPager(mActivity));
        mMenuDetailPagers.add(new PhotosMenuDetailPager(mActivity,btnPhoto));
        mMenuDetailPagers.add(new InteractMenuDetailPager(mActivity));

        //将新闻菜单详情页设置为默认页面
        setCurrentDetailPager(0);
    }

    //设置菜单详情页
    public void setCurrentDetailPager(int position){
        //重新给frameLayout添加内容
        BaseMenuDatailPager pager = mMenuDetailPagers.get(position); //获取当前应该显示的页面
        View view = pager.mRootView;  //当前页面的布局

        //清楚之前旧的布局
        flContent.removeAllViews();

        flContent.addView(view);  //给帧布局添加布局

        //初始化数据
        pager.initData();

        //更新标题
        tvTitle.setText(mNewsData.data.get(position).title);

        //如果是组图页面，需要显示切换按钮
        if (pager instanceof PhotosMenuDetailPager){
            btnPhoto.setVisibility(View.VISIBLE);
        }else {
            //隐藏切换按钮
            btnPhoto.setVisibility(View.GONE);
        }
    }
}
