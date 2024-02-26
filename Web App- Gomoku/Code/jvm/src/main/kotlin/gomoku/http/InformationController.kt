package gomoku.http

import gomoku.domainEntities.Either
import gomoku.http.models.MEDIA_TYPE
import gomoku.http.models.ProblemJson
import gomoku.http.models.siren.getStatsResponse
import gomoku.http.models.siren.informationResponse
import gomoku.service.InformationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class InformationController(val informationService: InformationService) {
    @GetMapping(PathTemplate.CREDITS)
    fun credits(): ResponseEntity<*> {

        return when (val res = informationService.credits()) {
            is Either.Right -> ResponseEntity.status(200).header("Content-Type", MEDIA_TYPE)
                .body(informationResponse(res.value))

            is Either.Left -> ProblemJson.response(500, ProblemJson.internalServerError())
        }
    }

    @GetMapping(PathTemplate.STATS)
    fun getStats(): ResponseEntity<*> {
        return when (val res = informationService.getAllStats()) {
            is Either.Right -> ResponseEntity.status(200).header("Content-Type", MEDIA_TYPE)
                    .body(getStatsResponse(res.value))

            is Either.Left -> ProblemJson.response(500, ProblemJson.internalServerError())
        }
    }
}