package com.example.kumamon.android

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kumamon.SelectResponseTypeUseCase
import com.example.kumamon.data.LangMod
import com.example.kumamon.model.Chat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(private val selectResponseTypeUseCase: SelectResponseTypeUseCase,
                    private val model: LangMod,
                    private val dispatcher: CoroutineDispatcher = Dispatchers.IO): ViewModel() {

    private val _conversation = mutableStateListOf(
        Chat()  // initialize the chat
    )
    val conversation: List<Chat> get() = _conversation

    private var _errorMsg by mutableStateOf("")
    val errorMsg: String get() = _errorMsg

    private var _isLoading by mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading

    fun onSubmit(text: String) {
        viewModelScope.launch(dispatcher) {
            _conversation.add(
                Chat(message = text, fromUser = true)
            )
            try {
                when (selectResponseTypeUseCase(text)) {
                    SelectResponseTypeUseCase.Response.TEXT -> { replyWithText(text) }
                    SelectResponseTypeUseCase.Response.IMAGE -> { replyWithImage(text) }
                    SelectResponseTypeUseCase.Response.TRANSLATION -> {}
                }

            } catch (ex: Exception) {
                Log.d("TRACE", ex.message.toString())
                _errorMsg = ex.message.toString()
            }
        }
    }
    suspend fun replyWithText(text: String) {
        val reply = model.converse(text)
        delay(500)
        _conversation.add(
            Chat(message = reply, fromUser = false)
        )
    }

    suspend fun replyWithImage(text: String) {
        val imageResponse = model.replyImage(text)
        Log.d("TRACE", "replyWithImage returned prompt ${imageResponse.prompt}, imageUrl ${imageResponse.imageUrl}")
        _conversation.add(
            Chat(message = "", fromUser = false, imageUrl = imageResponse.imageUrl)
        )
    }

    private fun printList(chats: List<Chat>) {
        val sb = StringBuilder("")
        for(i in chats.indices) {
            sb.append(chats[i].message)
            if (i != chats.size-1) {
                sb.append(", ")
            }
        }
        Log.d("TRACE", "MainViewModel conversation=$sb")
    }
}