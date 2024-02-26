package pt.isel.ls.http

import org.http4k.core.Status

/**
 * implementation of the http response errors *
 */

fun httpResponseErrors(e: IllegalStateException): Status =
    when (e.message) {
        "INVALID_PARAMETER", "EMAIL_NOT_FOUND", "PASSWORD_NOT_FOUND", "ID_NOT_VALID", "LIST_NOT_VALID",
        "CARD_NOT_VALID", "EXISTING_USER", "EXISTING_BOARD", "EXISTING_LIST",
        "EMAIL_NOT_VALID", "EXISTING_USER_AT_BOARD" ->
            Status(400, " BAD_REQUEST: ${e.message}")

        "ID_NOT_FOUND", "USER_NOT_FOUND", "BOARD_NOT_FOUND", "BOARDS_NOT_FOUND", "LIST_NOT_FOUND", "CARD_NOT_FOUND" ->
            Status(404, " NOT_FOUND: ${e.message}")

        else -> Status(500, "INTERNAL_SERVER_ERROR")
    }
