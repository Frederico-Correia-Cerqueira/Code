package gomoku.service

import gomoku.domainEntities.*
import gomoku.domainEntities.failure
import gomoku.domainEntities.success
import gomoku.http.SavedGames
import gomoku.http.models.SaveGameOutputModel
import gomoku.repository.TransactionManager
import gomoku.utils.*
import org.springframework.stereotype.Component
import kotlin.math.pow

@Component
class GamesService(
    private val transactionManager: TransactionManager
) {
    fun getGame(id: Int): GameResult =
        transactionManager.executeTransaction {
            try {
                val game = it.gamesRepository.getById(id)
                if (game == null) {
                    failure(Error.GameNotFound)
                } else {
                    success(game)
                }
            } catch (e: Exception) {
                return@executeTransaction catchError(e)
            }
        }

    fun play(id: Int, playerID: Int, l: Int, c: Int): GameResult =
        transactionManager.executeTransaction {
            if (l !in 0..BOARD_DIM || c !in 0..BOARD_DIM) return@executeTransaction failure(Error.InvalidPositionFormat)
            it.playersRepository.getTokenById(playerID) ?: return@executeTransaction failure(Error.TokenNotFound)
            it.playersRepository.getById(playerID) ?: return@executeTransaction failure(Error.PlayerNotFound)
            val game = it.gamesRepository.getById(id)
            if (game == null) {
                return@executeTransaction failure(Error.GameNotFound)
            } else {
                if (playerID != game.playerB && playerID != game.playerW) {
                    return@executeTransaction failure(Error.PlayerNotInGame)
                }
                if (game.info) return@executeTransaction failure(Error.ImpossibleToPlay)
                try {
                    val updatedBoard = game.play(Square(l, c), playerID)
                    val updatedGame = it.gamesRepository.update(id, updatedBoard)
                    // Update player's stats if the game is over
                    if (updatedBoard.type == BoardTypes.BOARD_WIN) {
                        val turn = updatedBoard.turn
                        val opponent = updatedGame.newTurn()
                        /**
                         * updatePlayersStateAfterGame(playerID, updatedGame.newTurn())
                         */
                        it.playersRepository.updatePlayerState(turn, PlayerState.IDLE)
                        it.playersRepository.updatePlayerState(opponent, PlayerState.IDLE)

                        /**
                         * updateStatsAfterGame(playerID, updatedGame.newTurn())
                         */
                        val oldStatsWinner = it.informationRepository.getPlayerStats(turn)
                        val oldStatsLoser = it.informationRepository.getPlayerStats(opponent)


                        //updatePlayerStats(winProb(oldStatsWinner.elo, oldStatsLoser.elo), oldStatsWinner, true)
                        it.informationRepository.updateStats(
                            turn,
                            updatePlayerStats(winProb(oldStatsWinner.elo, oldStatsLoser.elo), oldStatsWinner, true)
                        )
                        //updatePlayerStats(winProb(oldStatsLoser.elo, oldStatsWinner.elo), oldStatsLoser, false)
                        it.informationRepository.updateStats(
                            opponent,
                            updatePlayerStats(winProb(oldStatsLoser.elo, oldStatsWinner.elo), oldStatsLoser, false)
                        )
                    }
                    success(updatedGame)
                } catch (e: Exception) {
                    return@executeTransaction catchError(e)
                }
            }
        }

    fun gameInfo(id: Int): GameResult =
        transactionManager.executeTransaction {
            try {
                val game = it.gamesRepository.getById(id)
                if (game == null) {
                    failure(Error.GameNotFound)
                } else {
                    when {
                        game.openingRules == OpeningRules.SWAP && game.board.moves.size == 3
                        -> success(it.gamesRepository.updateInfo(id, true))

                        game.variants == Variants.SWAP_FIRST_MOVE && game.board.moves.size == 1
                        -> success(it.gamesRepository.updateInfo(id, true))

                        else
                        -> success(it.gamesRepository.updateInfo(id, false))
                    }
                }
            } catch (e: Exception) {
                return@executeTransaction catchError(e)
            }
        }

    fun swap(id: Int, response: Boolean, playerID: Int): GameResult =
        transactionManager.executeTransaction {
            try {
                val game = it.gamesRepository.getById(id)
                if (game == null) {
                    failure(Error.GameNotFound)
                } else {
                    if (game.board.turn != playerID) return@executeTransaction failure(Error.NotYourTurn)
                    if (game.openingRules != OpeningRules.SWAP) return@executeTransaction failure(Error.CantSwap)
                    if (!game.info) return@executeTransaction failure(Error.GameInfoNotDefined)
                    val updatedBoard = game.swap(response)
                    it.gamesRepository.update(id, updatedBoard)
                    success(it.gamesRepository.updateInfo(id, false))
                }
            } catch (e: Exception) {
                return@executeTransaction catchError(e)
            }
        }

    fun swapFirstMove(id: Int, response: Boolean, playerID: Int): GameResult =
        transactionManager.executeTransaction {
            try {
                val game = it.gamesRepository.getById(id)
                if (game == null) {
                    failure(Error.GameNotFound)
                } else {
                    if (game.board.turn != playerID) return@executeTransaction failure(Error.NotYourTurn)
                    if (!game.info) return@executeTransaction failure(Error.GameInfoNotDefined)
                    if (game.variants != Variants.SWAP_FIRST_MOVE) return@executeTransaction failure(Error.CantSwapFirstMove)
                    val updatedBoard = game.swapFirstMove(response)
                    it.gamesRepository.update(id, updatedBoard)
                    success(it.gamesRepository.updateInfo(id, false))
                }
            } catch (e: Exception) {
                return@executeTransaction catchError(e)
            }
        }

    fun saveGame(id: Int, playerID: Int, gameName: String, gameDesc: String): GameSavingResult =
        transactionManager.executeTransaction {
            try {
                it.playersRepository.getById(playerID) ?: return@executeTransaction failure(Error.PlayerNotFound)
                val game = it.gamesRepository.getById(id) ?: return@executeTransaction failure(Error.GameNotFound)

                success(GameID(it.gamesRepository.saveGame(game.id, playerID, gameName, gameDesc)))
            } catch (e: Exception) {
                return@executeTransaction catchError(e)
            }
        }

    fun deleteGame(id: Int, pid: Int): DeleteGameResult =
        transactionManager.executeTransaction {
            try {
                it.gamesRepository.getSaveGameById(id, pid) ?: failure(Error.GameNotFound)
                val delGame = it.gamesRepository.deleteGame(id, pid)
                success(GameID(delGame))
            } catch (e: Exception) {
                return@executeTransaction catchError(e)
            }
        }

    fun getSavedGameById(sid: Int, pid: Int): SavedGameResult =
        transactionManager.executeTransaction {
            try {
                val saved: SaveGameOutputModel = it.gamesRepository.getSaveGameById(sid, pid) ?:
                return@executeTransaction  failure(Error.SavedGameNotFound)
                success(saved)

            } catch (e: Exception) {
                return@executeTransaction catchError(e)
            }
        }

    fun getSavedGameByName(sname: String, pid: Int): SavedGamesResult =
        transactionManager.executeTransaction {
            try {
                val saved: List<SaveGameOutputModel> = it.gamesRepository.getSaveGameByName(sname, pid) ?: return@executeTransaction  failure(Error.SavedGameNotFound)
                success(SavedGames(saved))

            } catch (e: Exception) {
                return@executeTransaction catchError(e)
            }
        }

    fun getPlayerSavedGames(playerID: Int): SavedGamesResult =
        transactionManager.executeTransaction {
            try {
                val games = it.gamesRepository.getPlayerSavedGames(playerID)
                success(SavedGames(games))
            } catch (e: Exception) {
                return@executeTransaction catchError(e)
            }
        }

    /** All values hardcoded below are related to the elo algorithm developed for this project **/
    private fun updatePlayerStats(winProb: Double, oldStats: Stats, winner: Boolean): Stats {
        val victories: Int
        val defeats: Int
        val newEloValue: Double
        if (winner) {
            victories = oldStats.victories + 1
            defeats = oldStats.defeats
            newEloValue = oldStats.elo + 10 * (1 - winProb)
        } else {
            victories = oldStats.victories
            defeats = oldStats.defeats + 1
            newEloValue =
                if ((oldStats.elo - 10 * (1 - winProb)) < 0) 0.0 else oldStats.elo - 10 * (1 - winProb)
        }
        val totalGames = oldStats.totalGames + 1
        val winRate = ((victories.toDouble() / totalGames.toDouble()) * 100).toInt()
        return Stats(
            player = oldStats.player,
            elo = newEloValue,
            victories = victories,
            defeats = defeats,
            totalGames = totalGames,
            winRate = winRate,
            rank = updateRank(newEloValue)
        )
    }

    private fun winProb(r1: Double, r2: Double): Double = 1 / (1 + (10.0.pow((r2 - r1) / 400)))
    private fun updateRank(elo: Double): Rank =
        when (elo) {
            in 0.0..50.0 -> Rank.UNRANKED
            in 50.1..100.0 -> Rank.NOOB
            in 100.1..300.0 -> Rank.BEGINNER
            in 300.1..500.0 -> Rank.INTERMEDIATE
            in 500.1..1000.0 -> Rank.EXPERT
            else -> Rank.PRO
        }
}