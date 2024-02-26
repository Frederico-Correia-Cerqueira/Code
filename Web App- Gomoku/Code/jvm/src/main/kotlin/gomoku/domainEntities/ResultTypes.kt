package gomoku.domainEntities

import gomoku.http.SavedGames
import gomoku.http.models.SaveGameOutputModel
import gomoku.utils.Error

typealias TokenCreationResult = Either<Error, Token>
typealias TokenRemoveResult = Either<Error, Boolean>

typealias PlayerCreationResult = Either<Error, Token>
typealias PlayerAccessingResult = Either<Error, User>
typealias GetGameIDByPlayerIDResult = Either<Error, GameID>
typealias MatchmakingResult = Either<Error, Matchmaking>

typealias GameResult = Either<Error, Game>
typealias GameSavingResult = Either<Error, GameID>
typealias SavedGamesResult = Either<Error, SavedGames>
typealias SavedGameResult = Either<Error, SaveGameOutputModel>
typealias DeleteGameResult = Either<Error, GameID>

typealias StatsResult = Either<Error, StatsList>
typealias PlayerStatsResult = Either<Error, Stats>

typealias InfoResult = Either<Error, Information>