package com.sssoyalan.newsapp.api

import android.content.Context
import com.sssoyalan.newsapp.models.ModelNews
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface Api {
    suspend fun getGeneral() : ModelNews
    suspend fun getSport() : ModelNews
    suspend fun getTech() : ModelNews
}