package gomoku.repository

import gomoku.domainEntities.*
import gomoku.http.models.SaveGameOutputModel

interface GamesRepository {
    fun getById(id: Int): Game?
    fun update(id: Int, board: Board): Game
    fun updateInfo(id: Int, info: Boolean): Game
    fun saveGame(id: Int, playerID: Int, game_name: String, game_desc: String): Int
    fun deleteGame(id: Int, pid: Int): Int
    fun getSaveGameById(id: Int, playerID: Int): SaveGameOutputModel?
    fun getSaveGameByName(name: String, playerID: Int): List<SaveGameOutputModel>
    fun getPlayerSavedGames(playerID: Int): List<SaveGameOutputModel>
    fun insertGame(playerB: Int, playerW: Int, board: Board, gameType: Variants, rules: OpeningRules): Int
}