package com.sssoyalan.newsapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sssoyalan.newsapp.source.DataRepository

class MainViewModelFactory(
    val dataRepository: DataRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(dataRepository) as T
    }
}