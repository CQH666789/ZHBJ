package com.c.zhbj74.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
/*
* 头条新闻自定义viewpager
* */
public class TopNewsViewPager extends ViewPager {

    private int startX;
    private int startY;

    public TopNewsViewPager(Context context) {
        super(context);
    }

    public TopNewsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*
    * 1.上下滑动不拦截
    * 2.向右滑动并且当前是第一个页面，需要拦截
    * 3.向左滑动并且当前是最后一个页面，需要拦截
    * */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX = (int) ev.getX();
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int endX = (int)ev.getX();
                int endY = (int) ev.getY();

                int dx = endX - startX;
                int dy = endY - startY;

                if (Math.abs(dy) < Math.abs(dx)){
                    int currentItem = getCurrentItem();
                    //左右滑动
                    if (dx > 0){
                        //向右划
                        if (currentItem == 0){
                            //第一个页面，需要拦截
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }else {
                        //向左划
                        int count = getAdapter().getCount();
                        if (currentItem == count - 1){
                            //最后一个页面，需要拦截
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }
                }else {
                    //上下滑动，不拦截
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
        }

        return super.dispatchTouchEvent(ev);
    }
}
