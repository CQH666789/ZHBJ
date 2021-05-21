package com.c.zhbj74.utils.bitmap;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 自定义三级缓存工具类
 */
public class MyBitmapUtils {

	private NetCacheUtils mNetCacheUtils;
	private LocalCacheUtils mLocalCacheUtils;
	private MemoryCacheUtils mMemoryCacheUtils;

	public MyBitmapUtils() {
		mMemoryCacheUtils = new MemoryCacheUtils();
		mLocalCacheUtils = new LocalCacheUtils();
		mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils);

	}

	//加载图片进行展示
	public void display(ImageView imageView, String url) {
		//内存缓存: 速度很快, 不浪费流量, 优先
		Bitmap bitmap = mMemoryCacheUtils.getMemroyCache(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			System.out.println("从内存加载缓存啦!!!");
			return;
		}

		//本地缓存: 速度快, 不浪费流量, 其次
		bitmap = mLocalCacheUtils.getLocalCache(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			System.out.println("从本地加载缓存啦!!!");
			//写内存缓存
			mMemoryCacheUtils.setMemoryCache(url, bitmap);
			return;
		}

		//网络缓存: 速度慢, 浪费流量, 最后
		mNetCacheUtils.getBitmapFromNet(imageView, url);
	}

}
