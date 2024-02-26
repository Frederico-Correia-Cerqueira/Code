package domainModelTests

import gomoku.domainEntities.*
import gomoku.domainEntities.Board.Companion.parseMovesString
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BoardTests {

    @Test
    fun `test initialBoard`() {
        val board = initialBoard(1, 2)
        assertEquals(BoardTypes.BOARD_RUN, board.type)
        assertEquals(emptyMap<Square, SquareDetails> (), board.moves)
    }

    @Test
    fun `test randomFirstPlayerTurn`() {
        val playerB = 1
        val playerW = 2
        val result = randomFirstPlayerTurn(playerB, playerW)
        assertTrue(result == playerB || result == playerW)
    }

    @Test
    fun `test parseMovesString`() {
        val movesString = "1:1-1:N;2:2-2:N"
        val moves = parseMovesString(movesString)
        assertEquals(2, moves.size)
    }

    @Test
    fun `test checkWin`() {
        val moves = mapOf(
            Square(Row(1), Column(1)) to SquareDetails(1, Pieces.N),
            Square(Row(1), Column(2)) to SquareDetails(1, Pieces.N),
            Square(Row(1), Column(3)) to SquareDetails(1, Pieces.N),
            Square(Row(1), Column(4)) to SquareDetails(1, Pieces.N),
            Square(Row(1), Column(5)) to SquareDetails(1, Pieces.N)
        )
        val board = Board(moves, 1, BoardTypes.BOARD_RUN)
        val winners = board.checkWin(Square(Row(1), Column(1)))
        assertEquals(5, winners.size)
    }

    @Test
    fun `test implementWinnerPieces`() {
        val moves = mapOf(
            Square(Row(1), Column(1)) to SquareDetails(1, Pieces.N),
            Square(Row(1), Column(2)) to SquareDetails(1, Pieces.N),
            Square(Row(1), Column(3)) to SquareDetails(1, Pieces.N),
            Square(Row(1), Column(4)) to SquareDetails(1, Pieces.N),
            Square(Row(1), Column(5)) to SquareDetails(1, Pieces.N)
        )
        val board = Board(moves, 1, BoardTypes.BOARD_RUN)
        val winners = board.checkWin(Square(Row(1), Column(1)))
        val newMoves = board.moves.implementWinnerPieces(winners)
        assertTrue(newMoves.values.all { it.type == Pieces.W })
    }

    // Adicione mais testes conforme necessário

}
