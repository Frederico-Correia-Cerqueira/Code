package gomoku.domainEntities

const val WinCondition = 5

enum class Variants { NORMAL, SWAP_FIRST_MOVE }
enum class OpeningRules { NORMAL, SWAP }

data class Game(
    val id: Int, val playerB: Int, val playerW: Int, val board: Board,
    val variants: Variants, val openingRules: OpeningRules, val info: Boolean
)

data class GameID(val id: Int)
data class Info(val playerID: Int, val info: Boolean)
enum class Status { WAITING, READY_TO_CREATE }

data class Matchmaking(val status: Status, val gameID: Int?)

fun Game.newTurn() = if (this.board.turn == this.playerB) this.playerW else this.playerB

fun parseGameVariantString(typeStr: String): Variants {
    val type = Variants.values().find { it.name == typeStr }
    requireNotNull(type) { "Invalid Game Variant format: $typeStr" }
    return type
}

fun parseOpeningRulesString(rulesStr: String): OpeningRules {
    val rules = OpeningRules.values().find { it.name == rulesStr }
    requireNotNull(rules) { "Invalid Game Opening Rules format: $rulesStr" }
    return rules
}
