package com.example.gomoku.ui.common

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gomoku.R
import com.example.gomoku.domain.Board
import com.example.gomoku.domain.CELL_SIZE
import com.example.gomoku.domain.DefineCellDraw
import com.example.gomoku.domain.Pieces
import com.example.gomoku.domain.Sizes
import com.example.gomoku.domain.SquareDetails
import com.example.gomoku.domain.SquarePosition
import com.example.gomoku.domain.parseToSquarePosition
import com.example.gomoku.domain.squarePositionMapper
import com.example.gomoku.tags.GameTags
import com.example.gomoku.ui.game.BOARD_DIM


@Composable
fun Square(
    modifier: Modifier = Modifier,
    player: Int?,
    enableToPlay: Boolean,
    position: SquarePosition,
    pieceImage: Int,
    tag: String,
    onPlay: () -> Unit,
) {
    Box(
        modifier = modifier.size(CELL_SIZE)
    ) {
        Image(
            painter = painterResource(id = R.drawable.board_background),
            contentDescription = "player piece",
            modifier = Modifier.fillMaxSize()
        )
        Surface(
            modifier = modifier
                .size(CELL_SIZE)
                .clickable(
                    enabled = player == null && enableToPlay,
                    onClick = { onPlay() }
                )
                .drawBehind {
                    val draw = DefineCellDraw(size = size)
                    val lines = squarePositionMapper(position, draw)

                    drawCell(
                        drawScope = this,
                        horizontal = lines.horizontal,
                        vertical = lines.vertical
                    )
                }
                .testTag(tag),
            color = Color.Transparent

        ) {
            if (player != null) {
                Image(
                    painter = painterResource(id = pieceImage),
                    contentDescription = "player piece",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun Board(onSquareClick: (Int, Int) -> Unit, board: Board, playerW: Int, enableToPlay: Boolean) {
    Log.v("Board", "Compose Board")
    val rows = BOARD_DIM
    val columns = BOARD_DIM

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        repeat(rows) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(columns) { column ->
                    val squareDetails = board.moves["($row-$column)"]
                    val position = parseToSquarePosition(row, column)
                    val squareOwner = squareDetails?.player
                    Square(
                        modifier = Modifier.padding(0.dp),
                        player = squareOwner,
                        enableToPlay = enableToPlay,
                        position = position,
                        pieceImage = getPieceImage(squareDetails, playerW),
                        tag = GameTags.GAME_BOARD_CELL_TAG + "_" + row + "_" + column,
                        onPlay = { onSquareClick(row, column) }
                    )
                }
            }
        }
    }
}

fun getPieceImage(squareDetails: SquareDetails?, playerW: Int) =
    if (squareDetails?.player == playerW) {
        if (squareDetails.type == Pieces.W) R.drawable.white_player_piece_winner
        else R.drawable.white_player_piece
    } else {
        if (squareDetails?.type == Pieces.W) R.drawable.black_player_piece_winner
        else R.drawable.black_player_piece
    }

fun drawCell(
    drawScope: DrawScope,
    horizontal: Sizes,
    vertical: Sizes,
    lineColor: Color = Color.Black,
    stroke: Float = 2f
) {
    // Horizontal Lines
    drawScope.drawLine(
        color = lineColor,
        start = horizontal.startSize,
        end = horizontal.endSize,
        strokeWidth = stroke
    )
    // Vertical Lines
    drawScope.drawLine(
        color = lineColor,
        start = vertical.startSize,
        end = vertical.endSize,
        strokeWidth = stroke
    )
}

