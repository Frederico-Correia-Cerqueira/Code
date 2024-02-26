package gomoku.domainEntities

/**
 * The Row type identifies one of the rows on the board.
 * Rows are identified by a number, from 0 to 14 (if BOARD_DIM==15)
 * The top row is '0'
 */
class Row private constructor(val number: Int) {
    companion object {
        val values = List(BOARD_DIM) { idx -> Row(idx) }
        operator fun invoke(r: Int) = values[r]
    }
}



