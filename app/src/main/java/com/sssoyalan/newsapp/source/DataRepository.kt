package com.sssoyalan.newsapp.source

import com.sssoyalan.newsapp.models.BorsaModel
import com.sssoyalan.newsapp.models.ModelNews
import com.sssoyalan.newsapp.retrofit.RetrofitApi

class DataRepository(private val api: RetrofitApi) {

    suspend fun getGeneral() : ModelNews {
        return api.getGeneral()
    }
    suspend fun getSport() : ModelNews {
        return api.getSport()
    }
    suspend fun getTech() : ModelNews {
        return api.getTech()
    }
    suspend fun getBorsa() : BorsaModel {
        return api.getBorsa()
    }
}

