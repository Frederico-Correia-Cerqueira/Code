package gomoku.http.models.siren

import gomoku.http.models.SirenModel
import gomoku.http.models.URL
import gomoku.http.models.siren
import java.net.URI

fun <T> getGameResponse(properties: T, gameID: Int): SirenModel<T> =
    siren(properties) {
        clazz("game")
        entity(listOf("game"), listOf(URI("$URL/games/$gameID")), URI("$URL/games/$gameID"), "", emptyList()) {}
        action("getGame", emptyList(), "Get game", "GET", URI("$URL/games/$gameID"), "") {}
        action("play", emptyList(), "Play", "POST", URI("$URL/games/$gameID"), "") {}
        action("swap", emptyList(), "Swap", "POST", URI("$URL/games/$gameID/swap"), "") {}
        action(
            "swapFirstMove",
            emptyList(),
            " Swap In First Move",
            "POST",
            URI("$URL/games/$gameID/swap_first_move"),
            ""
        ) {}
    }

fun <T> playResponse(properties: T, gameID: Int): SirenModel<T> =
    siren(properties) {
        clazz("game")
        entity(listOf("play"), listOf(URI("$URL/games/$gameID")), URI("$URL/games/$gameID"), "", emptyList()) {}
        action("play", emptyList(), "Play", "POST", URI("$URL/games/$gameID"), "") {}
    }

fun <T> swapResponse(properties: T, gameID: Int): SirenModel<T> =
    siren(properties) {
        clazz("game")
        entity(
            listOf("swap"),
            listOf(URI("$URL/games/$gameID/swap")),
            URI("$URL/games/$gameID/swap"),
            "",
            emptyList()
        ) {}
        action("swap", emptyList(), "Swap", "POST", URI("$URL/games/$gameID/swap"), "") {}
    }

fun <T> swapFirstMoveResponse(properties: T, gameID: Int): SirenModel<T> =
    siren(properties) {
        clazz("game")
        entity(
            listOf("swapFirstMove"),
            listOf(URI("$URL/games/$gameID/swap_first_move")),
            URI("$URL/games/$gameID/swap_first_move"),
            "",
            emptyList()
        ) {}
        action(
            "swapFirstMove",
            emptyList(),
            "Swap In First Move",
            "POST",
            URI("$URL/games/$gameID/swap_first_move"),
            ""
        ) {}
    }

fun <T> saveGameResponse(properties: T, gameID: Int, playerID: Int): SirenModel<T> =
    siren(properties) {
        clazz("game")
        entity(
            listOf("saveGame"),
            listOf(URI("$URL/games/$gameID/savegame/$playerID")),
            URI("$URL/games/$gameID/savegame/$playerID"),
            "",
            emptyList()
        ) {}
        action("saveGame", emptyList(), "Save Game", "POST", URI("$URL/games/$gameID/savegame/$playerID"), "") {}
    }

fun <T> deleteGameResponse(properties: T, gameID: Int, playerID: Int): SirenModel<T> =
    siren(properties) {
        clazz("game")
        entity(
            listOf("deleteGame"),
            listOf(URI("$URL/games/$gameID/deletegame/$playerID")),
            URI("$URL/games/$gameID/deletegame/$playerID"),
            "",
            emptyList()
        ) {}
        action(
            "deleteGame",
            emptyList(),
            "Delete Game",
            "DELETE",
            URI("$URL/games/$gameID/deletegame/$playerID"),
            ""
        ) {}
    }

fun <T> getSaveGameResponse(properties: T, gameID: Int, playerID: Int): SirenModel<T> =
    siren(properties) {
        clazz("game")
        entity(
            listOf("saveGame"),
            listOf(URI("$URL/games/$gameID/savegame/$playerID")),
            URI("$URL/games/$gameID/savegame/$playerID"),
            "",
            emptyList()
        ) {}
        action("saveGame", emptyList(), "Save Game", "GET", URI("$URL/games/$gameID/savegame/$playerID"), "") {}
        link(emptyList(), listOf("getPlayer"), URI("$URL/player/$playerID"), "Get Player", "") {}
    }

fun <T> getSavedGamesResponse(properties: T, playerID: Int): SirenModel<T> =
    siren(properties) {
        clazz("games")
        entity(
            listOf("games"),
            listOf(URI("$URL/games/savegame/$playerID")),
            URI("$URL/games/savegame/$playerID"),
            "",
            emptyList()
        ) {}
        action("GetSavedGamesd", emptyList(), "Get Saved Games", "GET", URI("$URL/games/savegame/$playerID"), "") {}
        link(emptyList(), listOf("getPlayer"), URI("$URL/player/$playerID"), "Get Player", "") {}
    }

