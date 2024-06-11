package com.example.kumamon.data

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.chatCompletionRequest
import com.aallam.openai.api.chat.chatMessage
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI

interface LangMod {

    suspend fun init(apiKey: String, modId: String)

    suspend fun converse(incomingMsg: String): String

    suspend fun replyImage(incomingMsg: String): ImageResponse

}

data class ImageResponse(val prompt: String?=null, val imageUrl: String)

object OaiModel: LangMod {

    const val API_KEY = ""
    const val MODEL_ID = ""

    lateinit var model: OpenAI private set
    private var modelId: ModelId? = null
    private val chatMessages = mutableListOf<ChatMessage>()

    // test
    // Should be called once at application start
    override suspend fun init(apiKey: String, modId: String) {
        model = OpenAI(
            token = apiKey
        )
        modelId = ModelId(modId)
        receiveResponse(
            incomingMsg = "Pretend you are Kumamon, the mascot of Kumamoto in your responses."
        )
    }

    // returns response to a user input
    override suspend fun converse(incomingMsg: String): String {
        val chatResponse = receiveResponse(incomingMsg)
        return chatResponse.content ?: "Sorry, I am unable to answer at this time."
    }

    override suspend fun replyImage(incomingMsg: String): ImageResponse {
        val images = model.imageURL(
            creation = ImageCreation(
                prompt = incomingMsg,
                model = ModelId("dall-e-3"),
                n = 1,
                size = ImageSize.is1024x1024
            )
        )
        return ImageResponse(
            prompt = images.first().revisedPrompt,
            imageUrl = images.first().url
        )
    }

    override suspend fun replyImage(incomingMsg: String): ImageResponse {
        val images = model.imageURL(
            creation = ImageCreation(
                prompt = incomingMsg,
                model = ModelId("dall-e-3"),
                n = 1,
                size = ImageSize.is1024x1024
            )
        )
        return ImageResponse(
            prompt = images.first().revisedPrompt,
            imageUrl = images.first().url
        )
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
