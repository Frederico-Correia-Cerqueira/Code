package com.example.gomoku

import com.example.gomoku.domain.Information
import com.example.gomoku.domain.CreateUserDto
import com.example.gomoku.domain.LoginDto
import com.example.gomoku.domain.GameDto
import com.example.gomoku.domain.GameIDDto
import com.example.gomoku.domain.LogoutDto
import com.example.gomoku.domain.Matchmaking
import com.example.gomoku.domain.MatchmakingDto
import com.example.gomoku.domain.UserDto
import com.example.gomoku.domain.Ranking
import com.example.gomoku.domain.Stats
import com.example.gomoku.domain.SavedGames

interface PlayerService {
    suspend fun fetchGetUser(id: Int): UserDto
    suspend fun fetchPlayerStats(id: Int): Stats
    suspend fun fetchCreateUser(
        name: String,
        password: String
    ): CreateUserDto

    suspend fun fetchStats(): Ranking
}

interface LoginService {
    suspend fun fetchLogin(name: String, password: String): LoginDto
    suspend fun fetchLogout(token: String): LogoutDto
}

interface GameService {
    suspend fun fetchGetGame(id: Int): GameDto
    suspend fun fetchPlay(id: Int, playerID: Int, l: Int, c: Int): GameDto
    suspend fun fetchSwap(gameID: Int, playerID: Int, info: Boolean): GameDto
    suspend fun fetchSwapFirstMove(gameID: Int, playerID: Int, info: Boolean): GameDto
    suspend fun fetchSavedGames(id: Int): SavedGames
    suspend fun fetchSaveGame(
        id: Int,
        pid: Int,
        nameGame: String,
        descriptionGame: String
    ): GameIDDto

    suspend fun fetchGetSavedGame(id: Int, pid: Int): GameDto
}

interface InformationService {
    suspend fun fetchCredits(): Information
}

interface MatchmakingService {
    suspend fun fetchGameID(id: Int): GameIDDto
    suspend fun fetchMatchmaking(matchInfo: Matchmaking): MatchmakingDto
    suspend fun fetchPlayerState(id: Int): UserDto
}

class FetchException(message: String, val code: Int, cause: Throwable? = null) :
    Exception(message, cause)