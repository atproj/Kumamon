package com.example.kumamon.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels { ViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Conversation(viewModel)
                    Column {
                        //Conversation(viewModel)
                        TextToSpeechComponent()
                    }
                }
            }
        }
    }
}

@Composable
fun TextToSpeechComponent() {
    val context = LocalContext.current
    var textToSpeak by remember { mutableStateOf("") }
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }

    // Initialize TextToSpeech
    LaunchedEffect(Unit) {
        tts.value = TextToSpeech(context, OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                // TTS engine is ready
            } else {
                // Handle initialization error
            }
        })
    }

    Column {
        TextField(
            value = textToSpeak,
            onValueChange = { textToSpeak = it },
            label = { Text("Enter text to speak") }
        )
        Button(onClick = { speakText(tts.value, textToSpeak) }) {
            Text("Speak")
        }
    }

    // Clean up
    DisposableEffect(Unit) {
        onDispose {
            tts.value?.stop()
            tts.value?.shutdown()
        }
    }
}
fun speakText(tts: TextToSpeech?, text: String) {
    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
}
@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}
