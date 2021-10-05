package com.sssoyalan.newsapp.retrofit

import com.sssoyalan.newsapp.util.Constant
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val client = OkHttpClient.Builder().build()

    private val builder  = Retrofit.Builder()
        .baseUrl(Constant.BASE_URL_NEWS)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
    val service: RetrofitApi = builder.create(RetrofitApi::class.java)

    private val builderBorsa  = Retrofit.Builder()
        .baseUrl(Constant.BASE_URL_BORSA)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
    val serviceBorsa: RetrofitApi = builderBorsa.create(RetrofitApi::class.java)

    private val builderWeather  = Retrofit.Builder()
        .baseUrl(Constant.BASE_URL_WEATHER)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
    val serviceWeather: RetrofitApi = builderWeather.create(RetrofitApi::class.java)

}