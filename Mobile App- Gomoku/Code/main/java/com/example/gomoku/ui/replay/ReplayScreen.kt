package com.example.gomoku.ui.replay

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.gomoku.R
import com.example.gomoku.domain.GameDto
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Loaded
import com.example.gomoku.domain.PlayerInfo
import com.example.gomoku.domain.UserDto
import com.example.gomoku.domain.getOrNull
import com.example.gomoku.tags.GameTags
import com.example.gomoku.ui.common.Loading
import com.example.gomoku.ui.common.loadScreenSizes
import com.example.gomoku.ui.game.BoardRow
import com.example.gomoku.ui.lobby.BottomBarView
import com.example.gomoku.ui.lobby.SelectedActivity

@Composable
fun ReplayScreen(
    gameName: String?,
    game: IOState<GameDto>,
    current: PlayerInfo?,
    opponent: IOState<UserDto>,
    playIdx: Int,
    onIncrementIdx: () -> Unit,
    onDecrementIdx: () -> Unit,
    onRankingRequest: () -> Unit,
    onSavedGamesRequest: () -> Unit,
    onLobbyRequest: () -> Unit,
    onCreditsRequest: () -> Unit,
    onProfileRequest: () -> Unit
) {
    Log.v("ReplayScreen", "Display ReplayScreen")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background))
    ) {
        when (game) {
            is Loaded -> {
                val gameResult: GameDto =
                    game.value.getOrNull() ?: error("Game not found")

                val opponentPlayer = opponent.getOrNull()
                if (opponentPlayer == null || current == null) {
                    Loading()
                } else {
                    val opponentPiece: Int =
                        if (opponentPlayer.id == gameResult.playerW) R.drawable.white_player_piece
                        else R.drawable.black_player_piece

                    val currentPiece: Int =
                        if (opponentPiece == R.drawable.white_player_piece) R.drawable.black_player_piece
                        else R.drawable.white_player_piece

                    val screenSizes = loadScreenSizes()
                    DisplayGameName(
                        name = gameName.toString(),
                        screenSizes = screenSizes.copy(height = screenSizes.height * 0.1f)
                    )
                    DisplayPlayerUp(
                        player = opponentPlayer.username,
                        piece = opponentPiece,
                        screenSizes = screenSizes.copy(height = screenSizes.height * 0.07f)
                    )

                    BoardRow(
                        onSquareClick = { _, _ -> },
                        board = gameResult.board.copy(
                            moves = gameResult.board.moves.toList().take(playIdx).toMap()
                        ),
                        height = screenSizes.height * 0.5f,
                        playerW = gameResult.playerW,
                        enableToPlay = false
                    )


                    DisplayPlayerDown(
                        player = current.name,
                        piece = currentPiece,
                        screenSizes = screenSizes.copy(height = screenSizes.height * 0.07f)
                    )
                    DrawControllersButtons(
                        onDecrementIdx,
                        onIncrementIdx,
                        playIdx,
                        gameResult.board.moves.size,
                        screenSizes.copy(height = screenSizes.height * 0.05f)
                    )
                    BottomBarView(
                        onRankingRequest = { onRankingRequest() },
                        onSavedGamesRequest = { onSavedGamesRequest() },
                        onMiddleButtonRequest = { onLobbyRequest() },
                        onCreditsRequest = { onCreditsRequest() },
                        onProfileRequest = { onProfileRequest() },
                        middleIcon = R.drawable.lobby_button_1,
                        middleDesc = "Lobby",
                        selectedActivity = SelectedActivity.SAVED_GAMES,
                        screenSizes = screenSizes.copy(height = screenSizes.height * 0.15f)
                    )
                }
            }


            else -> {
                Loading()
            }
        }
    }
}

@Composable
fun DisplayGameName(name: String, screenSizes: ScreenSizes) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(screenSizes.height)

    ) {
        Log.v("DisplayGameName", "name: $name")
        Text(
            text = name,
            color = Color.White,
            fontSize = 30.sp
        )
    }
}


@Composable
fun DisplayPlayerDown(player: String, piece: Int, screenSizes: ScreenSizes) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenSizes.height)
            .testTag(GameTags.GAME_CURRENT_NAME_TAG)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = screenSizes.width * 0.1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.player_username_reverse),
                contentDescription = "player"
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(start = screenSizes.width * 0.49f)
                .width(screenSizes.width * 0.30f)
                .fillMaxHeight()
        ) {
            Text(text = player)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = screenSizes.width * 0.1f)
        ) {
            Image(painter = painterResource(id = piece), contentDescription = "player_piece")
        }
    }
}

@Composable
fun DisplayPlayerUp(player: String, piece: Int, screenSizes: ScreenSizes) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenSizes.height)
            .testTag(GameTags.GAME_OPPONENT_NAME_TAG)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = screenSizes.width * 0.1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.player_username),
                contentDescription = "player"
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(start = screenSizes.width * 0.21f)
                .width(screenSizes.width * 0.30f)
                .height(height = screenSizes.height)
        ) {
            Text(text = player)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = screenSizes.width * 0.1f)
        ) {
            Image(painter = painterResource(id = piece), contentDescription = "player_piece")
        }
    }
}

@Composable
fun DrawControllersButtons(
    onDecrementIdx: () -> Unit,
    onIncrementIdx: () -> Unit,
    playIdx: Int,
    movesSize: Int,
    screenSizes: ScreenSizes
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .height(screenSizes.height)
    ) {
        Log.v("DrawControllersButtons", "playIdx: $playIdx")
        Image(
            painter = painterResource(id = R.drawable.replay_previous),
            contentDescription = "replay_previous",
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(screenSizes.height * 0.5f))
                .clickable(onClick = { onDecrementIdx() }, enabled = playIdx > 0)
        )

        Image(
            painter = painterResource(id = R.drawable.replay_next),
            contentDescription = "replay_next",
            modifier = Modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(screenSizes.height * 0.5f))
                .clickable(onClick = { onIncrementIdx() }, enabled = playIdx < movesSize)
        )
    }
}


data class ScreenSizes(val width: Dp, val height: Dp)