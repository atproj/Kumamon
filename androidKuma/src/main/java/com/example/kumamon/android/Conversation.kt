package com.example.kumamon.android

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.kumamon.model.Chat

@Composable
fun Conversation(viewModel: MainViewModel) {
    var currentMsg by rememberSaveable { mutableStateOf("") }
    val conversation = viewModel.conversation.collectAsStateWithLifecycle().value
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
                        /*coroutineScope.launch {
                            listState.animateScrollToItem(viewModel.conversation.size-1)
                        }*/
                    }) {
                        Text("Send")
                    }
                }
            }
        }
    ) { innerPadding ->
        //val conversation = viewModel.conversation.collectAsStateWithLifecycle().value
        when (conversation) {
            is ResultState.Success.NonEmpty -> Conversation(conversation.value, innerPadding)
            is ResultState.Success.Empty -> {
                // no-op as a conversation is always initialized
            }
            is ResultState.Loading -> {
                // append a loading message bubble from kumamon
            }
            is ResultState.Failure -> {
                // show a snackbar with a retry button
            }
        }
    }
}

@Composable
fun Conversation(chats: List<Chat>, innerPadding: PaddingValues) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(innerPadding)
    ) {
        items(chats) { chat ->
            ChatBubble(chat)
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
    val arrangement = if (chat.fromUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = arrangement,
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = backgroundColor,
            shadowElevation = 4.dp,
            modifier = Modifier.padding(4.dp)
        ) {
            if (chat.message.isNotBlank()) {
                Text(
                    text = chat.message,
                    color = if (chat.fromUser) Color.White else Color.Black,
                    modifier = Modifier.padding(8.dp)
                )
            }

            chat.imageUrl?.let {
                AsyncImage(model = it, contentDescription = null)
            }

            tts.value?.let {
                if (chat.enableDictation) {
                    // Add a icon to speak the text.
                    Button(onClick = { speakChatMessage(tts.value, textToSpeak) }) {
                        Text("Speak")
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
