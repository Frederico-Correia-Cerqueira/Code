package gomoku.domainEntities

/**
 * The Column type identifies one of the columns on the board.
 * Columns are identified by a number from 0 to 14 (if BOARD_DIM==15)
 * The left column is 0 .
 */
class Column private constructor(val number: Int) {
    companion object {
        val values = List(BOARD_DIM) { idx -> Column(idx) }
        operator fun invoke(c: Int) = values[c]
    }
}

