package gomoku.http.models.exception

import gomoku.http.models.ProblemJson
import java.net.URI

fun <T> cantSwapInFirstMove(gameID: T) = ProblemJson(
    URI("http://localhost:8080/errors/cantSwapInFirstMove"),
    "Can't Swap in First Move",
    "Impossible to swap in First Move because game with id: $gameID has no information",
    "/games/$gameID/swap_first_move"
)

fun <T> cantSwap(gameID: T) = ProblemJson(
    URI("http://localhost:8080/errors/swap"),
    "Can't Swap ",
    "Impossible to swap because game with id: $gameID has no information",
    "/games/$gameID/swap"
)