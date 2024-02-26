package gomoku.http.models.siren

import gomoku.http.models.SirenModel
import gomoku.http.models.URL
import gomoku.http.models.siren
import java.net.URI

fun <T> waitingRoomResponse(properties: T): SirenModel<T> =
    siren(properties) {
        clazz("waitingRoom")
        entity(listOf("matchmaking"), listOf(URI("$URL/matchmaking")), URI("$URL/matchmaking"), "", emptyList()) {}
        action("matchmaking", emptyList(), "Join game", "POST", URI("$URL/matchmaking"), "") {}
    }