package com.sssoyalan.newsapp.retrofit

import com.sssoyalan.newsapp.models.borsa.BorsaModel
import com.sssoyalan.newsapp.models.news.ModelNews
import com.sssoyalan.newsapp.models.weather.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitApi {

    @GET("/v2/top-headlines?country=tr&apiKey=71719ef3b1cf43b595c1cda2531e9b19")
    suspend fun getGeneral(@Query("category") category: String): Response<ModelNews>
    @GET("/data/2.5/weather?&appid=7c9ee817ca3e3c8cf3534ca2f2e2e0b0&lang=tr")
    suspend fun getWeather(@Query("lat") lat : String?,
                           @Query("lon") lon : String?,
                           @Query("q") city : String?): Response<WeatherResponse>
    @GET("/embed/para-birimleri.json")
    suspend fun getBorsa(): BorsaModel

}