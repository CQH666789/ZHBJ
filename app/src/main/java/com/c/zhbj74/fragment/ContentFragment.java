package com.c.zhbj74.fragment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.c.zhbj74.MainActivity;
import com.c.zhbj74.R;
import com.c.zhbj74.base.BasePager;
import com.c.zhbj74.base.impl.GovAffairsPager;
import com.c.zhbj74.base.impl.HomePager;
import com.c.zhbj74.base.impl.NewsCenterPager;
import com.c.zhbj74.base.impl.SettingPager;
import com.c.zhbj74.base.impl.SmartServicePager;
import com.c.zhbj74.view.NoScrollViewPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


import java.util.ArrayList;

public class ContentFragment extends BaseFragment {

    private NoScrollViewPager mViewPager;
    private ArrayList<BasePager> mPagers; //五个标签页集合
    private RadioGroup rgGroup;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_content,null);
        mViewPager = view.findViewById(R.id.vp_content);
        rgGroup = view.findViewById(R.id.rg_group);
        return view;
    }

    @Override
    public void initData() {
        mPagers = new ArrayList<>();

        mPagers.add(new HomePager(mActivity));
        mPagers.add(new NewsCenterPager(mActivity));
        mPagers.add(new SmartServicePager(mActivity));
        mPagers.add(new GovAffairsPager(mActivity));
        mPagers.add(new SettingPager(mActivity));

        mViewPager.setAdapter(new ContentAdapter());

        //底栏标签切换监听
        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb_home:
                        //首页
                        mViewPager.setCurrentItem(0,false);
                        break;
                    case R.id.rb_news:
                        //新闻中心
                        mViewPager.setCurrentItem(1,false);
                        break;
                    case R.id.rb_smart:
                        //智慧服务
                        mViewPager.setCurrentItem(2,false);
                        break;
                    case R.id.rb_gov:
                        //政务
                        mViewPager.setCurrentItem(3,false);
                        break;
                    case R.id.rb_setting:
                        //设置
                        mViewPager.setCurrentItem(4,false);
                        break;
                    default:
                        break;
                }
            }

        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BasePager pager = mPagers.get(position);
                pager.initData();

                if (position == 0 || position == mPagers.size() - 1){
                    //首页和设置禁用侧边栏
                    setSlidingMenuEnable(false);
                }else {
                    //其他页面开启侧边栏
                    setSlidingMenuEnable(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //手动加载第一页数据
        mPagers.get(0).initData();
        //首页禁用
        setSlidingMenuEnable(false);
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

    class ContentAdapter extends PagerAdapter{

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
            BasePager pager = mPagers.get(position);
            View view = pager.mRootView;  //获取当前页面对象的布局

            //pager.initData();  //初始化数据，viewPager会默认加载下一个页面，为了节省流量和性能，不要在此处调用初始化数据的方法
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    //获取新闻中心页面
    public NewsCenterPager getNewsCenterPager(){
        NewsCenterPager pager = (NewsCenterPager) mPagers.get(1);

        return pager;
    }
}
