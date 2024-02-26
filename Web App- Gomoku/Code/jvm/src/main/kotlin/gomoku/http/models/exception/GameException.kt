package gomoku.http.models.exception

import gomoku.domainEntities.BOARD_DIM
import gomoku.http.models.ProblemJson
import java.net.URI

fun <T> gameNotFound(gameID: T) = ProblemJson(
    URI("http://localhost:8080/errors/gameNotFound"),
    "Game not found",
    "The game with id: $gameID not found.",
    "/games/$gameID"
)

fun <T> savedGameNotFound(saveID: T, pid: T) = ProblemJson(
    URI("http://localhost:8080/errors/savedGameNotFound"),
    "Saved game not found",
    "The save with id: $saveID and player id: $pid not found.",
    "/games/$saveID/savegame/$pid"
)

fun <T, R> playerNotInGame(gameID: T, playerID: R) = ProblemJson(
    URI("http://localhost:8080/errors/playerNotInGame"),
    "Player not in Game",
    "The player with id: $playerID is not in game (current game id: $gameID)",
    "/games/$gameID"
)

fun <T> gameInfoNotDefined(gameID: T) = ProblemJson(
    URI("http://localhost:8080/errors/gameInfoNotDefined"),
    "Game info not defined",
    "The game with id: $gameID has no defined information.",
    "/games/$gameID"
)

fun <T> impossibleToPlay(gameID: T) = ProblemJson(
    URI("http://localhost:8080/errors/impossibleToPlay"),
    "Impossible To Play",
    "Impossible to play because the game with id: $gameID has an information.",
    "/games/$gameID"
)

fun <T> gameOver(gameID: T) = ProblemJson(
    URI("http://localhost:8080/errors/gameOver"),
    "Game Over",
    "The game with id: $gameID is over.",
    "/games/$gameID"
)

fun <T, R> notYourTurn(gameID: T, playerID: R) = ProblemJson(
    URI("http://localhost:8080/errors/notYourTurn"),
    "Not your turn",
    "It's not the id: $playerID turn to play",
    "/games/$gameID"
)

fun <T, R> positionTaken(gameID: T, position: R) = ProblemJson(
    URI("http://localhost:8080/errors/positionTaken"),
    "Position Taken",
    "The position $position is taken",
    "/games/$gameID"
)

fun <T, R> invalidPositionFormat(gameID: T, position: R) = ProblemJson(
    URI("http://localhost:8080/errors/invalidPositionFormat"),
    "Invalid Position Format",
    "The position $position format is not right. Correct format: l-(0..$BOARD_DIM) and c-(0..$BOARD_DIM)",
    "/games/$gameID"
)
