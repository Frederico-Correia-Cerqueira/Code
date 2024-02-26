package gomoku.domainEntities

import java.util.*

typealias Moves = Map<Square, SquareDetails>
enum class PlayerState { IDLE, WAITING, IN_GAME }
data class PlayerData(val token: UUID, val id: Int, val username: String, val state: PlayerState)
data class Waiter(val id: Int, val playerID: Int)

fun parsePlayerStateString(playerState: String): PlayerState {
    return when (playerState) {
        "IDLE" -> PlayerState.IDLE
        "WAITING" -> PlayerState.WAITING
        else -> PlayerState.IN_GAME
    }
}

