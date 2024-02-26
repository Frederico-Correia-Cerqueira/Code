package gomoku.http.models.siren

import gomoku.http.models.SirenModel
import gomoku.http.models.URL
import gomoku.http.models.siren
import java.net.URI

fun <T> getPlayerResponse(properties: T, playerID: Int): SirenModel<T> =
    siren(properties) {
        clazz("player")
        entity(listOf("player"), listOf(URI("$URL/player/$playerID")), URI("$URL/player/$playerID"), "", emptyList()) {}
        action("GetPlayer", emptyList(), "Get player", "GET", URI("$URL/players/$playerID"), "") {}
        link(emptyList(), listOf("stats"), URI("$URL/player/stats"), "Get stats", "") {}
        link(emptyList(), listOf("game"), URI("$URL/matchmaking"), "Join game", "") {}
        link(emptyList(), listOf("credits"), URI("$URL/credits"), "Version and Credits", "") {}
    }

fun <T> createPlayerResponse(properties: T): SirenModel<T> =
    siren(properties) {
        clazz("player")
        entity(listOf("player"), listOf(URI("$URL/player")), URI("$URL/player"), "", emptyList()) {}
        action("CreatePlayer", emptyList(), "Create player", "POST", URI("$URL/players"), "") {}
        link(emptyList(), listOf("login"), URI("$URL/login"), "Login", "") {}
        link(emptyList(), listOf("credits"), URI("$URL/credits"), "Version and Credits", "") {}
        link(emptyList(), listOf("stats"), URI("$URL/player/stats"), "Get stats", "") {}
    }

fun <T> getStatsResponse(properties: T): SirenModel<T> =
    siren(properties) {
        clazz("stats")
        entity(listOf("stats"), listOf(URI("$URL/players/stats")), URI("$URL/players/stats"), "", emptyList()) {}
        action("GetStats", emptyList(), "Get stats", "GET", URI("$URL/players/stats"), "") {}
        link(emptyList(), listOf("createPlayer"), URI("$URL/player"), "Create Player", "") {}
        link(emptyList(), listOf("login"), URI("$URL/login"), "Login", "") {}
        link(emptyList(), listOf("credits"), URI("$URL/credits"), "Version and Credits", "") {}
    }

fun <T> getGameIDResponse(properties: T): SirenModel<T> =
    siren(properties) {
    }