package com.example.kumamon.android

import android.app.Application
import android.util.Log
import com.example.kumamon.data.OaiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class KumamonApp: Application() {

    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                OaiModel.init(
                    apiKey = OaiModel.API_KEY,
                    modId = OaiModel.MODEL_ID
                )
            } catch (ex: Exception) {
                Log.d("TRACE", "problem in OaiModel.init is ${ex.message}")
            }
        }

    }
}