package com.example.gomoku.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.gomoku.R
import com.example.gomoku.ui.common.theme.GomokuTheme
import kotlin.random.Random

@Composable
fun Loading() {
    GomokuTheme {
        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.background))
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = getRandomLoadingPhrase(),
                color = Color.White,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.karasha)),
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(30.dp))
            CircularProgressIndicator(
                modifier = Modifier.wrapContentSize(),
                color = Color.White,
                strokeWidth = 7.dp
            )
        }
    }
}

fun getRandomLoadingPhrase(): String {
    val phrases = listOf(
        "Loading... Meanwhile, our bytes are doing push-ups.",
        "Please wait, our hamster is running faster on the wheel!",
        "Loading... Taking shortcuts through the circuits!",
        "Warning: Do not feed the pixels during loading.",
        "Loading... Finding the shortest path to your fun.",
        "We're gathering digital magic... And a bit of coffee too.",
        "Wait a moment, we're painting the bits in brighter colors.",
        "Loading... Asking for patience from the technological universe.",
        "While loading, our robot is learning to dance.",
        "We're loading your entertainment... And the programmer's coffee too."
    )

    val randomIndex = Random.nextInt(phrases.size)
    return phrases[randomIndex]
}