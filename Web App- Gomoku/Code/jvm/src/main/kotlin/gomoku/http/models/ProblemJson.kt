package gomoku.http.models

import org.springframework.http.ResponseEntity
import java.net.URI

data class ProblemJson(
    val type: URI,
    val title: String,
    val detail: String,
    val instance: String,
) {

    companion object {
        private const val MEDIA_TYPE = "application/problem+json"
        fun response(status: Int, problem: ProblemJson) = ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body<Any>(problem)

        fun internalServerError() = ProblemJson(
            URI("http://localhost:8080/errors/internalServerError"),
            "Internal Server Error",
            "An internal server error occurred. Please try again later.",
            " "
        )
    }
}




