package com.sssoyalan.newsapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sssoyalan.newsapp.models.BorsaModel
import com.sssoyalan.newsapp.models.ModelNews
import com.sssoyalan.newsapp.models.modelInside
import com.sssoyalan.newsapp.retrofit.RetrofitInstance
import com.sssoyalan.newsapp.retrofit.RetrofitInstanceBorsa
import com.sssoyalan.newsapp.source.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel() : ViewModel() {

    private val dataRepository = DataRepository(RetrofitInstance.service)
    private val dataRepositoryBorsa = DataRepository(RetrofitInstanceBorsa.service)

    val aliveData = MutableLiveData<ModelNews>()
    val aliveDataBorsa = MutableLiveData<List<modelInside>>()

    fun fetchGeneral() {
        viewModelScope.launch {
            val modelNews : ModelNews = withContext(Dispatchers.IO){
                dataRepository.getGeneral()
            }
            aliveData.value=modelNews
        }
    }

    fun fetchSport() {
        viewModelScope.launch {
            val modelNews : ModelNews = withContext(Dispatchers.IO){
                dataRepository.getSport()
            }
            aliveData.value=modelNews
        }
    }

    fun fetchTech() {
        viewModelScope.launch {
            val modelNews : ModelNews = withContext(Dispatchers.IO){
                dataRepository.getTech()
            }
            aliveData.value=modelNews
        }
    }

    fun fetchBorsa() {
        viewModelScope.launch {
            val borsa : BorsaModel = withContext(Dispatchers.IO){
                dataRepositoryBorsa.getBorsa()
            }

            val list: MutableList<modelInside> = ArrayList()
            var model : modelInside = borsa.EUR
            list.add(model)
            model =borsa.USD
            list.add(model)
            model =borsa.BTC
            list.add(model)
            model =borsa.GBP
            list.add(model)
            model =borsa.BCH
            list.add(model)
            model =borsa.GA
            list.add(model)
            model =borsa.GAG
            list.add(model)
            model =borsa.ETH
            list.add(model)
            model =borsa.LTC
            list.add(model)
            model =borsa.XRP
            list.add(model)
            model =borsa.XU100
            list.add(model)
            aliveDataBorsa.value=list
        }
    }
}