package com.sssoyalan.newsapp.api

import com.sssoyalan.newsapp.models.Article
import com.sssoyalan.newsapp.models.BorsaModel
import com.sssoyalan.newsapp.models.ModelNews
import com.sssoyalan.newsapp.models.today.Today
import com.sssoyalan.newsapp.models.weather.WeatherResponse
import com.sssoyalan.newsapp.ui.fragments.WeatherFragment
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitApi {

    @GET("/v2/top-headlines?country=tr&apiKey=71719ef3b1cf43b595c1cda2531e9b19")
    suspend fun getGeneral(@Query("category") category: String): Response<ModelNews>
    @GET("/data/2.5/weather?&appid=7c9ee817ca3e3c8cf3534ca2f2e2e0b0&lang=tr")
    suspend fun getWeather(@Query("lat") lat : String,
                           @Query("lon") lon : String): Response<WeatherResponse>
    @GET("/embed/para-birimleri.json")
    suspend fun getBorsa(): BorsaModel

}