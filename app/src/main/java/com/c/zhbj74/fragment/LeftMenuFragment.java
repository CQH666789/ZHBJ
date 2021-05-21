package com.c.zhbj74.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.c.zhbj74.MainActivity;
import com.c.zhbj74.R;
import com.c.zhbj74.base.impl.NewsCenterPager;
import com.c.zhbj74.domain.NewsMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

public class LeftMenuFragment extends BaseFragment {

    @ViewInject(R.id.lv_list)
    private ListView lvList;

    private ArrayList<NewsMenu.NewsMenuData> mNewMenuData; //侧边栏网络数据对象

    private int mCurrentPos; //当前被选中的item的位置
    private LeftMenuAdapter mAdapter;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_left_menu, null);
        //lvList = view.findViewById(R.id.lv_list);
        ViewUtils.inject(this,view); //注入view和事件
        return view;
    }

    @Override
    public void initData() {

    }

    //给侧边栏设置数据
    public void setMenuData(ArrayList<NewsMenu.NewsMenuData> data){
        mCurrentPos = 0; //当前选中的位置归零
        //更新页面
        mNewMenuData = data;
        mAdapter = new LeftMenuAdapter();

        lvList.setAdapter(mAdapter);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentPos = position; //更新当前被选中的位置
                mAdapter.notifyDataSetChanged(); //刷新listview

                //收起侧边栏
                toggle();

                //侧边栏点击后，修改新闻中心的FragmentLayout中的内容
                setCurrentDetailPager(position);
            }
        });
    }

    //设置当前菜单详情页
    private void setCurrentDetailPager(int position) {
        //获取新闻中心的对象
        MainActivity mainUI = (MainActivity) mActivity;
        //
        ContentFragment fragment = mainUI.getContentFragment();

        NewsCenterPager newsCenterPager = fragment.getNewsCenterPager();

        newsCenterPager.setCurrentDetailPager(position);
    }

    /*
* 打开或者关闭侧边栏
* */
    private void toggle() {
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        slidingMenu.toggle(); //如果当前状态是开，调用后就关；反之亦然
    }

    class LeftMenuAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mNewMenuData.size();
        }

        @Override
        public Object getItem(int position) {
            return mNewMenuData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(mActivity, R.layout.list_item_left_menu, null);
            TextView tvMenu = view.findViewById(R.id.tv_menu);
            NewsMenu.NewsMenuData item = (NewsMenu.NewsMenuData) getItem(position);
            tvMenu.setText(item.title);

            if (position == mCurrentPos){
                tvMenu.setEnabled(true); //文字变为红色
            }else {
                tvMenu.setEnabled(false); //文字变为白色
            }

            return view;
        }
    }
}
