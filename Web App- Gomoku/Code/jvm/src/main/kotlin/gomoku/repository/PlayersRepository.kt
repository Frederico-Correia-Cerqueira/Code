package gomoku.repository

import gomoku.domainEntities.PlayerState
import gomoku.domainEntities.Token
import gomoku.domainEntities.User

interface PlayersRepository {
    fun getById(id: Int): User?
    fun getByName(username: String): User?
    fun getByCredentials(username: String, password: String): User?
    fun createPlayer(name: String, password: String, state: PlayerState): User
    fun createToken(token: Token, maxTokens: Int)
    fun getTokenById(id: Int): Token?
    fun removeToken(token: String): Int
    fun getPlayerState(id: Int): PlayerState
    fun updatePlayerState(id: Int, state: PlayerState): User
    fun getGameID(playerID: Int): Int?
    fun getPlayerID(token: String): Int?
}