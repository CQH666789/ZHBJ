package com.c.zhbj74.utils.bitmap;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * 内存缓存
 * 
 * 因为从 Android 2.3 (API Level 9)开始，垃圾回收器会更倾向于回收持有软引用或弱引用的对象，这让软引用和弱引用变得不再可靠。
 */
public class MemoryCacheUtils {
	
	//private static HashMap<String,Bitmap> mHashMap = new HashMap<String,Bitmap>();

	//private HashMap<String, SoftReference<Bitmap>> mHashMap = new HashMap<String, SoftReference<Bitmap>>();

	private LruCache<String, Bitmap> mLruCache;

	public MemoryCacheUtils() {
		long maxMemory = Runtime.getRuntime().maxMemory();//获取虚拟机分配的最大内存,默认16MB
		System.out.println("maxMemory:" + maxMemory);
		
		//LRU: least recentlly used 最近最少使用算法
		//A
		//B
		//C(最近最少使用)
		//B
		//A
		//D

		//maxSize:内存缓存上限
		mLruCache = new LruCache<String, Bitmap>((int) (maxMemory / 8)) {

			//返回单个对象占用内存的大小
			@Override
			protected int sizeOf(String key, Bitmap value) {
				//int byteCount = value.getByteCount();
				//  return getRowBytes() * getHeight();
				//计算图片占用内存大小
				int byteCount = value.getRowBytes() * value.getHeight();
				return byteCount;
			}
		};
	}

	//写缓存
	public void setMemoryCache(String url, Bitmap bitmap) {
		//		SoftReference<Bitmap> soft = new SoftReference<Bitmap>(bitmap);//用软引用包装bitmap
		//		mHashMap.put(url, soft);
		mLruCache.put(url, bitmap);
	}

	//读缓存
	public Bitmap getMemroyCache(String url) {
		//		SoftReference<Bitmap> soft = mHashMap.get(url);
		//		if (soft != null) {
		//			Bitmap bitmap = soft.get();//从软引用中取出当前对象
		//			return bitmap;
		//		}
		//
		//		return null;
		//return mHashMap.get(url);
		return mLruCache.get(url);
	}

}
