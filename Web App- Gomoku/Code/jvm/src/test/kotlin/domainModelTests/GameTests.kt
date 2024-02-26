package domainModelTests

class GameTests {
    /*
    @Test
    fun `test first play`() {
        val playerB = 1
        val playerW = 2
        val game = Game(1, playerB, playerW, initialBoard(playerB, playerW), Variants.NORMAL, OpeningRules.NORMAL)
        val firstTurn = game.board.turn
        val newBoard = game.play(Square(Row(1), Column(1)), firstTurn)
        assertNotEquals(firstTurn, newBoard.turn)
        assertEquals(BoardTypes.BOARD_RUN, newBoard.type)
    }

    @Test
    fun `test play - invalid move`() {
        val playerB = 1
        val playerW = 2
        val game = Game(1, playerB, playerW, initialBoard(playerB, playerW), Variants.NORMAL, OpeningRules.NORMAL)
        val nextTurn = game.newTurn()
        assertThrows(IllegalArgumentException::class.java) {
            game.play(Square(Row(1), Column(1)),nextTurn )
        }
    }

    @Test
    fun `test play - second move`() {
        val playerB = 1
        val playerW = 2
        val game = Game(1, playerB, playerW, initialBoard(playerB, playerW), Variants.NORMAL, OpeningRules.NORMAL)
        val firstTurn = game.board.turn
        val firstMove = game.play(Square(Row(1), Column(1)), firstTurn)
        assertEquals(game.newTurn(), firstMove.turn)
        assertEquals(BoardTypes.BOARD_RUN, firstMove.type)

        val secondGame = game.copy(board = firstMove )
        val secondMove = secondGame.play(Square(Row(2), Column(2)),game.newTurn())
        assertEquals(firstTurn, secondMove.turn)
        assertEquals(BoardTypes.BOARD_RUN, secondMove.type)
    }

    @Test
    fun `test swapFirstMove`() {
        val playerB = 1
        val playerW = 2
        val game = Game(1, playerB, playerW, initialBoard(playerB, playerW), Variants.SWAP_FIRST_MOVE, OpeningRules.NORMAL)
        val firstTurn = game.board.turn
        val firstMove = game.play(Square(Row(1), Column(1)), firstTurn)
        val secondGame = game.copy(board = firstMove )
        val secondMove =secondGame.play(null, game.newTurn(), "YES")
        assertEquals(firstTurn, secondMove.turn)
        assertEquals(BoardTypes.BOARD_RUN, secondMove.type)

    }
    /*@Test
    fun `test swapFirstMove - swap pieces`() {
        val playerB = 1
        val playerW = 2
        val square =Square(Row(1), Column(1))
        val game = Game(1, playerB, playerW, initialBoard(playerB, playerW), Variants.SWAP_FIRST_MOVE, OpeningRules.NORMAL)
        val firstTurn = game.board.turn
        val secondTurn = game.newTurn()
        val firstMove = game.play(square, firstTurn)
        val secondGame = game.copy(board = firstMove )
        val secondMove =secondGame.play(null, secondTurn, "YES")
        assertEquals(secondMove.moves[square], SquareDetails(secondTurn, Pieces.N))
        assertEquals(secondMove.moves.size,1)
        assertEquals(secondMove.turn, firstTurn)
        assertEquals(BoardTypes.BOARD_RUN, secondMove.type)
    }*/

    @Test
    fun `test swapFirstMove - don't swap pieces`() {
        val playerB = 1
        val playerW = 2
        val square =Square(Row(1), Column(1))
        val game = Game(1, playerB, playerW, initialBoard(playerB, playerW), Variants.SWAP_FIRST_MOVE, OpeningRules.NORMAL)
        val firstTurn = game.board.turn
        val secondTurn = game.newTurn()
        val firstMove = game.play(square, firstTurn)
        val secondGame = game.copy(board = firstMove )
        val secondMove =secondGame.play(null, secondTurn, "NO")
        assertEquals(secondMove.moves[square], SquareDetails(firstTurn, Pieces.N))
        assertEquals(secondMove.moves.size,1)
        assertEquals(secondMove.turn, secondTurn)
        assertEquals(BoardTypes.BOARD_RUN, secondMove.type)

    }

    /*@Test
    fun `test swat`() {
        val playerB = 1
        val playerW = 2
        val game = Game(1, playerB, playerW, initialBoard(playerB, playerW), Variants.NORMAL, OpeningRules.SWAP)

        val newBoard = game.swat("YES")
        assertEquals(playerB, newBoard.turn)
        assertEquals(BoardTypes.BOARD_RUN, newBoard.type)

        // Adicione mais casos de teste conforme necessário

    }

    @Test
    fun `test swat - swap pieces`() {
        val playerB = 1
        val playerW = 2
        val game = Game(1, playerB, playerW, initialBoard(playerB, playerW), Variants.NORMAL, OpeningRules.SWAP)

        val newBoard = game.swat("YES")
        assertEquals(playerB, newBoard.turn)
        assertEquals(BoardTypes.BOARD_RUN, newBoard.type)
    }

    @Test
    fun `test swat - don't swap pieces`() {
        val playerB = 1
        val playerW = 2
        val game = Game(1, playerB, playerW, initialBoard(playerB, playerW), Variants.NORMAL, OpeningRules.SWAP)

        val newBoard = game.swat("NO")
        assertEquals(playerW, newBoard.turn)
        assertEquals(BoardTypes.BOARD_RUN, newBoard.type)
    }*/


    // Adicione mais testes conforme necessário

     */

}