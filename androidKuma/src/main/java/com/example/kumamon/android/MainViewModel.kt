package com.example.kumamon.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kumamon.SelectResponseTypeUseCase
import com.example.kumamon.data.LangMod
import com.example.kumamon.model.Chat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val selectResponseTypeUseCase: SelectResponseTypeUseCase,
                    private val model: LangMod,
                    private val dispatcher: CoroutineDispatcher = Dispatchers.IO): ViewModel() {

    //private val messages = mutableListOf(Chat())
    private var messages = listOf(Chat())
    private val _conversation = MutableStateFlow<ResultState<List<Chat>>>(
        ResultState.Success.NonEmpty(messages)
    )
    val conversation: StateFlow<ResultState<List<Chat>>> = _conversation

    fun onSubmit(outgoingMsg: String) {
        viewModelScope.launch(dispatcher) {
            // create a new list or stateflow won't recognize a change to emit
            messages = messages + Chat(outgoingMsg, true)
            _conversation.value = ResultState.Success.NonEmpty(messages)

            // utilize a use case to determine a reply that is appended to the conversation
            try {
                when (selectResponseTypeUseCase(outgoingMsg)) {
                    SelectResponseTypeUseCase.Response.TEXT -> { replyWithText(outgoingMsg) }
                    SelectResponseTypeUseCase.Response.IMAGE -> { replyWithImage(outgoingMsg) }
                    SelectResponseTypeUseCase.Response.TRANSLATION -> { replyWithTranslation(outgoingMsg) }
                }

            } catch (ex: Exception) {
                _conversation.value = ResultState.Failure(ex)
            }
        }
    }
    suspend fun replyWithText(outgoingMsg: String) {
        messages = messages + Chat(getTextReply(outgoingMsg), false)
        _conversation.value = ResultState.Success.NonEmpty(messages)
    }

    suspend fun replyWithImage(text: String) {
        // you've determined this is a request for an image.  pass in the conversational context
        // first message in a conversation is the greeting followed by the request for a reply
        val imageResponse = model.replyImage(messages[messages.size-2].message)
        messages = messages + Chat(message = "", fromUser = false, imageUrl = imageResponse.imageUrl)
        _conversation.value = ResultState.Success.NonEmpty(messages)
    }

    suspend fun replyWithTranslation(userMsg: String) {
        messages = messages + Chat(getTextReply(userMsg), false, enableDictation = true)
        _conversation.value = ResultState.Success.NonEmpty(messages)
    }

    suspend fun getTextReply(userMsg: String): String {
        val reply = model.converse(userMsg)
        delay(500)
        return reply
    }
}