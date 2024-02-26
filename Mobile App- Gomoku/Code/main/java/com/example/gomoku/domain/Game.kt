package com.example.gomoku.domain

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.example.gomoku.ui.game.BOARD_DIM


data class Row(val number: Int)
data class Column(val number: Int)
enum class Pieces { N, W }
data class SquareDetails(val player: Int, val type: Pieces)
typealias Moves = Map<String, SquareDetails>

enum class BoardTypes { BOARD_WIN, BOARD_RUN }
enum class Variants { NORMAL, SWAP_FIRST_MOVE }
enum class OpeningRules { NORMAL, SWAP }

data class Board(val moves: Moves, val turn: Int, val type: BoardTypes)
data class GameDto(
    val id: Int,
    val playerB: Int,
    val playerW: Int,
    val board: Board,
    val variants: Variants,
    val openingRules: OpeningRules,
    val info: Boolean
)

data class GameIDDto(
    val id: Int
)

data class SaveGameInputModel(val name: String, val description: String = "")

class DefineCellDraw(private val size: Size) {
    private val start = 0f

    private val horizontalMiddle: Float
        get() = size.width / 2

    private val verticalMiddle: Float
        get() = size.height / 2

    private val horizontalEnd: Float
        get() = size.width

    private val verticalEnd: Float
        get() = size.height

    val fullHorizontal: Sizes =
        createSizes(Offset(start, verticalMiddle), Offset(horizontalEnd, verticalMiddle))
    val fullVertical: Sizes =
        createSizes(Offset(horizontalMiddle, start), Offset(horizontalMiddle, verticalEnd))
    val startHalfHorizontal: Sizes =
        createSizes(Offset(start, verticalMiddle), Offset(horizontalMiddle, verticalMiddle))
    val endHalfHorizontal: Sizes =
        createSizes(Offset(horizontalMiddle, verticalMiddle), Offset(horizontalEnd, verticalMiddle))
    val startHalfVertical: Sizes =
        createSizes(Offset(horizontalMiddle, start), Offset(horizontalMiddle, verticalMiddle))
    val endHalfVertical: Sizes =
        createSizes(Offset(horizontalMiddle, verticalMiddle), Offset(horizontalMiddle, verticalEnd))

    private fun createSizes(start: Offset, end: Offset): Sizes =
        Sizes(startSize = start, endSize = end)
}

data class Sizes(val startSize: Offset, val endSize: Offset)
data class SquareLines(val horizontal: Sizes, val vertical: Sizes)

enum class SquarePosition {
    TOP_LEFT,
    TOP,
    TOP_RIGHT,
    LEFT,
    CENTER,
    RIGHT,
    BOTTOM_LEFT,
    BOTTOM,
    BOTTOM_RIGHT
}

fun parseToSquarePosition(row: Int, column: Int): SquarePosition {
    val rows = BOARD_DIM
    val columns = BOARD_DIM
    return when {
        row == 0 && column == 0 -> SquarePosition.TOP_LEFT
        row == 0 && column == columns - 1 -> SquarePosition.TOP_RIGHT
        row == 0 -> SquarePosition.TOP
        row == rows - 1 && column == 0 -> SquarePosition.BOTTOM_LEFT
        row == rows - 1 && column == columns - 1 -> SquarePosition.BOTTOM_RIGHT
        row == rows - 1 -> SquarePosition.BOTTOM
        column == 0 -> SquarePosition.LEFT
        column == columns - 1 -> SquarePosition.RIGHT
        else -> SquarePosition.CENTER
    }
}

fun squarePositionMapper(position: SquarePosition, defineCellDraw: DefineCellDraw): SquareLines {
    return when (position) {
        SquarePosition.TOP_LEFT -> SquareLines(
            defineCellDraw.endHalfHorizontal,
            defineCellDraw.endHalfVertical
        )

        SquarePosition.TOP -> SquareLines(
            defineCellDraw.fullHorizontal,
            defineCellDraw.endHalfVertical
        )

        SquarePosition.TOP_RIGHT -> SquareLines(
            defineCellDraw.startHalfHorizontal,
            defineCellDraw.endHalfVertical
        )

        SquarePosition.LEFT -> SquareLines(
            defineCellDraw.endHalfHorizontal,
            defineCellDraw.fullVertical
        )

        SquarePosition.CENTER -> SquareLines(
            defineCellDraw.fullHorizontal,
            defineCellDraw.fullVertical
        )

        SquarePosition.RIGHT -> SquareLines(
            defineCellDraw.startHalfHorizontal,
            defineCellDraw.fullVertical
        )

        SquarePosition.BOTTOM_LEFT -> SquareLines(
            defineCellDraw.endHalfHorizontal,
            defineCellDraw.startHalfVertical
        )

        SquarePosition.BOTTOM -> SquareLines(
            defineCellDraw.fullHorizontal,
            defineCellDraw.startHalfVertical
        )

        SquarePosition.BOTTOM_RIGHT -> SquareLines(
            defineCellDraw.startHalfHorizontal,
            defineCellDraw.startHalfVertical
        )
    }
}


val CELL_SIZE = 25.dp