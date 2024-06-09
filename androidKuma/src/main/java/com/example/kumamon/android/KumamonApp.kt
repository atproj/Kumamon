package com.example.kumamon.android

import android.app.Application
import android.util.Log
import com.aallam.openai.api.file.FileSource
import com.example.kumamon.data.OaiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.aallam.openai.api.file.FileUpload
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.finetuning.FineTuningRequest
import com.aallam.openai.api.model.ModelId
import com.example.kumamon.data.OaiModel.model
import okio.buffer
import okio.source


class KumamonApp: Application() {

    companion object {
        private const val TRAINING_FILE = "training.jsonl"
    }

    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                OaiModel.init(
                    apiKey = OaiModel.API_KEY,
                    modId = OaiModel.MODEL_ID
                )
                fineTune()
            } catch (ex: Exception) {
                Log.d("TRACE", "problem in OaiModel.init is ${ex.message}")
            }
        }
    }

    private suspend fun fineTune() {
        // upload the training file
        val oaiFile = model.file(
            request = FileUpload(
                file = createFileSourceFromAssets(),
                purpose = Purpose("fine-tune")
            )
        )
        // fine tune
        val request = FineTuningRequest(
            trainingFile = oaiFile.id,
            model = ModelId(OaiModel.MODEL_ID)
        )
        model.fineTuningJob(request)
    }

    private fun createFileSourceFromAssets(): FileSource {
        // Access the asset manager
        val assetManager = applicationContext.assets

        // Open the asset as an input stream
        val inputStream = assetManager.open(TRAINING_FILE)

        // Create a Source object from the input stream
        val source = inputStream.source().buffer()

        // Create a FileSource instance
        return FileSource(
            name = TRAINING_FILE,
            source = source
        )
    }
}