package gomoku.http.models.exception

import gomoku.http.models.ProblemJson
import java.net.URI

fun <T> invalidGameVariantFormat(gameID: T) = ProblemJson(
    URI("http://localhost:8080/errors/invalidGameVariantFormat"),
    "Invalid Game Variant Format",
    "The format of the game variant is not right. Correct format: NORMAL or SWAP_FIRST_MOVE.",
    "/matchmaking"
)

fun <T> invalidGameOpeningRulesFormat(gameID: T) = ProblemJson(
    URI("http://localhost:8080/errors/invalidGameOpeningRulesFormat"),
    "Invalid Game Opening Rules Format",
    "The format of the game opening rules is not right. Correct format: NORMAL or SWAP.",
    "/matchmaking"
)