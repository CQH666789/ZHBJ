package com.c.zhbj74.base.impl.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.c.zhbj74.NewsDetailActivity;
import com.c.zhbj74.R;
import com.c.zhbj74.base.BaseMenuDatailPager;
import com.c.zhbj74.domain.NewsTabBean;
import com.c.zhbj74.domain.NewsMenu;
import com.c.zhbj74.global.GlobalConstants;
import com.c.zhbj74.utils.CacheUtils;
import com.c.zhbj74.utils.PrefUtils;
import com.c.zhbj74.view.PullToRefreshListView;
import com.c.zhbj74.view.TopNewsViewPager;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.logging.LogRecord;

//页签页面对象
public class TabDetailPager extends BaseMenuDatailPager {

    private NewsMenu.NewsTabData mTabData; //单个页签的网络数据
    private TextView view;

    @ViewInject(R.id.vp_top_news)
    private TopNewsViewPager mViewPager;

    @ViewInject(R.id.indicator)
    private CirclePageIndicator mIndicator;

    @ViewInject(R.id.tv_title)
    private TextView tvTitle;

    @ViewInject(R.id.lv_list)
    private PullToRefreshListView lvList;
    private final String mUrl;
    private ArrayList<NewsTabBean.TopNews> mTopNews;
    private ArrayList<NewsTabBean.NewsData> mNewsList;
    private NewsAdapter mNewsAdapter;
    private String mMoreUrl;  //下一页数据链接

    private Handler mHandler;

    public TabDetailPager(Activity activity, NewsMenu.NewsTabData newsTabData) {
        super(activity);

        mTabData = newsTabData;

        mUrl = GlobalConstants.SERVER_URL + mTabData.url;
    }

    @Override
    public View initView() {
        /*
        view = new TextView(mActivity);
        //view.setText(mTabData.title); //此处空指针异常
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);
        */
        View view = View.inflate(mActivity, R.layout.pager_tab_detail,null);
        ViewUtils.inject(this,view);

        //给listView添加头布局
        View mHeaderView = View.inflate(mActivity,R.layout.list_item_header,null);
        ViewUtils.inject(this,mHeaderView);  //此处必须将头布局也注入
        lvList.addHeaderView(mHeaderView);

        //5.前端界面设置回调
        lvList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromServer();
            }

