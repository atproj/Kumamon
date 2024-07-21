package com.example.kumamon


class SelectResponseTypeUseCase {
    enum class Response {
        TEXT,
        IMAGE,
        TRANSLATION
    }

    private val imageCommands = setOf(
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
    private val imageWords = setOf("picture", "photo", "image", "pic")

    private val translationPrompts = setOf("Translate in ", "Please translate ", "Translate to ",
        "How do I say ", "Translate this ")


    operator fun invoke(incomingMsg: String): Response {
        if (imageCommands.count { imgCmd -> incomingMsg.contains(other = imgCmd, ignoreCase = true) } != 0 &&
            imageWords.count { imgWord -> incomingMsg.contains(other = imgWord, ignoreCase = true )} != 0) {
            return Response.IMAGE
        }

        if (translationPrompts.count { prompt -> incomingMsg.contains(other = prompt, ignoreCase = true) } != 0) return Response.TRANSLATION
        return Response.TEXT
    }
}