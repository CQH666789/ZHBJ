package com.c.zhbj74.base.impl.menu;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.c.zhbj74.MainActivity;
import com.c.zhbj74.R;
import com.c.zhbj74.base.BaseMenuDatailPager;
import com.c.zhbj74.domain.NewsMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;

//菜单详情页——新闻
/*
* viewPagerIndicator使用流程
* 1.引入库
* 2.解决support-v4冲突，让2个版本一致（针对eclipse）
* 3.从例子程序拷贝布局文件
* 4.从例子程序拷贝相关代码（指示器和viewpager绑定；重写getPageTitle，返回标题）
* 5.在清单文件中增加样式
* 6.背景修改为白色
* 7.修改样式-背景样式&文字样式
* */
public class NewsMenuDetailPager extends BaseMenuDatailPager implements ViewPager.OnPageChangeListener {

    @ViewInject(R.id.vp_news_menu_detail)
    private ViewPager mViewPager;

    @ViewInject(R.id.indicator)
    private TabPageIndicator mIndicator;

    private ArrayList<NewsMenu.NewsTabData> mTabData; //网签网络数据
    private ArrayList<TabDetailPager> mPagers; //页签页面集合

    public NewsMenuDetailPager(Activity activity, ArrayList<NewsMenu.NewsTabData> children) {
        super(activity);
        mTabData = children;
    }

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager_news_menu_detail,null);
        ViewUtils.inject(this,view);
        return view;
    }

    public void initData(){
        //初始化网签
        mPagers = new ArrayList<>();
        for (int i = 0; i < mTabData.size(); i++) {
            TabDetailPager pager = new TabDetailPager(mActivity,mTabData.get(i));
            mPagers.add(pager);
        }

        mViewPager.setAdapter(new NewsMenuDetailAdapter());
        mIndicator.setViewPager(mViewPager);  //将viewpager和指示器绑定在一起，注意：必须在viewpager设置完之后再绑定

        //设置页面滑动监听
        //mViewPager.setOnPageChangeListener(this);
        mIndicator.setOnPageChangeListener(this); //此处必须给指示器设置页面监听，不能设置给viewpager
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0){
            //开启侧边栏
            setSlidingMenuEnable(true);
        }else {
            //禁用侧边栏
            setSlidingMenuEnable(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //开启或禁用侧边栏
    private void setSlidingMenuEnable(boolean enable) {
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        if (enable){
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        }else {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }

    class NewsMenuDetailAdapter extends PagerAdapter{

        //指定指示器标题
        @Override
        public CharSequence getPageTitle(int position) {
            NewsMenu.NewsTabData data = mTabData.get(position);
            return data.title;
        }

        @Override
        public int getCount() {
            return mPagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TabDetailPager pager = mPagers.get(position);

            View view = pager.mRootView;
            container.addView(view);

            pager.initData();

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @OnClick(R.id.btn_next)
    public void nextPage(View view){
        //跳到下个页面
        int currentItem = mViewPager.getCurrentItem();
        currentItem++;
        mViewPager.setCurrentItem(currentItem);
    }
}
