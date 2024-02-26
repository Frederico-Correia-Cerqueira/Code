package com.example.gomoku.ui.error

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.gomoku.R

@Composable
fun ErrorScreen(onBackHome: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.background)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Internal Error, please try again later", style = TextStyle(fontSize = 20.sp))
        Button(onClick = { onBackHome() }) {
            Text(text = "Back to Home")
        }
    }
}

@Preview
@Composable
fun ErrorScreenPreview() {
    ErrorScreen(onBackHome = {})
}