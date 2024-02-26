package gomoku.utils

import gomoku.domainEntities.Either
import gomoku.domainEntities.failure

sealed class Error {
    // Game
    object GameNotFound : Error()
    object SavedGameNotFound : Error()
    object PlayerNotInGame : Error()
    object GameInfoNotDefined : Error()
    object ImpossibleToPlay : Error()
    object GameOver : Error()
    object NotYourTurn : Error()
    object PositionTaken : Error()
    object InvalidPositionFormat : Error()

    // Player
    object PlayerAlreadyExists : Error()
    object PlayerNotFound : Error()
    object PlayerAlreadyInGame : Error()

    object TokenNotFound : Error()

    // Token
    object UserOrPasswordInvalid : Error()

    // Swap
    object CantSwapFirstMove : Error()
    object CantSwap : Error()

    // Server
    object InternalServerError : Error()
}

fun catchError(e: Exception): Either.Left<Error> =
    when (e) {
        is GameOver -> failure(Error.GameOver)
        is NotYourTurn -> failure(Error.NotYourTurn)
        is PositionTaken -> failure(Error.PositionTaken)
        else -> failure(Error.InternalServerError)
    }
