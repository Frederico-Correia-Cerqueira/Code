package com.example.gomoku.ui.game

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.gomoku.R
import com.example.gomoku.TAG
import com.example.gomoku.domain.Board
import com.example.gomoku.domain.BoardTypes
import com.example.gomoku.domain.GameDto
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Loaded
import com.example.gomoku.ui.common.Board
import com.example.gomoku.ui.common.loadScreenSizes
import com.example.gomoku.ui.common.GameWinPopUp
import com.example.gomoku.ui.common.SwapPopUp
import com.example.gomoku.ui.common.theme.SaveGamePopUp
import com.example.gomoku.domain.PlayerInfo
import com.example.gomoku.domain.UserDto
import com.example.gomoku.domain.getOrNull
import com.example.gomoku.tags.GameTags
import com.example.gomoku.ui.common.Loading
import com.example.gomoku.ui.replay.DisplayPlayerDown
import com.example.gomoku.ui.replay.DisplayPlayerUp
import com.example.gomoku.ui.replay.ScreenSizes

const val BOARD_DIM = 15

@Composable
fun GameScreen(
    game: IOState<GameDto>,
    current: PlayerInfo?,
    opponent: IOState<UserDto>,
    enableToPlay: Boolean,
    onSquareClick: (Int, Int) -> Unit,
    onSwapResponseClick: (Boolean) -> Unit,
    onBackLobbyClick: () -> Unit,
    nameGame: String,
    descriptionGame: String,
    setNameGame: (String) -> Unit,
    setDescriptionGame: (String) -> Unit,
    saveGame: Boolean,
    setSaveGame: (Boolean) -> Unit,
    onSaveGame: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.background)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        when (game) {
            is Loaded -> {
                val gameResult: GameDto =
                    game.value.getOrNull() ?: error("Stats is null")
                Log.v("LobbyScreen", "GameResult: $gameResult")

                val opponentPlayer = opponent.getOrNull()

                if (opponentPlayer == null || current == null) {
                    Loading()
                } else {
                    val screenSizes = loadScreenSizes()

                    val opponentPiece: Int =
                        if (opponentPlayer.id == gameResult.playerW) R.drawable.white_player_piece
                        else R.drawable.black_player_piece

                    val currentPiece: Int =
                        if (opponentPiece == R.drawable.white_player_piece) R.drawable.black_player_piece
                        else R.drawable.white_player_piece

                    DisplayPlayerUp(
                        player = opponentPlayer.username,
                        piece = opponentPiece,
                        screenSizes = screenSizes.copy(height = screenSizes.height * 0.07f)
                    )
                    BoardRow(
                        onSquareClick,
                        gameResult.board,
                        gameResult.playerW,
                        screenSizes.height * 0.5f,
                        enableToPlay
                    )
                    DisplayPlayerDown(
                        player = current.name,
                        piece = currentPiece,
                        screenSizes = screenSizes.copy(height = screenSizes.height * 0.07f)
                    )
                    PlayerRow(
                        gameResult.board.turn,
                        current.playerID,
                        screenSizes.copy(height = screenSizes.height * 0.1f)
                    )

                    if (gameResult.info && gameResult.board.turn == current.playerID) {
                        SwapPopUp(onSwapResponseClick)
                    }
                    if (saveGame) {
                        SaveGamePopUp(
                            nameGame,
                            descriptionGame,
                            setNameGame,
                            setDescriptionGame,
                            onSaveGame,
                            onBackLobbyClick
                        )
                    }
                    GameWin(
                        gameResult.board,
                        current.playerID,
                        onBackLobbyClick,
                        setSaveGame,
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
fun GameWin(
    board: Board,
    current: Int,
    onBackLobbyClick: () -> Unit,
    onSaveGame: (Boolean) -> Unit,
) {
    Log.v(TAG, "GameWin called")
    if (board.type == BoardTypes.BOARD_WIN) {
        if (board.turn == current)
            GameWinPopUp(
                "You won!",
                "Do you want to play again?",
                onBackLobbyClick,
                onSaveGame,
            )
        else
            GameWinPopUp(
                "You lost!",
                "Do you want to play again?",
                onBackLobbyClick,
                onSaveGame,
            )
    }
}

@Composable
fun BoardRow(
    onSquareClick: (Int, Int) -> Unit,
    board: Board,
    playerW: Int,
    height: Dp,
    enableToPlay: Boolean
) {
    Log.v("BoardRow", "board: ${board.moves}")
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .size(height)
            .testTag(GameTags.GAME_BOARD_TAG)
    ) {
        Board(onSquareClick, board, playerW, enableToPlay)
    }
}

@Composable
fun PlayerRow(turn: Int, currentID: Int, screenSizes: ScreenSizes) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenSizes.height),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val myTurn = if (turn == currentID) "Your turn" else "Opponent's turn"
        Text(
            text = myTurn,
            style = TextStyle(fontSize = 25.sp),
            color = Color.White
        )
    }
}