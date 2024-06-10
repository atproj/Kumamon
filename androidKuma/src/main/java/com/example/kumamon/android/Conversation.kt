package com.example.kumamon.android

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kumamon.model.Chat
import kotlinx.coroutines.launch

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

@Composable
fun ChatBubble(chat: Chat) {
    val context = LocalContext.current
    val textToSpeak by remember { mutableStateOf(chat.message) }
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }

    // Initialize TextToSpeech
    LaunchedEffect(Unit) {
        tts.value = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                // TTS engine is ready
            } else {
                // Handle initialization error
                tts.value = null
            }
        })
    }

    val backgroundColor = if (chat.fromUser) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.surface
    val alignment = if (chat.fromUser) Arrangement.End else Arrangement.Start

    MyApplicationTheme {
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
                Column {
                    if (chat.message.isNotBlank()) {
                        Text(
                            text = chat.message,
                            color = if (chat.fromUser) Color.White else Color.Black,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

//                    chat.imageUrl?.let {
//                        AsyncImage(model = it, contentDescription = null)
//                    }

                    tts.value?.let {
                        if (!chat.fromUser) {
                            // Add a icon to speak the text.
                            Button(onClick = { speakChatMessage(tts.value, textToSpeak) }) {
                                Text("Speak")
                            }
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.value?.stop()
            tts.value?.shutdown()
        }
    }
}

fun speakChatMessage(tts: TextToSpeech?, text: String) {
    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
}

@Preview
@Composable
fun ChatPreview() {
    MyApplicationTheme {
        ChatBubble(Chat())
    }
}