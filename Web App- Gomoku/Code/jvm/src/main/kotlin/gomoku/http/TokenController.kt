package gomoku.http

import gomoku.domainEntities.Either
import gomoku.http.models.*
import gomoku.http.models.exception.tokenNotFound
import gomoku.http.models.exception.userOrPasswordInvalid
import gomoku.http.models.siren.tokenResponse
import gomoku.service.PlayersService
import gomoku.utils.Error
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


data class PlayerLogoutModel(val token: String)

@RestController
@RequestMapping("/api")
class TokenController(val playerServices: PlayersService) {
    @PostMapping(PathTemplate.LOGIN)
    fun login(@RequestBody player: PlayerInputModel): ResponseEntity<*> {
        when (val res = playerServices.createToken(player.name, player.password)) {
            is Either.Right -> {
                val authCookie = ResponseCookie
                        .from("auth", "${res.value.token}")
                        .httpOnly(true)
                        .secure(true)
                        .build()

                return ResponseEntity.status(200)
                        .header("Content-Type", MEDIA_TYPE)
                        .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                        .body(tokenResponse(UserTokenCreateOutputModel(token = res.value.token.toString(), userID = res.value.userID)))
            }

            is Either.Left -> return when (res.value) {
                Error.UserOrPasswordInvalid -> ProblemJson.response(400, userOrPasswordInvalid(player.name))
                else -> ProblemJson.response(500, ProblemJson.internalServerError())
            }
        }
    }

    @PostMapping(PathTemplate.LOGOUT)
    fun logout(@RequestBody token: PlayerLogoutModel): ResponseEntity<*> {
        val authCookie = ResponseCookie
                .from("auth", token.token)
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .build()

        return when (val res = playerServices.removeToken(token.token)) {
            is Either.Right -> ResponseEntity.status(200).header("Content-Type", MEDIA_TYPE)
                    .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                    .body(tokenResponse(UserTokenRemoveOutputModel(sucess = res.value)))

            is Either.Left -> when (res.value) {
                Error.TokenNotFound -> ProblemJson.response(404, tokenNotFound(token))
                else -> ProblemJson.response(500, ProblemJson.internalServerError())
            }
        }
    }

    @GetMapping(PathTemplate.GET_AUTH_COOKIE)
    fun readCookie(@CookieValue(name = "auth") token: String?): ResponseEntity<*> {
        val res = if (token != null) {
            val playerID = playerServices.getPlayerIDbyToken(token)
            if (playerID != -1) {
                UserTokenValidationOutputModel(token, true, playerID)
            } else {
                UserTokenValidationOutputModel(" ", false, -1)
            }
        } else {
            UserTokenValidationOutputModel(" ", false, -1)
        }

        return ResponseEntity
                .status(200)
                .header("Content-Type", MEDIA_TYPE)
                .body(tokenResponse(res))
    }

}