package com.c.zhbj74;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.c.zhbj74.utils.PrefUtils;

import java.util.ArrayList;

public class GuideActivity extends Activity {

    private ViewPager mViewPager;

    private ArrayList<ImageView> mImageViewList;

    private int[] mImageIds = new int[] {R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
    private LinearLayout llContainer;
    private ImageView ivRedPoint;
    private int mPointDis;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);

        mViewPager = (ViewPager) findViewById(R.id.vp_guide);
        llContainer = findViewById(R.id.ll_container);
        ivRedPoint = findViewById(R.id.iv_red_point);
        btnStart = findViewById(R.id.btn_start);
        initData();
        mViewPager.setAdapter(new GuideAdapter());
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                //当页面滑动过程中的回调
                int leftMargin = (int) (mPointDis * v) + i * mPointDis;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivRedPoint.getLayoutParams();
                params.leftMargin = leftMargin;
                //重新设置布局参数
                ivRedPoint.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int i) {
                //某个页面被选中
                if (i == mImageViewList.size() - 1){
                    btnStart.setVisibility(View.VISIBLE);
                }else {
                    btnStart.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                //页面状态发生变化的回调

            }
        });

        //计算两个圆点的距离
        //移动距离=第二个圆点left值 - 第一个圆点left值
        //measure->layout（确定位置）->draw(activity的onCreate方法执行结束之后才会走此流程)
        //mPointDis = llContainer.getChildAt(1).getLeft() - llContainer.getChildAt(0).getLeft();

        ivRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //移除监听，避免重复回调
                ivRedPoint.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                //layout方法执行结束的回调
                mPointDis = llContainer.getChildAt(1).getLeft() - llContainer.getChildAt(0).getLeft();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrefUtils.setBoolean(getApplicationContext(), "is_first_enter", false);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    private void initData() {
        mImageViewList = new ArrayList<>();
        for (int i = 0; i < mImageIds.length; i++){
            ImageView view = new ImageView(this);
            view.setBackgroundResource(mImageIds[i]);
            mImageViewList.add(view);

            ImageView point = new ImageView(this);
            point.setImageResource(R.drawable.shape_point_gray);
            //初始化布局参数，宽高包裹内容，父控件是谁，就是谁声明的布局参数
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if (i>0){
                //从第二个点开始设置左边距
                params.leftMargin = 10;
            }
            point.setLayoutParams(params);
            llContainer.addView(point);


        }
    }

    class GuideAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mImageViewList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView view = mImageViewList.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            //super.destroyItem(container, position, object);
        }
    }
}