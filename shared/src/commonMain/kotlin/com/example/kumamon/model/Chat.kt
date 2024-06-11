package com.example.kumamon.model
data class Chat(val message: String = "Hi, I am Kumamon, the lovable black bear mascot of Kumamoto." +
        " My mission is to bring happiness and positivity to everyone I meet." +
        " If you have any questions about Kumamoto or just need a little pick-me-up, I'm here for you." +
                "Here are a few tips: \n "+
        "- Translate in japanese \"Hello! good morning \" \n"+
        "- Show me a picture of ... "
                ,
                val fromUser: Boolean = false,
                val imageUrl: String?=null)
