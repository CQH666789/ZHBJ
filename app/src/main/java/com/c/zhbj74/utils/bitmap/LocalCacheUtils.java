package com.c.zhbj74.utils.bitmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.itcast.zhbj.utils.MD5Encoder;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * 本地缓存工具类
 */
public class LocalCacheUtils {

	String PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/zhbj_cache/";//缓存文件夹

	//写缓存
	public void setLocalCache(String url, Bitmap bitmap) {
		//将图片保存在本地文件
		File dir = new File(PATH);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();//创建文件夹
		}

		try {
			File cacheFile = new File(dir, MD5Encoder.encode(url));//创建本地文件, 以url的md5命名
			//将图片压缩保存在本地; 参1:图片格式, 参2:压缩比(0-100),100表示不压缩; 参3:输出流
			bitmap.compress(CompressFormat.JPEG, 100, new FileOutputStream(
					cacheFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//读缓存
	public Bitmap getLocalCache(String url) {
		try {
			File cacheFile = new File(PATH, MD5Encoder.encode(url));

			if (cacheFile.exists()) {
				//缓存存在
				Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(
						cacheFile));
				return bitmap;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
