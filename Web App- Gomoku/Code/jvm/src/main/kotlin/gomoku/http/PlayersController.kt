package gomoku.http

import gomoku.domainEntities.Either
import gomoku.http.models.MEDIA_TYPE
import gomoku.http.models.PlayerInputModel
import gomoku.http.models.ProblemJson
import gomoku.http.models.exception.playerAlreadyExist
import gomoku.http.models.exception.playerNotFound
import gomoku.http.models.siren.createPlayerResponse
import gomoku.http.models.siren.getGameIDResponse
import gomoku.http.models.siren.getPlayerResponse
import gomoku.http.models.siren.getStatsResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import gomoku.service.PlayersService
import gomoku.utils.Error
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@RestController
@RequestMapping("/api/players")
class PlayersController(val playerServices: PlayersService) {
    @GetMapping(PathTemplate.USER_BY_ID)
    fun getPlayer(@PathVariable id: Int): ResponseEntity<*> {
        return when (val res = playerServices.getById(id)) {
            is Either.Right -> ResponseEntity.status(200).header("Content-Type", MEDIA_TYPE)
                    .body(getPlayerResponse(res.value, id))

            is Either.Left -> when (res.value) {
                Error.PlayerNotFound -> ProblemJson.response(404, playerNotFound(id))
                else -> ProblemJson.response(500, ProblemJson.internalServerError())
            }
        }
    }

    @PostMapping
    fun createPlayer(@RequestBody player: PlayerInputModel): ResponseEntity<*> {
        return when (val res = playerServices.createPlayer(player.name, player.password)) {
            is Either.Right -> {
                val authCookie = ResponseCookie
                        .from("auth", "${res.value.token}")
                        .httpOnly(true)
                        .secure(false)
                        .build()

                ResponseEntity.status(201)
                        .header("Content-Type", MEDIA_TYPE)
                        .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                        .body(createPlayerResponse(res.value))

            }

            is Either.Left -> when (res.value) {
                Error.PlayerAlreadyExists -> ProblemJson.response(409, playerAlreadyExist(player.name))
                else -> ProblemJson.response(500, ProblemJson.internalServerError())
            }
        }
    }

    @GetMapping(PathTemplate.PLAYER_STATS)
    fun getPlayerStats(@PathVariable id: Int): ResponseEntity<*> {
        return when (val res = playerServices.getPlayerStats(id)) {
            is Either.Right -> ResponseEntity.status(200).header("Content-Type", MEDIA_TYPE)
                    .body(getStatsResponse(res.value))

            is Either.Left -> ProblemJson.response(500, ProblemJson.internalServerError())
        }
    }

    @GetMapping(PathTemplate.GET_GAME_ID)
    fun getGameID(@PathVariable id: Int): ResponseEntity<*> {
        return when (val res = playerServices.getGameID(id)) {
            is Either.Right -> ResponseEntity.status(200).header("Content-Type", MEDIA_TYPE)
                    .body(getGameIDResponse(res.value))

            is Either.Left -> ProblemJson.response(500, ProblemJson.internalServerError())
        }
    }

}
