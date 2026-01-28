package com.example.cycle.model

data class SteamNewsResponse(
    val appnews: AppNews
)

data class AppNews(
    val appid: Int,
    val newsitems: List<NewsItem>,
    val count: Int
)

data class NewsItem(
    val gid: String,
    val title: String,
    val url: String,
    val is_external_url: Boolean,
    val author: String,
    val contents: String,
    val feedlabel: String,
    val date: Long,
    val feedname: String,
    val feed_type: Int,
    val appid: Int
)
