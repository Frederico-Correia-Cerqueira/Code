package gomoku.service

import gomoku.domainEntities.*
import gomoku.domainEntities.failure
import gomoku.domainEntities.success
import gomoku.repository.TransactionManager
import gomoku.domainEntities.MatchmakingResult
import gomoku.utils.Error
import gomoku.utils.catchError
import org.springframework.stereotype.Component

@Component
class WaitingRoomService(
    private val transactionManager: TransactionManager
) {

    /**
     * Joins a game:
     * If there is any player waiting in the waiting room with the same openingRules, variants, and rank of the player
     * that is trying to play, the two players are paired up and the status game is changed to READY_TO_PLAY.
     * If there is no possible player waiting in the waiting room, the player is added to the waiting room and waits for
     * another player to join, here the game status will be WAITING.
     * @param playerID The ID of the player joining the game.
     * @param openingRules The openingRules choice of the player joining the game.
     * @param variants The variants choice of the player joining the game.
     * @return A `GameStatus` object containing the game info, including the IDs of the two players and the game status
     **/
    fun join(playerID: Int, openingRules: OpeningRules, variants: Variants): MatchmakingResult =
        transactionManager.executeTransaction {
            try {
                it.playersRepository.getTokenById(playerID)
                    ?: return@executeTransaction failure(Error.TokenNotFound)
                val player = it.playersRepository.getById(playerID)
                if (player == null) {
                    failure(Error.PlayerNotFound)
                } else {
                    if (player.state != PlayerState.IDLE) {
                        return@executeTransaction failure(Error.PlayerAlreadyInGame)
                    }
                    // get the player's rank
                    val rank = it.informationRepository.getPlayerStats(playerID).rank
                    // check if there's any possible opponent in the waiting room
                    val waiter = it.waitingRoomRepository.getAvailablePlayer(variants, openingRules, rank)
                    if (waiter == null) {
                        // if there's not, add the player to the waiting room and change player's state
                        it.waitingRoomRepository.addPlayerToWaitingRoom(playerID, variants, openingRules, rank)
                        it.playersRepository.updatePlayerState(playerID, PlayerState.WAITING)
                        success(Matchmaking(Status.WAITING, null))
                    } else {
                        // match the player's otherwise
                        it.waitingRoomRepository.removePlayerFromWaitingRoom(waiter.playerID)
                        it.playersRepository.updatePlayerState(playerID, PlayerState.IN_GAME)
                        it.playersRepository.updatePlayerState(waiter.playerID, PlayerState.IN_GAME)
                        val gameID = it.gamesRepository.insertGame(
                            playerID,
                            waiter.playerID,
                            initialBoard(playerID, waiter.playerID),
                            variants,
                            openingRules,
                        )
                        success(Matchmaking(Status.READY_TO_CREATE, gameID))
                    }
                }
            } catch (e: Exception) {
                return@executeTransaction catchError(e)
            }
        }

}

