package gomoku.service

import gomoku.domainEntities.*
import org.springframework.stereotype.Component
import gomoku.repository.TransactionManager
import gomoku.utils.Error
import gomoku.utils.catchError
import java.util.UUID

@Component
class PlayersService(
        private val transactionManager: TransactionManager
) {
    fun getById(id: Int): PlayerAccessingResult =
            transactionManager.executeTransaction {
                try {
                    val player: User? = it.playersRepository.getById(id)
                    if (player == null) {
                        failure(Error.PlayerNotFound)
                    } else {
                        success(player)
                    }
                } catch (e: Exception) {
                    return@executeTransaction catchError(e)
                }
            }

    fun createPlayer(name: String, password: String): PlayerCreationResult =
            transactionManager.executeTransaction {
                try {
                    if (it.playersRepository.getByName(name) != null) {
                        failure(Error.PlayerAlreadyExists)
                    } else {
                        val player = it.playersRepository.createPlayer(name, password, PlayerState.IDLE)
                        it.informationRepository.addToStats(player.id)
                        val token = Token(UUID.randomUUID(), player.id)
                        it.playersRepository.createToken(token, 1)
                        success(token)
                    }
                } catch (e: Exception) {
                    return@executeTransaction catchError(e)
                }
            }

    fun getPlayerStats(id: Int): PlayerStatsResult =
            transactionManager.executeTransaction {
                try {
                    val stats = it.informationRepository.getPlayerStats(id)
                    success(stats)
                } catch (e: Exception) {
                    return@executeTransaction catchError(e)
                }
            }

    fun createToken(username: String, password: String): TokenCreationResult =
            transactionManager.executeTransaction {
                try {
                    val user = it.playersRepository.getByCredentials(username, password)
                    if (user == null)
                        failure(Error.UserOrPasswordInvalid)
                    else {

                        val tokenValue = UUID.randomUUID()
                        val token = Token(
                                token = tokenValue,
                                userID = user.id
                        )
                        it.playersRepository.createToken(token, 1)
                        success(token)
                    }
                } catch (e: Exception) {
                    return@executeTransaction catchError(e)
                }
            }

    fun removeToken(token: String): TokenRemoveResult =
            transactionManager.executeTransaction {
                try {
                    val res = it.playersRepository.removeToken(token)
                    if (res <= 0) failure(Error.TokenNotFound)
                    else success(true)
                } catch (e: Exception) {
                    return@executeTransaction catchError(e)
                }
            }

    fun getGameID(playerID: Int): GetGameIDByPlayerIDResult =
            transactionManager.executeTransaction {
                try {
                    val gameID = it.playersRepository.getGameID(playerID)
                    success(GameID(gameID ?: -1))
                } catch (e: Exception) {
                    return@executeTransaction catchError(e)
                }
            }

    fun getPlayerIDbyToken(token: String): Int =
            transactionManager.executeTransaction {
                val playerID = it.playersRepository.getPlayerID(token)
                return@executeTransaction playerID ?: -1
            }
}