package com.example.gomoku.ui.savedGames

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gomoku.R
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Loaded
import com.example.gomoku.domain.SavedGame
import com.example.gomoku.domain.SavedGames
import com.example.gomoku.ui.common.Loading
import com.example.gomoku.ui.common.WriteColumn
import com.example.gomoku.ui.common.loadScreenSizes
import com.example.gomoku.ui.lobby.BottomBarView
import com.example.gomoku.ui.lobby.SelectedActivity
import com.example.gomoku.ui.ranking.SearchRow
import com.example.gomoku.ui.replay.ScreenSizes

@Composable
fun SavedGamesScreen(
    onSearchRequest: () -> Unit = { },
    onGameNameChange: (String) -> Unit,
    gameName: String,
    savedGames: IOState<SavedGames>,
    onRankingRequest: () -> Unit,
    onProfileRequest: () -> Unit,
    onCreditsRequest: () -> Unit,
    onLobbyRequest: () -> Unit,
    onSavedGameRequest: (Int, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background))
    ) {
        Log.v("LobbyScreen", "LoadState: $savedGames")
        when (savedGames) {
            is Loaded -> {
                Log.v("LobbyScreen", "LoadState: ${savedGames.value}")
                val savedGamesResult: SavedGames =
                    savedGames.value.getOrNull() ?: SavedGames(emptyList())
                Log.v("LobbyScreen", "StatsResult: $savedGamesResult")
                val screenSizes = loadScreenSizes()

                SearchGameBarView(
                    onSearchRequest = onSearchRequest,
                    gameName = gameName,
                    onGameNameChange = onGameNameChange,
                )
                SavedGamesColumnsNames(screenSizes = screenSizes.copy(height = screenSizes.height * 0.05f))
                SavedGamesList(
                    savedGames = savedGamesResult,
                    name = gameName,
                    screenSizes = screenSizes.copy(height = screenSizes.height * 0.66f)
                ) { gameID, gameName -> onSavedGameRequest(gameID, gameName) }
                BottomBarView(
                    onRankingRequest = { onRankingRequest() },
                    onSavedGamesRequest = { },
                    onMiddleButtonRequest = { onLobbyRequest() },
                    onCreditsRequest = { onCreditsRequest() },
                    onProfileRequest = { onProfileRequest() },
                    middleIcon = R.drawable.lobby_button_1,
                    middleDesc = "Lobby",
                    selectedActivity = SelectedActivity.SAVED_GAMES,
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
fun SavedGamesColumnsNames(screenSizes: ScreenSizes) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenSizes.height),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WriteColumn(
            name = "Name",
            modifier = Modifier.width(screenSizes.width * 0.5f),
            Arrangement.Start
        )
        WriteColumn(
            name = "Description",
            modifier = Modifier.width(screenSizes.width * 0.5f),
            Arrangement.Start
        )
    }
}

@Preview
@Composable
fun SavedGamesScreenPreview() {
    val savedGames = SavedGames(
        listOf(
            SavedGame(1, 1, "name", "description"),
            SavedGame(2, 2, "name", "description"),
            SavedGame(3, 3, "name", "description"),
            SavedGame(4, 4, "name", "description")
        )
    )

    SavedGamesScreen(
        onSearchRequest = { },
        gameName = "",
        onGameNameChange = { },
        savedGames = Loaded(Result.success(savedGames)),
        onRankingRequest = { },
        onProfileRequest = { },
        onCreditsRequest = { },
        onLobbyRequest = { },
        onSavedGameRequest = { _, _ -> }
    )
}

@Composable
fun SearchGameBarView(
    onSearchRequest: () -> Unit = { },
    gameName: String,
    onGameNameChange: (String) -> Unit,
) {
    SearchRow(
        onSearchByName = onGameNameChange,
        name = gameName,
        buttonText = "Search Game",
        onSearchRequest = onSearchRequest
    )
}

@Preview
@Composable
fun SearchGameBarViewPreview() {
    SearchGameBarView(
        onSearchRequest = { },
        gameName = "",
        onGameNameChange = { },
    )
}

@Preview
@Composable
fun SavedGamesListPreview() {
    val savedGames = SavedGames(
        listOf(
            SavedGame(1, 1, "name", "description"),
            SavedGame(2, 2, "name", "description"),
            SavedGame(3, 3, "name", "description"),
            SavedGame(4, 4, "name", "description")
        )
    )
    SavedGamesList(savedGames = savedGames, name = "", ScreenSizes(100.dp, 100.dp)) { _, _ -> }
}

@Composable
fun SavedGamesList(
    savedGames: SavedGames,
    name: String,
    screenSizes: ScreenSizes,
    onSavedGameRequest: (Int, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenSizes.height)
    ) {
        Log.v("SavedGamesList", "SavedGames: $savedGames")
        val savedGamesOrder = savedGames.games
            .sortedBy { it.id }
            .let { games ->
                if (name.isNotBlank()) games.filter { it.name == name }
                else games
            }
        items(savedGamesOrder.size) { index ->
            SavedGameRows(
                savedGamesOrder[index],
                name,
                screenSizes.height
            ) { gameID, gameName -> onSavedGameRequest(gameID, gameName) }
            Spacer(modifier = Modifier.height(3.dp))
        }
    }
}

@Composable
fun SavedGameRows(
    savedGame: SavedGame,
    name: String,
    height: Dp,
    onSavedGameRequest: (Int, String) -> Unit
) {
    Log.v("SavedGameRows", "SavedGame: $savedGame")
    val color = if (savedGame.name == name) {
        Color(0xFFC0CFFF)
    } else {
        Color(0xFFE0FFFF)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height * 0.1f)
            .background(color, shape = RoundedCornerShape(height * 0.01f))
            .border(2.dp, Color(0xFFB0E2FF))
            .clickable { onSavedGameRequest(savedGame.game, savedGame.name) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        SavedGameInfo(savedGame.name, Modifier.weight(1f))
        SavedGameInfo(savedGame.description, Modifier.weight(1f))
    }
}

@Composable
fun SavedGameInfo(text: String, modifier: Modifier) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                style = TextStyle(fontSize = 15.sp)
            )
        }
    }
}

