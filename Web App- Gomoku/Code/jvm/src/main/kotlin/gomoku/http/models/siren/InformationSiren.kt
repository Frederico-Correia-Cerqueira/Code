package gomoku.http.models.siren

import gomoku.http.models.SirenModel
import gomoku.http.models.URL
import gomoku.http.models.siren
import java.net.URI

fun <T> informationResponse(properties: T): SirenModel<T> =
    siren(properties) {
        clazz("credits")
        entity(listOf("credits"), listOf(URI("$URL/credits")), URI("$URL/credits"), "", emptyList()) {}
        action("getCredits", emptyList(), "Credits", "GET", URI("$URL/credits"), "") {}
        link(emptyList(), listOf("createPlayer"), URI("$URL/player"), "Create Player", "") {}
        link(emptyList(), listOf("login"), URI("$URL/login"), "Login", "") {}
        link(emptyList(), listOf("stats"), URI("$URL/player/stats"), "Get stats", "") {}
    }