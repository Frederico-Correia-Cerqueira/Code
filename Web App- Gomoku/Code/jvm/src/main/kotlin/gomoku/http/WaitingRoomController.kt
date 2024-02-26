package gomoku.http

import gomoku.domainEntities.parseGameVariantString
import gomoku.domainEntities.parseOpeningRulesString
import gomoku.http.models.MatchmakingInputModel
import gomoku.domainEntities.Either
import gomoku.http.models.MEDIA_TYPE
import gomoku.http.models.ProblemJson
import gomoku.http.models.exception.playerAlreadyInGame
import gomoku.http.models.exception.playerNotFound
import gomoku.http.models.exception.tokenNotFound
import gomoku.http.models.siren.waitingRoomResponse
import gomoku.service.WaitingRoomService
import gomoku.utils.Error
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/matchmaking")
class WaitingRoomController(private val waitingRoomService: WaitingRoomService) {
    @PostMapping
    fun joinGame(@RequestBody matchmakingInput: MatchmakingInputModel): ResponseEntity<*> {
        val openingRules = parseOpeningRulesString(matchmakingInput.openingRules)
        val variants = parseGameVariantString(matchmakingInput.gameType)
        return when (val res = waitingRoomService.join(matchmakingInput.playerID, openingRules, variants)) {
            is Either.Right -> ResponseEntity.status(200).header("Content-Type", MEDIA_TYPE)
                .body(waitingRoomResponse(res.value))

            is Either.Left -> when (res.value) {
                Error.PlayerNotFound -> ProblemJson.response(404, playerNotFound(matchmakingInput.playerID))
                Error.TokenNotFound -> ProblemJson.response(404, tokenNotFound(matchmakingInput.playerID))
                Error.PlayerAlreadyInGame -> ProblemJson.response(404, playerAlreadyInGame(matchmakingInput.playerID))
                else -> ProblemJson.response(500, ProblemJson.internalServerError())
            }
        }
    }
}



