package com.example.gomoku.ui.lobby

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gomoku.R
import com.example.gomoku.domain.Loaded
import com.example.gomoku.domain.Rank
import com.example.gomoku.domain.Stats
import com.example.gomoku.ui.replay.ScreenSizes

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LobbyScreenPreview() {
    val playerProfile = Stats("Alice", 100.0, 1, 1, 2, 50, Rank.PRO)

    LobbyScreen(
        onDefinitionsRequest = {  },
        onRankingRequest = {  },
        onSavedGamesRequest = {  },
        onPlayRequest = {  },
        onCreditsRequest = {  },
        onProfileRequest = {  },
        onLogoutRequest = {  },
        onGameSelect = {  },
        onGameDetails = {  },
        playerStats = Loaded(Result.success(playerProfile)),
        selected = Options.NORMAL,
        definitionPopup = false,
        enableToLogout = false,
        displayDetails = "Details"
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TopBarPreview() {
    TopBarView("Pedro Silva", ScreenSizes(100.dp, 100.dp)) {}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BottomBarPreview() {
    BottomBarView(
        onRankingRequest = {  },
        onSavedGamesRequest = {  },
        onMiddleButtonRequest = {  },
        onCreditsRequest = {  },
        onProfileRequest = {  },
        middleIcon = R.drawable.play_button_1,
        middleDesc = "Play",
        selectedActivity = SelectedActivity.RANKING,
        screenSizes = ScreenSizes(100.dp, 100.dp)
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MiddleBarPreview() {
    MiddleBarView(
        onGameSelect = {  },
        onGameDetails = {  },
        selected = { Options.NORMAL },
        displayDetails = "Details",
        Rank.UNRANKED,
        1000.0,
        ScreenSizes(100.dp, 100.dp)
    )
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BoxGamePreview() {
    BoxGame(
        Options.NORMAL,
        tag = "true",
        detailTag = "true",
        selected = true,
        onClickBox = {  },
        onClickDetails = {  },
        screenSizes = ScreenSizes(100.dp, 100.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GameOptionsPreview() {
    GameOptions(
        onClickBox = {  },
        onClickDetails = {  },
        selected = { Options.NORMAL },
        screenSizes = ScreenSizes(100.dp, 100.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DisplayGameDescriptionPreview() {
    DisplayGameDescription(displayDetails = "Details", screenSizes = ScreenSizes(100.dp, 100.dp))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DisplayRankPreview() {
    DisplayRank(playerRank = Rank.UNRANKED, playerElo = 10.0, padding = 10.dp,screenSizes = ScreenSizes(100.dp, 100.dp))
}



