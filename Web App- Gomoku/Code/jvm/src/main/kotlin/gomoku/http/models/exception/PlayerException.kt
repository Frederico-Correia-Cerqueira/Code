package gomoku.http.models.exception

import gomoku.http.models.ProblemJson
import java.net.URI

fun <T> playerAlreadyExist(username: T) = ProblemJson(
    URI("http://localhost:8080/errors/playerAlreadyExists"),
    "Player already exists",
    "The player with username: $username already exists.",
    "/players",
)

fun <T> playerNotFound(playerID: T) = ProblemJson(
    URI("http://localhost:8080/errors/playerNotFound"),
    "Player not found",
    "Player with id: $playerID not found.",
    "/players/$playerID"
)

fun <T> playerAlreadyInGame(playerID: T) = ProblemJson(
    URI("http://localhost:8080/errors/playerAlreadyInGame"),
    "Player is already in Game or in the waiting room",
    "Player with id: $playerID already in Game or in the waiting room.",
    "/players/$playerID"
)