package com.example.gomoku.ui.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.gomoku.R
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Loaded
import com.example.gomoku.ui.common.loadScreenSizes
import com.example.gomoku.ui.lobby.BottomBarView
import com.example.gomoku.ui.lobby.DisplayRank
import com.example.gomoku.ui.lobby.SelectedActivity
import com.example.gomoku.domain.Rank
import com.example.gomoku.domain.Stats
import com.example.gomoku.ui.common.Loading
import com.example.gomoku.ui.replay.ScreenSizes

@Composable
fun ProfileScreen(
    onRankingRequest: () -> Unit,
    onSavedGamesRequest: () -> Unit,
    onLobbyRequest: () -> Unit,
    onCreditsRequest: () -> Unit,
    playerStats: IOState<Stats>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background))
    ) {
        when (playerStats) {
            is Loaded -> {
                Log.v("LobbyScreen", "LoadState: ${playerStats.value}")
                val statsResult: Stats =
                    playerStats.value.getOrNull() ?: error("Stats is null")
                Log.v("LobbyScreen", "StatsResult: $statsResult")
                val screenSizes = loadScreenSizes()
                DisplayUsername(
                    username = statsResult.player,
                    screenSizes = screenSizes.copy(height = screenSizes.height * 0.1f)
                )

                DisplayPlayerWinsAndMatches(
                    wins = statsResult.victories,
                    matches = statsResult.totalGames,
                    screenSizes.copy(height = screenSizes.height * 0.1f)
                )
                DisplayPlayerDefeatsAndResume(
                    defeats = statsResult.defeats,
                    resume = statsResult.winRate,
                    screenSizes.copy(height = screenSizes.height * 0.1f)
                )

                DisplayRankAndElo(
                    rank = statsResult.rank,
                    statsResult.elo,
                    screenSizes.copy(height = screenSizes.height * 0.49f)
                )

                BottomBarView(
                    onRankingRequest = { onRankingRequest() },
                    onSavedGamesRequest = { onSavedGamesRequest() },
                    onMiddleButtonRequest = { onLobbyRequest() },
                    onCreditsRequest = { onCreditsRequest() },
                    onProfileRequest = { },
                    middleIcon = R.drawable.lobby_button_1,
                    middleDesc = "Lobby",
                    selectedActivity = SelectedActivity.PROFILE,
                    screenSizes = screenSizes.copy(height = screenSizes.height * 0.15f)
                )

            }

            else -> {
                Loading()
            }
        }
    }
}

@Composable
fun DisplayRankAndElo(rank: Rank, elo: Double, screenSizes: ScreenSizes) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(screenSizes.height)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_tree_left),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(screenSizes.width * 0.5f)
            )
            Image(
                painter = painterResource(id = R.drawable.profile_tree_right),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(screenSizes.width * 0.5f)
            )
        }
        DisplayRank(
            playerRank = rank,
            playerElo = elo,
            padding = screenSizes.height * 0.5f,
            screenSizes = screenSizes.copy(height = screenSizes.height * 0.4f)
        )

    }
}


@Composable
fun DisplayPlayerWinsAndMatches(wins: Int, matches: Int, screenSizes: ScreenSizes) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .height(screenSizes.height)
    ) {
        DisplayStat(image = R.drawable.profile_wins, stat = wins, screenSizes = screenSizes)
        DisplayStat(image = R.drawable.profile_matches, stat = matches, screenSizes = screenSizes)
    }
}

@Composable
fun DisplayStat(image: Int, stat: Int, screenSizes: ScreenSizes) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(screenSizes.width * 0.4f),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(start = screenSizes.width * 0.1f)
                .height(screenSizes.height * 0.3f)
                .width(screenSizes.width * 0.25f)
        ) {
            Text(
                text = stat.toString(),
                color = Color.White
            )
        }
    }
}

@Composable
fun DisplayPlayerDefeatsAndResume(defeats: Int, resume: Int, screenSizes: ScreenSizes) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .height(screenSizes.height)
    ) {
        DisplayStat(image = R.drawable.profile_loses, stat = defeats, screenSizes = screenSizes)
        DisplayStat(image = R.drawable.profile_results, stat = resume, screenSizes = screenSizes)
    }
}

@Composable
fun DisplayUsername(username: String, screenSizes: ScreenSizes) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(screenSizes.height)
    ) {

        Text(
            text = "$username Profile",
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.karasha)),
                fontSize = 35.sp,
                color = Color.White
            ),
            color = Color.White
        )

    }
}


