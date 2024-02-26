package gomoku.http.models.exception

import gomoku.http.models.ProblemJson
import java.net.URI

fun <T> userOrPasswordInvalid(username: T) = ProblemJson(
    URI("http://localhost:8080/errors/user_or_password_invalid"),
    "User or Password invalid",
    "Invalid password for user: $username",
    "/login"
)

fun <T> tokenNotFound(playerID: T) = ProblemJson(
    URI("http://localhost:8080/errors/tokenNotFound"),
    "Token not found",
    "Player token with id: $playerID not found.",
    "/login"
)