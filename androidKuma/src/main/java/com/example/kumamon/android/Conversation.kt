package com.example.kumamon.android

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Conversation(messages: List<Chat>, onSend: (String)->Unit) {
    var currentMsg by rememberSaveable { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            BottomAppBar {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = currentMsg,
                        onValueChange = { newMessage -> currentMsg = newMessage },
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        onSend(currentMsg)
                        currentMsg = ""
                    }) {
                        Text("Send")
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            items(messages) { message ->
                ChatBubble(message)
            }
        }
    }
}

@Composable
fun ChatBubble(chat: Chat) {
    val backgroundColor = if (chat.fromUser) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.surface
    val alignment = if (chat.fromUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = alignment
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = backgroundColor,
            shadowElevation = 4.dp,
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = chat.message,
                color = if (chat.fromUser) Color.White else Color.Black,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}