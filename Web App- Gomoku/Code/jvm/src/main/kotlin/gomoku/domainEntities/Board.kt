
package gomoku.domainEntities

import gomoku.utils.*

const val BOARD_DIM = 15

enum class BoardTypes { BOARD_WIN, BOARD_RUN }

data class Board(val moves: Moves, val turn: Int, val type: BoardTypes) {

    companion object {
        fun fromString(boardStr: String): Board {
            val parts = boardStr.split(" ")
            if (parts.size == 3) {
                val moves = if (parts[0] == "") emptyMap() else parseMovesString(parts[0])
                val turn = parts[1].toIntOrNull() ?: throw IllegalArgumentException("Invalid Player: ${parts[1]}")
                val type = when (parts[2]) {
                    "BOARD_RUN" -> BoardTypes.BOARD_RUN
                    "BOARD_WIN" -> BoardTypes.BOARD_WIN
                    else -> throw IllegalArgumentException("Invalid board type: ${parts[2]}")
                }
                return Board(moves, turn, type)
            } else {
                throw IllegalArgumentException("Invalid number of parameters for Board: $boardStr")
            }
        }

        fun parseMovesString(movesString: String): Map<Square, SquareDetails> {
            val movePairs: List<String> = movesString.split(";")
            val movesMap = movePairs.associate {
                val (squareStr, squareDetailsStr) = it.split("-")
                val square = Square.fromString(squareStr)
                val squareDetails = SquareDetails.fromString(squareDetailsStr)
                square to squareDetails
            }
            return movesMap
        }

    }

    override fun toString(): String {
        return "${moves.toFormattedString()} $turn $type"
    }

    private fun Moves.toFormattedString(): String {
        return this.entries.joinToString(";") { (square, squareDetails) ->
            "${square.row.number}:${square.column.number}-${squareDetails.player}:${squareDetails.type}"
        }
    }
}


/**
 * This function converts a string representing a board type into a [BoardTypes] enum
 * @param typeStr The string representing the board type
 * @return [BoardTypes] enum corresponding to the board type
 * @throws IllegalArgumentException If the provided board type is not valid
 */
fun parseBoardTypeString(typeStr: String): BoardTypes {
    val type = BoardTypes.values().find { it.name == typeStr }
    return type ?: throw IllegalArgumentException("Invalid Board Type format: $typeStr")
}


/**
 * Board.get: Returns the name of the player associated to a given position
 * */
operator fun Board.get(position: Square): SquareDetails? = moves[position]


/**
 * initialBoard: Returns the starter board.
 * @param playerB
 * @param playerW
 * Moves = emptyMap() -> All game free to play.
 * Call randomFirstPlayerTurn to decide the first player turn.
 */
fun initialBoard(playerB: Int, playerW: Int) =
    Board(emptyMap(), randomFirstPlayerTurn(playerB, playerW), BoardTypes.BOARD_RUN)


/**
 * Game.play:check if that move is possible,
 * if so add it to the list and check if there is any case of winning the game
 * If pos is null means what exist an information, what can mean swap the pieces,
 * any other cases pos can't be null
 * If Opening Rules is Swap, the first three moves will be played by the first player
 */
fun Game.play(pos: Square, userId: Int): Board {
    when (this.board.type) {
        BoardTypes.BOARD_WIN -> throw GameOver("Game is Over, winner = ${board.turn}")
        BoardTypes.BOARD_RUN -> {
            if (userId != board.turn) throw NotYourTurn("Not your turn")
            if (board[pos] != null) throw PositionTaken("Position taken $pos")

            val mapWithPieces = board.moves.toMutableMap()

            if (this.openingRules == OpeningRules.SWAP) {
                if (board.moves.size == 1) {
                    mapWithPieces[pos] = SquareDetails(player = newTurn(), type = Pieces.N)

                } else {
                    mapWithPieces[pos] = SquareDetails(player = this.board.turn, type = Pieces.N)
                }
                if (mapWithPieces.size < 3) return Board(mapWithPieces, this.board.turn, BoardTypes.BOARD_RUN)
            } else {
                mapWithPieces[pos] = SquareDetails(player = this.board.turn, type = Pieces.N)
            }
            val winners = Board(mapWithPieces, board.turn, this.board.type).checkWin(pos)

            return if (winners.isEmpty()) Board(mapWithPieces, newTurn(), BoardTypes.BOARD_RUN)
            else Board(mapWithPieces.implementWinnerPieces(winners), board.turn, BoardTypes.BOARD_WIN)
        }
    }
}


/**
 * Game.swapFirstMove: Change the first piece if second player want change the pieces and in this case
 * change the turn to first player
 */
fun Game.swapFirstMove(information: Boolean): Board {
    val mapWithPieces = board.moves.toMutableMap()
    return if (information) { //Change this condition
        val firstMove = board.moves.toList().first()
        mapWithPieces[firstMove.first] = SquareDetails(player = this.board.turn, type = Pieces.N)
        Board(mapWithPieces, newTurn(), BoardTypes.BOARD_RUN)
    } else board
}

/**
 * Game.swap: Change the first three pieces play for first player if second player want change
 * the pieces and in this case the next play its play by first player again
 */
fun Game.swap(information: Boolean): Board {
    val mapWithPieces = board.moves.toMutableMap()
    if (information) { //Change this condition
        val player = board.turn
        val playerTurn = newTurn()
        for ((key, _) in mapWithPieces) {
            val squareDetails = mapWithPieces[key]
            if (squareDetails != null) {
                if (squareDetails.player == player) {
                    mapWithPieces[key] = SquareDetails(playerTurn, squareDetails.type)
                } else {
                    mapWithPieces[key] = SquareDetails(player, squareDetails.type)
                }
            }
        }
        return Board(mapWithPieces, newTurn(), BoardTypes.BOARD_RUN)
    }
    return board
}










