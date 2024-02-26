package gomoku.domainEntities

/*
fun main() {
    //var game = Game(1,1,2, initialBoard(),Variants.NORMAL, OpeningRules.NORMAL )
    //var game = Game(1, 1, 2, initialBoard(), Variants.SWAP_FIRST_MOVE, OpeningRules.NORMAL)
    var game = Game(1, 1, 2, initialBoard(1, 2), Variants.NORMAL, OpeningRules.SWAP)
    var information: String? = null
    loop@ while (game.board.type == BoardTypes.BOARD_RUN) {
        val playerPlay = game.board.turn

        printBoard(game.board)
        val mapWithPieces = game.board.moves
        if (game.variants == Variants.SWAP_FIRST_MOVE && mapWithPieces.size == 1 /*&& game.board.turn != initialBoard().turn*/) {
            while (true) {
                println("Do player $playerPlay want swap the pieces?Y/N")
                when (readlnOrNull()) {
                    "Y" -> {
                        information = "YES"
                        //val newBoard = game.play(null, playerPlay, information)//Change this Square
                        //game = game.copy(board = newBoard)
                        continue@loop
                    }

                    "N" -> {
                        information = null
                        break
                    }
                }
            }
        }

        if (game.openingRules == OpeningRules.SWAP && mapWithPieces.size == 3 /*&& game.board.turn != initialBoard().turn*/) {
            while (true) {
                println("Do player $playerPlay want swap the pieces?Y/N")
                when (readlnOrNull()) {
                    "Y" -> {
                        information = "YES"
                        val newBoard = game.play(null, playerPlay, information)//Change this Square
                        game = game.copy(board = newBoard)
                        continue@loop
                    }
                    "N" -> {
                        information = null
                        break
                    }
                }
            }
        }

        println("${playerPlay}'s turn. Enter the position (row column): ")

        val input = readlnOrNull() ?: break

        val (row, col) = input.split(" ").map { it.toIntOrNull() }

        if (row != null && col != null && row in 0..14 && col in 0..col) {
            val pos = Square(row, col)
            try {
                val newBoard = game.play(pos, playerPlay, information)
                game = game.copy(board = newBoard)
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        } else {
            println("Invalid Input. Please enter two coordinates separated by space")
        }
    }
    if (game.board.type == BoardTypes.BOARD_WIN) {
        printBoard(game.board)
        println("The player ${(game.board.turn)} win the game!")
    }
}

fun printBoard(board: Board) {
    println("  0 1 2 3 4 5 6 7 8 9 10 11 12 13 14")
    for (row in 0 until BOARD_DIM) {
        print("$row ")
        for (col in 0 until BOARD_DIM) {
            val symbol = when (board[Square(row, col)]) {
                SquareDetails(player = 1, Pieces.N) -> "x"
                SquareDetails(player = 2, Pieces.N) -> "o"
                SquareDetails(player = 1, Pieces.W) -> "X"
                SquareDetails(player = 2, Pieces.W) -> "O"
                else -> "."
            }
            print("$symbol ")
        }
        println()
    }

}


*/