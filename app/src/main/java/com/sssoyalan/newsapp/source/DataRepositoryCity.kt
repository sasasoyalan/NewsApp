package com.sssoyalan.newsapp.source

import android.content.Context
import com.sssoyalan.newsapp.api.CityApi
import com.sssoyalan.newsapp.models.city.Cities
import com.sssoyalan.newsapp.models.city.CityResponseItem

class DataRepositoryCity(private val cityApi : CityApi) {

    suspend fun getCities(context: Context) : Cities {
        return cityApi.getCities(context)
    }
}