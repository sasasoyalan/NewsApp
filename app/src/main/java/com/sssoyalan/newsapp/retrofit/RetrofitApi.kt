package com.sssoyalan.newsapp.retrofit

import com.sssoyalan.newsapp.api.Api
import com.sssoyalan.newsapp.models.Article
import com.sssoyalan.newsapp.models.BorsaModel
import com.sssoyalan.newsapp.models.ModelNews
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface RetrofitApi {

    @GET("/v2/top-headlines?country=tr&apiKey=71719ef3b1cf43b595c1cda2531e9b19")
    suspend fun getGeneral(): ModelNews
    @GET("/v2/top-headlines?country=tr&category=sport&apiKey=71719ef3b1cf43b595c1cda2531e9b19")
    suspend fun getSport(): ModelNews
    @GET("/v2/top-headlines?country=tr&category=technology&apiKey=71719ef3b1cf43b595c1cda2531e9b19")
    suspend fun getTech(): ModelNews
    @GET("/embed/para-birimleri.json")
    suspend fun getBorsa(): BorsaModel

}