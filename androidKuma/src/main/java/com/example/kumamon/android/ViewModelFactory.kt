package com.example.kumamon.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kumamon.data.OaiModel

class ViewModelFactory: ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(model = OaiModel) as T
        }
        throw IllegalArgumentException("${modelClass.simpleName} is not a recognized viewmodel")
    }
}