package com.sssoyalan.newsapp.source

import com.sssoyalan.newsapp.models.BorsaModel
import com.sssoyalan.newsapp.models.ModelNews
import com.sssoyalan.newsapp.api.RetrofitApi
import com.sssoyalan.newsapp.db.ArticleDatabase
import com.sssoyalan.newsapp.models.Article
import com.sssoyalan.newsapp.models.ArticleFav
import com.sssoyalan.newsapp.models.today.Today
import com.sssoyalan.newsapp.retrofit.RetrofitInstance
import com.sssoyalan.newsapp.retrofit.RetrofitInstanceBorsa
import com.sssoyalan.newsapp.retrofit.RetrofitInstanceToday
import retrofit2.Response

class DataRepository(private val db : ArticleDatabase) {

    suspend fun getNews(category: String) = RetrofitInstance.service.getGeneral(category)
    suspend fun getToday() = RetrofitInstanceToday.service.getToday()
    suspend fun getBorsa() = RetrofitInstanceBorsa.service.getBorsa()

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedNews() = db.getArticleDao().getAll()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

    fun getAll() = db.getArticleDao().getAll()
}

