package com.example.kumamon.android

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kumamon.data.LangMod
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Chat(val message: String, val fromUser: Boolean)

class MainViewModel(private val model: LangMod,
                    private val dispatcher: CoroutineDispatcher = Dispatchers.IO): ViewModel() {

    private val conversation = mutableListOf(
        Chat("Hi, I'm Kumamon the sales minister of Kumamoto.", false)
    )
    private val _uiState = MutableStateFlow<Result<List<Chat>>>(
        Result.Success(conversation)
    )
    val uiState: StateFlow<Result<List<Chat>>> = _uiState

    private var numSubmissions = 0

    fun onSubmit(text: String) {
        numSubmissions++
        viewModelScope.launch(dispatcher) {
            conversation.add(
                Chat(message = text, fromUser = true)
            )
            printList(conversation)
            _uiState.value = Result.Success(conversation)
            try {
                //val reply = model.converse(text)
                val reply = when (numSubmissions) {
                    1 -> "I am a bear"
                    2 -> "My favorite sport is tennis"
                    3 -> "I have a couple of foods from Kumamoto I favor.  Including ramen and dumplings"
                    4 -> "Please come to Kumamoto"
                    else -> "else"
                }
                conversation.add(
                    Chat(message = reply, fromUser = false)
                )
                printList(conversation)
                _uiState.value = Result.Success(conversation)
            } catch (ex: Exception) {
                _uiState.value = Result.Failure(ex)
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
        Log.d("TRACE", "post conversation=$sb")
    }
}