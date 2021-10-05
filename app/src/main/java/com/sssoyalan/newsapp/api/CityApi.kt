package com.sssoyalan.newsapp.api


import android.content.Context
import com.sssoyalan.newsapp.models.city.Cities
import com.sssoyalan.newsapp.models.city.CityResponseItem

interface CityApi {
    suspend fun getCities(context: Context) : Cities
}