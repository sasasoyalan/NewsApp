//package com.sssoyalan.newsapp.source
//
//import com.google.gson.Gson
//import com.sssoyalan.newsapp.api.Api
//import com.sssoyalan.newsapp.models.ModelNews
//import okhttp3.*
//
//import java.net.URL
//
//class ParsingImpl : Api {
//    override suspend fun getGeneral(): ModelNews  {
//        val result = URL("https://newsapi.org/v2/top-headlines?country=tr&category=sport&apiKey=71719ef3b1cf43b595c1cda2531e9b19").readText()
//        return ModelNews(
//            Gson().fromJson(result, ModelNews::class.java).articles,
//            Gson().fromJson(result, ModelNews::class.java).status,
//            Gson().fromJson(result, ModelNews::class.java).totalResults
//        )
//    }
//
//    override suspend fun getSport(): ModelNews  {
//        val response = khttp.get("https://newsapi.org/v2/top-headlines?country=tr&category=sport&apiKey=71719ef3b1cf43b595c1cda2531e9b19").toString()
//
//        return ModelNews(
//            Gson().fromJson(response, ModelNews::class.java).articles,
//            Gson().fromJson(response, ModelNews::class.java).status,
//            Gson().fromJson(response, ModelNews::class.java).totalResults
//        )
//    }
//
//    override suspend fun getTech(): ModelNews  {
//        val response = khttp.get("https://newsapi.org/v2/top-headlines?country=tr&category=technology&apiKey=71719ef3b1cf43b595c1cda2531e9b19").toString()
//
//        return ModelNews(
//            Gson().fromJson(response, ModelNews::class.java).articles,
//            Gson().fromJson(response, ModelNews::class.java).status,
//            Gson().fromJson(response, ModelNews::class.java).totalResults
//        )
//    }
//
//}