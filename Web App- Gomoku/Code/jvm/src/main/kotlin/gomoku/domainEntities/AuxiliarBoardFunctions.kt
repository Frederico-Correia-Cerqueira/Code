package gomoku.domainEntities

import kotlin.random.Random
import kotlin.random.nextInt

/**
 * randomFirstPlayerTurn: Returns one of players passed as parameter.
 * @param playerB
 * @param playerW
 */
fun randomFirstPlayerTurn(playerB: Int, playerW: Int): Int {
    val random = Random.nextInt(0..1)
    return if (random == 0) playerB else playerW
}

/**
 * This private function checks if the pieces belong to the player in a specific direction
 *
 * @param board The game board
 * @param square The starting square
 * @param turn The current player's turn
 * @param dx The change in row direction
 * @param dy The change in column direction
 * @return The count of consecutive squares in the specified direction
 */
private fun checkDirection(board: Board, square: Square, turn: Int, dx: Int, dy: Int): Int {
    var count = 1

    // Verify in current direction
    for (i in 1 until WinCondition) {
        val newRow = square.row.number + (dx * i)
        val newCol = square.column.number + (dy * i)

        if (newRow !in 0 until BOARD_DIM || newCol !in 0 until BOARD_DIM) {
            break // Outside the Board
        }

        val nextSquare = board.moves[Square(Row(newRow), Column(newCol))]

        if (nextSquare != null) {
            if (nextSquare.player == turn) count++
            else break
        }
    }

    return count
}

/**
 * This private function retrieves a list of winning squares in a specific direction
 *
 * @param board The game board
 * @param square The starting square
 * @param turn The current player's turn
 * @param dx The change in row direction
 * @param dy The change in column direction
 * @return A list of winning squares in the specified direction
 */
private fun getWinners(board: Board, square: Square, turn: Int, dx: Int, dy: Int): List<Square> {
    val winners = mutableListOf(square)
    // Verify in current direction
    for (i in 1 until WinCondition) {
        val newRow = square.row.number + (dx * i)
        val newCol = square.column.number + (dy * i)

        if (newRow !in 0 until BOARD_DIM || newCol !in 0 until BOARD_DIM) {
            break // Fora da Board
        }

        val nextSquare = board.moves[Square(Row(newRow), Column(newCol))]

        if (nextSquare != null) {
            if (nextSquare.player == turn) winners.add(Square(Row(newRow), Column(newCol)))
            else break
        }
    }

    return winners.toList()
}

/**
 * @param square receives the last square played.
 * @return List of the squares responsible for the victory.
 */
fun Board.checkWin(square: Square): List<Square> {
    val directions = arrayOf(
        Pair(-1, 0), // Up
        Pair(0, 1),  // Right
        Pair(-1, -1),  //Top Left
        Pair(-1, 1)  // Top Right
    )

    for ((dx, dy) in directions) {
        val countForward = checkDirection(this, square, turn, dx, dy)
        val countBackward = checkDirection(this, square, turn, -dx, -dy)

        if (countForward + countBackward >= WinCondition + 1) {
            val winners = getWinners(this, square, turn, dx, dy) + getWinners(this, square, turn, -dx, -dy)
            return winners.distinct()
        }
    }

    return emptyList()
}

/**
 * @param winners receives the list of the squares responsible for the victory.
 *
 * This function receives the current map with all the Moves of the board, and
 * we want to change the type of the piece of which its square is in the list winners
 * passed as a parameter of function.
 *
 * To do this we created a new Map initially equivalent to the Moves received from the function receiver.
 * A for cycle is made to go through each square present in the winners list.
 * An initial check is made to check if there is a Key with the respective Square,
 * if there is we get the Value of that Key and change it
 * to replace the Normal piece (N) to a Winner piece(W).
 *
 * @return New Moves with the winners squares on it.
 */
fun Moves.implementWinnerPieces(winners: List<Square>): Moves {
    val newMoves = this.toMutableMap()
    for (winner in winners) {
        if (newMoves.containsKey(winner)) {
            val currentDetails = newMoves[winner]
            if (currentDetails != null) {
                newMoves[winner] = currentDetails.copy(type = Pieces.W)
            }
        }
    }
    return newMoves
}
