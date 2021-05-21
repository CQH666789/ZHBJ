package com.c.zhbj74.base.impl.menu;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.c.zhbj74.R;
import com.c.zhbj74.base.BaseMenuDatailPager;
import com.c.zhbj74.domain.PhotosBean;
import com.c.zhbj74.global.GlobalConstants;
import com.c.zhbj74.utils.CacheUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

//菜单详情页——组图
public class PhotosMenuDetailPager extends BaseMenuDatailPager implements View.OnClickListener {

    @ViewInject(R.id.lv_photo)
    private ListView lvPhoto;
    @ViewInject(R.id.gv_photo)
    private GridView gvPhoto;
    private ArrayList<PhotosBean.PhotoNews> mNewsList;
    private ImageButton btnPhoto;

    public PhotosMenuDetailPager(Activity activity, ImageButton btnPhoto) {
        super(activity);

        btnPhoto.setOnClickListener(this); //组图切换按钮设置点击事件
        this.btnPhoto = btnPhoto;
    }

    @Override
    public View initView() {
        View view = View.inflate(mActivity,R.layout.paper_photos_menu_detail,null);
        ViewUtils.inject(this,view);
        return view;
    }

    public void initData(){
        String cache = CacheUtils.getCache(GlobalConstants.PHOTO_URL,mActivity);
        if (!TextUtils.isEmpty(cache)){
            processData(cache);
        }
        getDataFromServer();
    }

    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, GlobalConstants.PHOTO_URL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result);

                CacheUtils.setCache(GlobalConstants.PHOTO_URL,result,mActivity);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                error.printStackTrace();
                Toast.makeText(mActivity,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processData(String result) {
        Gson gson = new Gson();
        PhotosBean photosBean = gson.fromJson(result,PhotosBean.class);

        mNewsList = photosBean.data.news;
        lvPhoto.setAdapter(new PhotoAdapter());
        gvPhoto.setAdapter(new PhotoAdapter()); //gridview的布局结构和listview完全一致，所以可以共用一个adapter
    }

    private boolean isListView = true; //标记当前是否是listview展示
    @Override
    public void onClick(View v) {
        if (isListView){
            //切成gridview
            lvPhoto.setVisibility(View.GONE);
            gvPhoto.setVisibility(View.VISIBLE);
            btnPhoto.setImageResource(R.drawable.icon_pic_list_type);

            isListView = false;
        }else {
            //切成listview
            lvPhoto.setVisibility(View.VISIBLE);
            gvPhoto.setVisibility(View.GONE);
            btnPhoto.setImageResource(R.drawable.icon_pic_grid_type);

            isListView = true;
        }
    }

    class PhotoAdapter extends BaseAdapter{

        private final BitmapUtils mBitmapUtils;

        public PhotoAdapter(){
            mBitmapUtils = new BitmapUtils(mActivity);
            mBitmapUtils.configDefaultLoadingImage(R.drawable.pic_item_list_default);
        }

        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public PhotosBean.PhotoNews getItem(int position) {
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
                convertView = View.inflate(mActivity,R.layout.list_item_photos,null);
                holder = new ViewHolder();
                holder.ivPic = convertView.findViewById(R.id.iv_pic);
                holder.tvTitle = convertView.findViewById(R.id.tv_title);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            PhotosBean.PhotoNews item = getItem(position);
            holder.tvTitle.setText(item.title);

            String imageUrl = item.listimage; //图片下载链接

            String[] sImageUrl = imageUrl.split("/");
            int length = sImageUrl.length;
            String eImageUrl = GlobalConstants.SERVER_URL + "/" + sImageUrl[length - 3] + "/" + sImageUrl[length - 2] + "/" + sImageUrl[length - 1];

            mBitmapUtils.display(holder.ivPic,eImageUrl);

            return convertView;
        }
    }

    static class ViewHolder{
        public ImageView ivPic;
        public TextView tvTitle;
    }
}
