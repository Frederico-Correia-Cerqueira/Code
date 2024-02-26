package com.example.gomoku.ui.replay

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gomoku.domain.Board
import com.example.gomoku.domain.BoardTypes
import com.example.gomoku.domain.GameDto
import com.example.gomoku.domain.Loaded
import com.example.gomoku.domain.OpeningRules
import com.example.gomoku.domain.UserDto
import com.example.gomoku.domain.Variants


@Preview
@Composable
fun ReplayScreenComponentsPreview() {
    val game = GameDto(
        id = 1,
        playerB = 1,
        playerW = 2,
        board = Board(
            moves = emptyMap(),
            turn = 1,
            type = BoardTypes.BOARD_RUN
        ),
        variants = Variants.NORMAL,
        openingRules = OpeningRules.NORMAL,
        info = false
    )
    ReplayScreen(
        gameName = "Game Name",
        game = Loaded(Result.success(game)),
        current = null,
        opponent = Loaded(Result.success(UserDto(1, "Opponent", "0", ""))),
        playIdx = 0,
        onIncrementIdx = { /*TODO*/ },
        onDecrementIdx = { /*TODO*/ },
        onRankingRequest = { /*TODO*/ },
        onSavedGamesRequest = { /*TODO*/ },
        onLobbyRequest = { /*TODO*/ },
        onCreditsRequest = { /*TODO*/ }) {
    }
}

@Preview
@Composable
fun DrawControllersButtonsPreview() {
    DrawControllersButtons(
        onDecrementIdx = { /*TODO*/ },
        onIncrementIdx = { /*TODO*/ },
        playIdx = 0,
        movesSize = 10,
        screenSizes = ScreenSizes(100.dp, 100.dp)
    )
}