package com.example.gomoku.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gomoku.R
import com.example.gomoku.ui.lobby.DisplayRank
import com.example.gomoku.domain.Rank
import com.example.gomoku.ui.replay.ScreenSizes

@Preview(showBackground = true)
@Composable
fun BackGroundPreview() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background))
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .padding(top = 10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile_background_screen),
                    contentDescription = "profile_background_screen",
                    modifier = Modifier.fillMaxSize()
                )
                DisplayUsername(username = "Username", screenSizes = ScreenSizes(100.dp, 100.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 430.dp)
                ) {
                    DisplayRank(
                        playerRank = Rank.PRO,
                        playerElo = 10.0,
                        padding = 10.dp,
                        ScreenSizes(100.dp, 100.dp)
                    )
                }

                DisplayRankAndElo(rank = Rank.PRO, elo = 1200.0, ScreenSizes(100.dp, 100.dp))
            }
        }
    }
}


