package com.sssoyalan.newsapp.retrofit

import com.sssoyalan.newsapp.api.RetrofitApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val client = OkHttpClient.Builder().build()

    private val builder  = Retrofit.Builder()
        .baseUrl("https://free-news.p.rapidapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val service: RetrofitApi = builder.create(RetrofitApi::class.java)

}