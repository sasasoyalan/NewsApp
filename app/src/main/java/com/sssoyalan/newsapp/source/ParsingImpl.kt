package com.sssoyalan.newsapp.source

import android.content.Context
import android.content.res.AssetManager
import com.google.gson.Gson
import com.sssoyalan.newsapp.api.CityApi
import com.sssoyalan.newsapp.models.city.Cities

import java.net.URL

class ParsingImpl : CityApi {

    override suspend fun getCities(context: Context): Cities {
        fun AssetManager.readFile(fileName: String) = open(fileName)
            .bufferedReader()
            .use { it.readText() }
        val jsonString = context.assets.readFile("city_list.json")

        return Cities(Gson().fromJson(jsonString, Cities::class.java).cities)
    }
}