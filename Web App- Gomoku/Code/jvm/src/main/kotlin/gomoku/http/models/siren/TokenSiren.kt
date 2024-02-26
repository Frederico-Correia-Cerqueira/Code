package gomoku.http.models.siren

import gomoku.http.models.SirenModel
import gomoku.http.models.URL
import gomoku.http.models.siren
import java.net.URI

fun <T> tokenResponse(properties: T): SirenModel<T> =
    siren(properties) {
        clazz("token")
        entity(listOf("login"), listOf(URI("$URL/login")), URI("$URL/login"), "", emptyList()) {}
        action("login", emptyList(), "Login", "POST", URI("$URL/login"), "") {
            textField("name", emptyList(), "Name", "")
            passwordField("password", emptyList(), "Password", "")
        }
        link(emptyList(), listOf("createPlayer"), URI("$URL/player"), "Create Player", "") {}
        link(emptyList(), listOf("credits"), URI("$URL/credits"), "Version and Credits", "") {}
        link(emptyList(), listOf("stats"), URI("$URL/player/stats"), "Get stats", "") {}
    }