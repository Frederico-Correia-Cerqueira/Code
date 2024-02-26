package gomoku.repository.jdbi

import gomoku.domainEntities.*
import gomoku.http.SavedGames
import gomoku.http.models.SaveGameOutputModel
import gomoku.repository.GamesRepository
import org.jdbi.v3.core.Handle

class JdbiGamesRepository(private val handle: Handle) : GamesRepository {

    override fun getById(id: Int): Game? =
        handle.createQuery("select id, board, playerB, playerW, type, rules,information from game where id = :id")
            .bind("id", id)
            .mapTo(Game::class.java)
            .singleOrNull()


    override fun insertGame(playerB: Int, playerW: Int, board: Board, gameType: Variants, rules: OpeningRules): Int =
        handle.createUpdate(
            "insert into game(board, playerB, playerW, type, rules,information) values (:board, :playerB, :playerW, :type, :rules,:information )"
        )
            .bind("board", board.toString())
            .bind("playerB", playerB)
            .bind("playerW", playerW)
            .bind("type", gameType.toString())
            .bind("rules", rules.toString())
            .bind("information", false)
            .executeAndReturnGeneratedKeys("id")
            .mapTo(Int::class.java)
            .last()

    override fun update(id: Int, board: Board): Game {
        handle.createUpdate("update game set board=:board where id=:id")
            .bind("id", id)
            .bind("board", board.toString())
            .execute()

        return handle.createQuery("select id, board, playerB, playerW, type, rules,information from game where id = :id")
            .bind("id", id)
            .mapTo(Game::class.java)
            .first()
    }


    override fun updateInfo(id: Int, info: Boolean): Game {
        handle.createUpdate("update game set information=:information where id=:id")
            .bind("id", id)
            .bind("information", info)
            .execute()

        return handle.createQuery("select id, board, playerB, playerW, type, rules,information from game where id = :id")
            .bind("id", id)
            .mapTo(Game::class.java)
            .first()
    }


    override fun saveGame(id: Int, playerID: Int, gameName: String, gameDesc: String): Int {
        handle.createUpdate("insert into savegame(game,player,name,description) values(:game,:playerID,:game_name,:game_desc)")
            .bind("game", id)
            .bind("playerID", playerID)
            .bind("game_name", gameName)
            .bind("game_desc", gameDesc)
            .execute()
        return id
    }

    override fun deleteGame(id: Int, pid: Int): Int {
        handle.createUpdate("delete from savegame where id=:id AND player=:pid")
            .bind("id", id)
            .bind("pid", pid)
            .execute()
        return id
    }

    override fun getSaveGameById(id: Int, playerID: Int): SaveGameOutputModel? {
        return handle.createQuery("select * from savegame where id=:id AND player=:playerID")
            .bind("id", id)
            .bind("playerID", playerID)
            .mapTo(SaveGameOutputModel::class.java)
            .singleOrNull()
    }

    override fun getSaveGameByName(name: String, playerID: Int): List<SaveGameOutputModel> {
        return handle.createQuery("SELECT * FROM savegame WHERE name LIKE :name AND player = :playerID")
            .bind("name", "%$name%")
            .bind("playerID", playerID)
            .mapTo(SaveGameOutputModel::class.java)
            .toList()
    }

    override fun getPlayerSavedGames(playerID: Int): List<SaveGameOutputModel> =
        handle.createQuery("select id,game, name, description from savegame where player = :playerID")
            .bind("playerID", playerID)
            .mapTo(SaveGameOutputModel::class.java)
            .toList()
}

