package com.c.zhbj74.domain;

import java.util.ArrayList;

/*
* 页签详情数据
* */
public class NewsTabBean {
    public NewsTab data;

    public class NewsTab{
        public String more;
        public ArrayList<NewsData> news;
        public ArrayList<TopNews> topnews;
    }

    //新闻列表对象
    public class NewsData{
        public int id;
        public String listimage;
        public String pubdate;
        public String title;
        public String type;
        public String url;
    }

    //头条新闻
    public class TopNews{
        public int id;
        public String topimage;
        public String pubdate;
        public String title;
        public String type;
        public String url;
    }
}
