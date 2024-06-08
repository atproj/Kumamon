package com.example.kumamon.android

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kumamon.data.LangMod
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Chat(val message: String, val fromUser: Boolean)
class MainViewModel(private val model: LangMod,
                    private val dispatcher: CoroutineDispatcher = Dispatchers.IO): ViewModel() {

    private val _conversation = mutableStateListOf(
            Chat("Hi, I'm Kumamon the sales minister of Kumamoto.", false)
    )
    val conversation: List<Chat> get() = _conversation

    private var _errorMsg by mutableStateOf("")
    val errorMsg: String get() = _errorMsg

    private var _isLoading by mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading

    private var numSubmissions = 0

    fun onSubmit(text: String) {
        numSubmissions++
        viewModelScope.launch(dispatcher) {
            _conversation.add(
                Chat(message = text, fromUser = true)
            )
            try {
                val reply = model.converse(text)
                delay(500)
                _conversation.add(
                    Chat(message = reply, fromUser = false)
                )
            } catch (ex: Exception) {
                _errorMsg = ex.message.toString()
            }
        }
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