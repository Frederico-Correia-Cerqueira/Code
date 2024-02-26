package gomoku.http

import gomoku.domainEntities.Either
import gomoku.domainEntities.Info
import gomoku.http.models.*
import gomoku.http.models.exception.*
import gomoku.http.models.siren.*
import org.springframework.web.bind.annotation.*
import gomoku.service.GamesService
import gomoku.utils.Error
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/api/games")
class GamesController(private val gamesService: GamesService) {

    @GetMapping(PathTemplate.GAME_BY_ID)
    fun getGame(@PathVariable id: Int): ResponseEntity<*> {
        return when (val res = gamesService.getGame(id)) {
            is Either.Right -> ResponseEntity.status(200).header("Content-Type", MEDIA_TYPE)
                .body(getGameResponse(res.value,id))
            is Either.Left -> when (res.value) {
                Error.GameNotFound -> ProblemJson.response(404, gameNotFound(id))
                else -> ProblemJson.response(500, ProblemJson.internalServerError())
            }
        }
    }

    @PostMapping(PathTemplate.PLAY)
    fun play(@PathVariable id: Int, @RequestBody p: GamePlayInputModel): ResponseEntity<*> {
        val playRes = gamesService.play(id, p.playerID, p.l, p.c)
        if (playRes is Either.Left) {
            return when (playRes.value) {
                Error.PlayerNotFound -> ProblemJson.response(404, playerNotFound(p.playerID))
                Error.TokenNotFound -> ProblemJson.response(404, tokenNotFound(p.playerID))
                Error.PlayerNotInGame -> ProblemJson.response(400, playerNotInGame(id, p.playerID))
                Error.GameNotFound -> ProblemJson.response(404, gameNotFound(id))
                Error.InvalidPositionFormat-> ProblemJson.response(400, invalidPositionFormat(id,"(${p.l}-${p.c})"))
                Error.PositionTaken-> ProblemJson.response(400, positionTaken(id,"(${p.l}-${p.c})"))
                Error.GameOver -> ProblemJson.response(400, gameOver(id))
                Error.NotYourTurn -> ProblemJson.response(400, notYourTurn(id, p.playerID))
                Error.ImpossibleToPlay->ProblemJson.response(400, impossibleToPlay(id))
                else -> ProblemJson.response(500, ProblemJson.internalServerError())
            }
        }
        return when (val res = gamesService.gameInfo(id)) {
            is Either.Right -> ResponseEntity.status(201).header("Content-Type", MEDIA_TYPE)
                .body(playResponse(res.value,id))
            is Either.Left -> when (res.value) {
                Error.GameNotFound -> ProblemJson.response(404, gameNotFound(id))
                else -> ProblemJson.response(500, ProblemJson.internalServerError())
            }
        }
    }


    @PostMapping(PathTemplate.SWAP)
    fun swap(@PathVariable id: Int, @RequestBody info: Info): ResponseEntity<*> {
        return when (val res = gamesService.swap(id, info.info,info.playerID)) {
            is Either.Right -> ResponseEntity.status(201).header("Content-Type", MEDIA_TYPE)
                .body(swapResponse(res.value,id) )
            is Either.Left -> when (res.value) {
                Error.GameNotFound -> ProblemJson.response(404, gameNotFound(id))
                Error.GameInfoNotDefined -> ProblemJson.response(400,gameInfoNotDefined(id))
                Error.CantSwap->ProblemJson.response(400, cantSwap(id))
                Error.NotYourTurn -> ProblemJson.response(400, notYourTurn(id, info.playerID))
                else -> ProblemJson.response(500, ProblemJson.internalServerError())
            }
        }
    }

