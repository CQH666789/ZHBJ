# ZHBJ
智慧北京，可以成功运行在android10.0版本

智慧北京问题汇总
1.隐藏标题栏程序崩溃
旧版Android中，在AndroidManifest.xml中实现：
<application android:icon="@drawable/icon"  
       android:label="@string/app_name"  
       android:theme="@android:style/Theme.NoTitleBar">
自己建的Activity继承AppCompatActivity进行上述操作的时候会崩溃或者报空指针异常，改为继承Activity就可以正常运行。或者采用与AppCompatActivity适配的标题栏隐藏方法：
在Activity的onCreate方法中，在setContentView(R.layout.activity_main);之前添加
ActionBar actionBar = getSupportActionBar();
actionBar.hide();

2.使用侧滑菜单引入SlidingFragmentActivity要注意support v7版本不高于25.3.1，否则会报错。

3.对于org.apache.http不能引用问题，在app Module的build.gradle文件中添加useLibrary'org.apache.http.legacy'
android {
    compileSdkVersion 29
    buildToolsVersion "30.0.2"
    useLibrary'org.apache.http.legacy'
对于报错java.lang.RuntimeException: Stub!，是因为target 升级到android 9.0 及以上的应用Apache HTTP client, 已经从bootClassLoader 里面移除了，解决方法除了上面的，还可以在清单文件application节点里面加上
<uses-library android:name="org.apache.http.legacy" android:required="false"/>

4.对于报错java.io.IOException: Cleartext HTTP traffic to img95.699pic.com not permitted；网址的请求不被允许，报错原因是Android 9.0是默认禁止所有http请求的（默认明文网络流量的使用为false），需要在代码中设置如以下代码才可以正常进行网络请求：
在AndroidManifest.xml中添加，效果图： 
 

5. 虚拟机调试服务器请求一直失败，报错如下Disconnected from the target VM, address: 'localhost:8603', transport: 'sock。真机调试一切正常。
服务器和真机一定要连接在同一个局域网下，不然服务器请求肯定不成功。

