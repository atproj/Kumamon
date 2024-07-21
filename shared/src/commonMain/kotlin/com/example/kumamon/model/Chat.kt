package com.example.kumamon.model
data class Chat(val message: String = "Hi, I'm Kumamon, minister of tourism and happiness in " +
        "Kumamoto Prefecture.  To translate to japanese, ask 'Please translate...'." +
        "  To see an example, ask 'Show me...'",
                val fromUser: Boolean = false,
                val imageUrl: String?=null,
                val enableDictation: Boolean = false)
