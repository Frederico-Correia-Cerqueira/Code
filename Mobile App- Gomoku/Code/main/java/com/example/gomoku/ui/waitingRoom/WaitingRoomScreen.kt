package com.example.gomoku.ui.waitingRoom

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
import androidx.compose.ui.unit.dp
import com.example.gomoku.R
import com.example.gomoku.ui.common.theme.GomokuTheme

@Composable
fun WaitingRoomScreen() {
    GomokuTheme {
        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.background))
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Looks like your opponents are afraid to face you...", color = Color.White)
            Spacer(modifier = Modifier.height(30.dp))
            CircularProgressIndicator(
                modifier = Modifier.wrapContentSize(),
                color = Color.White,
                strokeWidth = 7.dp
            )
        }
    }
}