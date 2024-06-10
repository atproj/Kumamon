package com.example.kumamon.android

import android.util.Log

class SelectResponseTypeUseCase {
    enum class Response {
        TEXT,
        IMAGE,
        TRANSLATION
    }
    //todo: how would this scale for internationalization without injecting domain specific deps?
    val imageCommands = setOf(
        "Show me ",
        "Show an ",
        "Show a ",
        "Show some ",
        "Show off some ",
        "Show off a ",
        "Show off an ",
        "Display a ",
        "Display an ",
        "Display some "
    )
    val imageWords = setOf("picture", "photo", "image", "pic")


    operator fun invoke(incomingMsg: String): Response {
        val containsCommand = imageCommands.count { imgCmd -> incomingMsg.contains(other = imgCmd, ignoreCase = true) } != 0
        Log.d("TRACE", "containsCommand=$containsCommand")
        val containsWord = imageWords.count { imgWord -> incomingMsg.contains(other = imgWord, ignoreCase = true )} != 0
        Log.d("TRACE", "containsWord=$containsWord")
        if (imageCommands.count { imgCmd -> incomingMsg.contains(other = imgCmd, ignoreCase = true) } != 0 &&
            imageWords.count { imgWord -> incomingMsg.contains(other = imgWord, ignoreCase = true )} != 0) {
            return Response.IMAGE
        }
        return Response.TEXT
    }
}