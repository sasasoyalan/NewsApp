package com.sssoyalan.newsapp.source

import com.sssoyalan.newsapp.db.ArticleDatabase
import com.sssoyalan.newsapp.models.news.Article
import com.sssoyalan.newsapp.retrofit.RetrofitInstance

class DataRepository(private val db : ArticleDatabase) {

    suspend fun getNews(category: String) = RetrofitInstance.service.getGeneral(category)
    suspend fun getWeather(lat : String?, lon : String?, city : String?) = RetrofitInstance.serviceWeather.getWeather(lat,lon,city)
    suspend fun getBorsa() = RetrofitInstance.serviceBorsa.getBorsa()

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedNews() = db.getArticleDao().getAll()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

    fun getAll() = db.getArticleDao().getAll()
}

