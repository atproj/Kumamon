package com.example.kumamon.data

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.chatCompletionRequest
import com.aallam.openai.api.chat.chatMessage
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlin.time.Duration.Companion.seconds

interface LangMod {

    suspend fun init(apiKey: String, modId: String)

    suspend fun converse(incomingMsg: String): String

}

object OaiModel: LangMod {

    const val API_KEY = ""
    const val MODEL_ID = "gpt-3.5-turbo-1106"

    lateinit var model: OpenAI private set
    private var modelId: ModelId? = null
    private val chatMessages = mutableListOf<ChatMessage>()

    // test
    // Should be called once at application start
    override suspend fun init(apiKey: String, modId: String) {
        model = OpenAI(
            token = apiKey,
            timeout = Timeout(socket = 10.seconds)
        )
        modelId = ModelId(modId)
        receiveResponse(
            incomingMsg = "Pretend you are Kumamon, the mascot of Kumamoto in your responses."
        )
    }

    // returns response to a user input
    override suspend fun converse(incomingMsg: String): String {
        val chatResponse = receiveResponse(incomingMsg)
        return chatResponse.content?:"Sorry, I don't know"
    }

    private suspend fun receiveResponse(incomingMsg: String): ChatMessage {
        // user message
        chatMessages.add(
            chatMessage {
                role = ChatRole.User
                content = incomingMsg
            }
        )
        val request = chatCompletionRequest {
            model = modelId
            messages = chatMessages
        }
        // first response should be an introductory message from kumamon
        // subsequent messages are responses to user submissions
        val response = model.chatCompletion(request)
        val responseMsg = response.choices.first().message
        chatMessages.add(responseMsg)
        return responseMsg
    }
}
