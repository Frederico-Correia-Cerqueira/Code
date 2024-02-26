package gomoku.domainEntities

enum class Pieces { N, W }

/**
 * The Square type identifies a position on the board (Column and Row)
 * Squares are identified by two digits
 * The top left is "0|0"
 */
class Square private constructor(val row: Row, val column: Column) {

    companion object {
        private val values =
            List(BOARD_DIM * BOARD_DIM) { idx -> Square(Row.values[idx / BOARD_DIM], Column.values[idx % BOARD_DIM]) }

        operator fun invoke(r: Int, c: Int) = values.first { it.row.number == r && it.column.number == c }
        operator fun invoke(r: Row, c: Column) = values.first { it.row == r && it.column == c }

        fun fromString(squareStr: String): Square {
            val parts = squareStr.split(":")
            if (parts.size == 2) {
                val row = parts[0].toInt()
                val column = parts[1].toInt()
                return Square(row, column)
            } else {
                throw IllegalArgumentException("Invalid Square Format: $squareStr")
            }
        }
    }

    override fun toString() = "(${row.number}-${column.number})"
}

data class SquareDetails(val player: Int, val type: Pieces) {
    companion object {
        fun fromString(squareDetailsStr: String): SquareDetails {
            val parts = squareDetailsStr.split(":")
            if (parts.size == 2) {
                val player = parts[0].toInt()
                val type = when (parts[1]) {
                    "N" -> Pieces.N
                    "W" -> Pieces.W
                    else -> throw IllegalArgumentException("Invalid part type: ${parts[1]}")
                }
                return SquareDetails(player, type)
            } else {
                throw IllegalArgumentException("Invalid Square Details format: $squareDetailsStr")
            }
        }
    }
}


