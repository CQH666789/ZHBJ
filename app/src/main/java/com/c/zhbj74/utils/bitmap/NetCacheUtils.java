package com.c.zhbj74.utils.bitmap;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * 网络缓存工具类
 */
public class NetCacheUtils {

	private LocalCacheUtils localCacheUtils;
	private MemoryCacheUtils memoryCacheUtils;

	public NetCacheUtils(LocalCacheUtils localCacheUtils,
			MemoryCacheUtils memoryCacheUtils) {
		this.localCacheUtils = localCacheUtils;
		this.memoryCacheUtils = memoryCacheUtils;
	}

	public void getBitmapFromNet(ImageView imageView, String url) {
		//异步下载图片
		new BitmapTask().execute(imageView, url);

	}

	class BitmapTask extends AsyncTask<Object, Void, Bitmap> {

		private ImageView imageView;
		private String url;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Bitmap doInBackground(Object... params) {
			imageView = (ImageView) params[0];
			url = (String) params[1];

			imageView.setTag(url);//给当前ImageView打标签

			//使用url下载图片
			Bitmap bitmap = download(url);

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				//给ImageView设置图片
				//由于ListView的重用机制,导致某个item有可能展示它所重用的那个item的图片, 导致图片错乱
				//解决方案:确保当前设置的图片和当前显示的imageview完全匹配
				String url = (String) imageView.getTag();//获取和当前ImageView绑定个url
				if (this.url.equals(url)) {//判断当前下载的图片的url是否和imageView的url一致, 如果一致,说明图片正确
					imageView.setImageBitmap(result);
					System.out.println("从网络下载图片啦!!!");

					//写本地缓存
					localCacheUtils.setLocalCache(url, result);
					//写内存缓存
					memoryCacheUtils.setMemoryCache(url, result);
				}
			}
		}

	}

	//使用url下载图片
	public Bitmap download(String url) {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(6000);
			conn.setReadTimeout(6000);

			conn.connect();

			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				InputStream in = conn.getInputStream();
				//使用输入流生成Bitmap对象
				Bitmap bitmap = BitmapFactory.decodeStream(in);
				return bitmap;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return null;
	}

}
