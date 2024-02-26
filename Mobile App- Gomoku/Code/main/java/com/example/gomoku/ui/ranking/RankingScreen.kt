package com.example.gomoku.ui.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gomoku.R
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Loaded
import com.example.gomoku.domain.Rank
import com.example.gomoku.domain.Stats
import com.example.gomoku.fakeService.PlayerFakeService
import com.example.gomoku.ui.common.Loading
import com.example.gomoku.ui.common.loadScreenSizes
import com.example.gomoku.ui.common.RankingList
import com.example.gomoku.ui.common.StatsNames
import com.example.gomoku.ui.lobby.BottomBarView
import com.example.gomoku.ui.lobby.NavigationButton
import com.example.gomoku.ui.lobby.SelectedActivity
import com.example.gomoku.ui.replay.ScreenSizes


@Composable
fun RankingScreen(
    stats: IOState<List<Stats>>,
    rank: Rank? = null,
    player: String,
    onSearchByName: (String) -> Unit = { },
    onGetPlayerStats: () -> Unit = {},
    onChooseRanking: (Rank?) -> Unit = { },
    onProfileRequest: () -> Unit,
    onLobbyRequest: () -> Unit,
    onCreditsRequest: () -> Unit,
    onSavedGamesRequest: () -> Unit,
) {
    val screenSizes = loadScreenSizes()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background))
    ) {
        when (stats) {
            is Loaded -> {
                val statsResult = stats.value.getOrNull() ?: error("Null stats")
                SearchRow(
                    onSearchByName,
                    player,
                    "My Stats",
                ) { onGetPlayerStats() }
                RankButtonsRow(
                    rank,
                    screenSizes.copy(height = screenSizes.height * 0.1f)
                ) { onChooseRanking(it) }
                Rank(rank, screenSizes.copy(height = screenSizes.height * 0.05f))
                StatsNames(screenSizes.copy(height = screenSizes.height * 0.05f))
                RankingList(
                    statsResult,
                    rank,
                    player,
                    screenSizes.copy(height = screenSizes.height * 0.51f)
                )
                BottomBarView(
                    onRankingRequest = {},
                    onSavedGamesRequest = onSavedGamesRequest,
                    onMiddleButtonRequest = onLobbyRequest,
                    onCreditsRequest = onCreditsRequest,
                    onProfileRequest = onProfileRequest,
                    middleIcon = R.drawable.lobby_button_1,
                    middleDesc = "Lobby",
                    selectedActivity = SelectedActivity.RANKING,
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
fun SearchRow(
    onSearchByName: (String) -> Unit,
    name: String,
    buttonText: String,
    onSearchRequest: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Search(onSearchByName = onSearchByName, name = name)
        Button(onClick = { onSearchRequest() }) {
            Text(text = buttonText)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(onSearchByName: (String) -> Unit, name: String) {
    TextField(
        modifier = Modifier.width(230.dp),
        value = name,
        onValueChange = onSearchByName,
        placeholder = {
            Text(
                "Enter the name you want to search",
                style = TextStyle(fontSize = 12.sp)
            )
        }
    )
}

@Composable
fun RankButtonsRow(currentRank: Rank?, screenSizes: ScreenSizes, onChooseRanking: (Rank?) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .height(screenSizes.height)
    ) {
        NavigationButton(
            image = R.drawable.unranked,
            desc = "unranked",
            size = screenSizes.width * 0.15f,
            selected = currentRank == Rank.UNRANKED,
            tag = "unranked"
        ) { onChooseRanking(Rank.UNRANKED) }
        NavigationButton(
            image = R.drawable.noob,
            desc = "noob",
            selected = currentRank == Rank.NOOB,
            size = screenSizes.width * 0.15f,
            tag = "noob"
        ) { onChooseRanking(Rank.NOOB) }
        NavigationButton(
            image = R.drawable.beginner,
            desc = "beginner",
            size = screenSizes.width * 0.15f,
            tag = "beginner",
            selected = currentRank == Rank.BEGINNER
        ) { onChooseRanking(Rank.BEGINNER) }
        NavigationButton(
            image = R.drawable.intermediate,
            desc = "expert",
            size = screenSizes.width * 0.15f,
            selected = currentRank == Rank.INTERMEDIATE,
            tag = "intermediate"
        ) { onChooseRanking(Rank.INTERMEDIATE) }
        NavigationButton(
            image = R.drawable.expert,
            desc = "expert",
            size = screenSizes.width * 0.15f,
            selected = currentRank == Rank.EXPERT,
            tag = "expert"
        ) { onChooseRanking(Rank.EXPERT) }
        NavigationButton(
            image = R.drawable.pro,
            desc = "pro",
            size = screenSizes.width * 0.15f,
            selected = currentRank == Rank.PRO,
            tag = "pro"
        ) { onChooseRanking(Rank.PRO) }


    }
}

@Composable
fun Rank(rank: Rank?, screenSizes: ScreenSizes) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(screenSizes.height)
    ) {
        Text(
            text = "Ranking ${rank?.name ?: "No Chosen"}",
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.karasha)),
                color = Color.White
            ),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RankButtonsRowPreview() {
    RankButtonsRow(null, ScreenSizes(100.dp, 100.dp)) { }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RankingListPreview() {
    RankingList(PlayerFakeService().stats, null, "", ScreenSizes(100.dp, 100.dp))
}


