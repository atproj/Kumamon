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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.kumamon.model.Chat
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Conversation(viewModel: MainViewModel) {
    var currentMsg by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
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
                    val softKeyboard = LocalSoftwareKeyboardController.current
                    Button(onClick = {
                        viewModel.onSubmit(currentMsg)
                        currentMsg = ""
                        softKeyboard?.hide()
                        coroutineScope.launch {
                            listState.animateScrollToItem(viewModel.conversation.size-1)
                        }
                    }) {
                        Text("Send")
                    }
                }
            }
        }
    ) { innerPadding ->
        if (viewModel.isLoading) {
            // Loading State for Reply
        } else {
            Log.d("TRACE", "Entering succcess state")
            printList(viewModel.conversation)

            if (viewModel.errorMsg.isBlank()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .padding(innerPadding)
                ) {
                    items(viewModel.conversation) { message ->
                        ChatBubble(message)
                    }
                }
            } else {
                // append error message and retry to bottom
            }
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
    Log.d("TRACE", "observe conversation=${sb.toString()}")
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