    @PostMapping(PathTemplate.SWAP_FIRST_MOVE)
    fun swapFirstMove(@PathVariable id: Int, @RequestBody info: Info): ResponseEntity<*> {
        return when (val res = gamesService.swapFirstMove(id, info.info,info.playerID)) {
            is Either.Right -> ResponseEntity.status(201).header("Content-Type", MEDIA_TYPE)
                .body(swapFirstMoveResponse(res.value,id))
            is Either.Left -> when (res.value) {
                Error.GameNotFound -> ProblemJson.response(404, gameNotFound(id))
                Error.GameInfoNotDefined -> ProblemJson.response(400, gameInfoNotDefined(id))
                Error.CantSwapFirstMove -> ProblemJson.response(400, cantSwapInFirstMove(id))
                Error.NotYourTurn -> ProblemJson.response(400, notYourTurn(id, info.playerID))
                else -> ProblemJson.response(500, ProblemJson.internalServerError())
            }
        }
    }

    @PostMapping(PathTemplate.SAVE_GAME)
    fun saveGame(@PathVariable id: Int, @PathVariable pid: Int, @RequestBody game_info: SaveGameInputModel): ResponseEntity<*> {
        return when (val res = gamesService.saveGame(id, pid, game_info.name, game_info.description)) {
            is Either.Right -> ResponseEntity.status(201).header("Content-Type", MEDIA_TYPE)
                .body(saveGameResponse(res.value,id,pid))

            is Either.Left -> when (res.value) {
                Error.PlayerNotFound -> ProblemJson.response(400,playerNotFound(pid))
                Error.GameNotFound -> ProblemJson.response(404, gameNotFound(id))
                else -> ProblemJson.response(500, ProblemJson.internalServerError())
            }
        }
    }

    @DeleteMapping(PathTemplate.DELETE_GAME)
    fun deleteGame(@PathVariable id: Int, @PathVariable pid: Int): ResponseEntity<*> {
        return when (val res = gamesService.deleteGame(id, pid)) {
            is Either.Right -> ResponseEntity.status(200).header("Content-Type", MEDIA_TYPE)
                .body(deleteGameResponse(res.value,id,pid))


    is Either.Left -> when (res.value) {
                Error.GameNotFound -> ProblemJson.response(404, gameNotFound(id))
                else -> ProblemJson.response(500, ProblemJson.internalServerError())
            }
        }
    }

    @GetMapping(PathTemplate.SAVE_GAME)
    fun getSaveGameById(@PathVariable sid: Int, @PathVariable pid: Int): ResponseEntity<*> {
        return when (val res = gamesService.getSavedGameById(sid, pid)) {
            is Either.Right -> ResponseEntity.status(200).header("Content-Type", MEDIA_TYPE)
                .body(getSaveGameResponse(res.value,sid,pid))
            is Either.Left -> when (res.value) {
                Error.SavedGameNotFound -> ProblemJson.response(404, savedGameNotFound(sid, pid))
                else -> ProblemJson.response(500, ProblemJson.internalServerError())
            }
        }
    }

    @PostMapping(PathTemplate.SAVED_GAMES_BY_NAME)
    fun getSaveGameByName(@PathVariable pid: Int, @RequestBody name: SaveGameInputModel): ResponseEntity<*> {
        return when (val res = gamesService.getSavedGameByName(name.name, pid)) {
            is Either.Right -> ResponseEntity.status(200).header("Content-Type", MEDIA_TYPE)
                .body(getSavedGamesResponse(res.value, pid))
            is Either.Left -> when (res.value) {
                Error.SavedGameNotFound -> ProblemJson.response(404, savedGameNotFound(name.name, pid))
                else -> ProblemJson.response(500, ProblemJson.internalServerError())
            }
        }
    }

    @GetMapping(PathTemplate.SAVED_GAMES_BY_PLAYER)
    fun getPlayerSavedGames(@PathVariable pid: Int): ResponseEntity<*> {
        return when (val res = gamesService.getPlayerSavedGames(pid)) {
            is Either.Right -> ResponseEntity.status(200).header("Content-Type", MEDIA_TYPE)
                .body(getSavedGamesResponse(res.value,pid))

            is Either.Left -> when (res.value) {
                Error.GameNotFound -> ProblemJson.response(404, gameNotFound(pid))
                else -> ProblemJson.response(500, ProblemJson.internalServerError())
            }
        }
    }
}

data class SavedGames(val games: List<SaveGameOutputModel>)