            @Override
            public void onLoadMore() {
                //判断是否有下一页数据
                if (mMoreUrl != null){
                    //有下一页
                    getMoreDataFromServer();
                }else {
                    //没有下一页
                    Toast.makeText(mActivity,"没有更多数据了",Toast.LENGTH_SHORT).show();
                    //没有数据时也要收起控件
                    lvList.onRefreshComplete(true);
                }
            }
        });

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int headerViewsCount = lvList.getHeaderViewsCount(); //获取头布局数量
                position = position - headerViewsCount; //需要减去头布局占位
                System.out.println("第" + position + "个被点击了");

                NewsTabBean.NewsData news = mNewsList.get(position);

                //read_ids:1101,1102,1105,1203

                String readIds = PrefUtils.getString(mActivity,"read_ids","");

                if (!readIds.contains(news.id + "")){ //只有不包含当前id,才追加，避免重复添加同一个id
                    readIds = readIds + news.id + ",";
                    PrefUtils.setString(mActivity,"read_ids",readIds);
                }

                //要将被点击的item的文字颜色改为灰色，局部刷新，view对象就是当前被点击的对象
                TextView tvTitle = view.findViewById(R.id.tv_title);
                tvTitle.setTextColor(Color.GRAY);
                //mNewsAdapter.notifyDataSetChanged(); //全局刷新，浪费性能

                //跳到新闻详情页面
                Intent intent = new Intent(mActivity, NewsDetailActivity.class);
                intent.putExtra("url",news.url);
                mActivity.startActivity(intent);
            }
        });

        return view;
    }

    //加载下一页数据
    private void getMoreDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, mMoreUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result,true);

                //收起下拉刷新控件
                lvList.onRefreshComplete(true);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                //请求失败
                error.printStackTrace();
                Toast.makeText(mActivity,msg,Toast.LENGTH_SHORT).show();

                //收起下拉刷新控件
                lvList.onRefreshComplete(false);
            }
        });
    }

    public void initData(){
        //view.setText(mTabData.title);
        String cache = CacheUtils.getCache(mUrl,mActivity);
        if (!TextUtils.isEmpty(cache)){
            processData(cache,false);
        }else {
            getDataFromServer();
        }
    }

    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, mUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result,false);

                CacheUtils.setCache(mUrl,result,mActivity);

                //收起下拉刷新控件
                lvList.onRefreshComplete(true);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                //请求失败
                error.printStackTrace();
                Toast.makeText(mActivity,msg,Toast.LENGTH_SHORT).show();

                //收起下拉刷新控件
                lvList.onRefreshComplete(false);
            }
        });
    }

    private void processData(String result,boolean isMore) {
        Gson gson = new Gson();
        NewsTabBean newsTabBean = gson.fromJson(result, NewsTabBean.class);

        String moreUrl = newsTabBean.data.more; //下一页数据链接
        if (!TextUtils.isEmpty(moreUrl)){
            mMoreUrl = GlobalConstants.SERVER_URL + moreUrl;
        }else {
            mMoreUrl = null;
        }

        if (!isMore){
            //头条新闻填充数据
            mTopNews = newsTabBean.data.topnews;
            if (mTopNews != null){
                mViewPager.setAdapter(new TopNewsAdapter());
                mIndicator.setViewPager(mViewPager);
                mIndicator.setSnap(true); //快照方式展示

                //事件要设置给Indicator
                mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        //更新头条新闻标题
                        NewsTabBean.TopNews topNews = mTopNews.get(position);
                        tvTitle.setText(topNews.title);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                //更新第一个头条新闻标题
                tvTitle.setText(mTopNews.get(0).title);

                mIndicator.onPageSelected(0);  //默认第一个选中（解决页面销毁后重新初始化时，Indicator仍然保留上次原点位置的bug）
            }

            //列表新闻
            mNewsList = newsTabBean.data.news;
            if (mNewsList!=null){
                mNewsAdapter = new NewsAdapter();
                lvList.setAdapter(mNewsAdapter);
            }

            if (mHandler == null){
                mHandler = new android.os.Handler(){
                    @SuppressLint("HandlerLeak")
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        int currentItem = mViewPager.getCurrentItem();
                        currentItem++;
                        if (currentItem > mTopNews.size()-1){
                            currentItem = 0; //如果已经到了最后一个页面，跳到第一页
                        }

                        mViewPager.setCurrentItem(currentItem);
                        //保证启动自动轮播逻辑只执行一次
                        mHandler.sendEmptyMessageDelayed(0,3000); //继续发送延时3秒的消息，形成内循环
                    }
                };

                mHandler.sendEmptyMessageDelayed(0,3000); //发送延时3秒的消息

                mViewPager.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                //停止广告自动轮播
                                //删除handler的所有消息
                                mHandler.removeCallbacksAndMessages(null);
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //在主线程运行
                                    }
                                });
                                break;
                            case MotionEvent.ACTION_CANCEL: //取消事件，当按下viewpager后，直接滑动listview,导致抬起事件无法响应
                                //启动广告
                                mHandler.sendEmptyMessageDelayed(0,3000); //发送延时3秒的消息
                                break;
                            case MotionEvent.ACTION_UP:
                                //启动广告
                                mHandler.sendEmptyMessageDelayed(0,3000); //发送延时3秒的消息
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
            }
        }else {
            //加载更多数据
            ArrayList<NewsTabBean.NewsData> moreNews = newsTabBean.data.news;
            mNewsList.addAll(moreNews); //将数据追加在原来的集合中
            //刷新listview
            mNewsAdapter.notifyDataSetChanged();
        }

    }

    //头条新闻数据适配器
    class TopNewsAdapter extends PagerAdapter{

        private final BitmapUtils mBitmapUtils;

        public TopNewsAdapter(){
            mBitmapUtils = new BitmapUtils(mActivity);
            mBitmapUtils.configDefaultLoadingImage(R.drawable.topnews_item_default); //设置加载中的默认图片
        }

        @Override
        public int getCount() {
            return mTopNews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = new ImageView(mActivity);
            //view.setImageResource(R.drawable.topnews_item_default);
            view.setScaleType(ImageView.ScaleType.FIT_XY); //设置图片缩放方式，宽高填充父控件

            String imageUrl = mTopNews.get(position).topimage; //图片下载链接

            String[] sImageUrl = imageUrl.split("/");
            int length = sImageUrl.length;
            String eImageUrl = GlobalConstants.SERVER_URL + "/" + sImageUrl[length - 2] + "/" + sImageUrl[length - 1];


            //下载图片——将图片设置给imageview——避免内存溢出——缓存
            //BitmapUtils-XUtils
            mBitmapUtils.display(view,eImageUrl);

            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }

    class NewsAdapter extends BaseAdapter{

        private BitmapUtils mBitmapUtils;

        public NewsAdapter(){
            mBitmapUtils = new BitmapUtils(mActivity);
            mBitmapUtils.configDefaultLoadingImage(R.drawable.news_pic_default);
        }

        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public NewsTabBean.NewsData getItem(int position) {
            return mNewsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                convertView = View.inflate(mActivity,R.layout.list_item_news,null);
                holder = new ViewHolder();
                holder.ivIcon = convertView.findViewById(R.id.iv_icon);
                holder.tvTitle = convertView.findViewById(R.id.tv_title);
                holder.tvDate = convertView.findViewById(R.id.tv_date);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            NewsTabBean.NewsData news = getItem(position);
            holder.tvTitle.setText(news.title);
            holder.tvDate.setText(news.pubdate);

            //根据本地记录来标记已读未读
            String readIds = PrefUtils.getString(mActivity,"read_ids","");
            if (readIds.contains(news.id + "")){
                holder.tvTitle.setTextColor(Color.GRAY);
            }else {
                holder.tvTitle.setTextColor(Color.BLACK);
            }

            String[] sImageUrl = news.listimage.split("/");
            int length = sImageUrl.length;
            String eImageUrl = GlobalConstants.SERVER_URL + "/" + sImageUrl[length - 2] + "/" + sImageUrl[length - 1];

            mBitmapUtils.display(holder.ivIcon,eImageUrl);
            return convertView;
        }
    }

    static class ViewHolder{
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvDate;
    }
}
