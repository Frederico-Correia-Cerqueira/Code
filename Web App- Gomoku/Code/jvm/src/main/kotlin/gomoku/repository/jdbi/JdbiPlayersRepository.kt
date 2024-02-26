package gomoku.repository.jdbi


import gomoku.domainEntities.*
import gomoku.repository.PlayersRepository
import org.jdbi.v3.core.Handle

class JdbiPlayersRepository(private val handle: Handle) : PlayersRepository {
    override fun getById(id: Int): User? =
        handle.createQuery("select id, username, password, state from player where id = :id")
            .bind("id", id)
            .mapTo(User::class.java)
            .singleOrNull()

    override fun getByName(username: String): User? =
        handle.createQuery("select id, username, password, state from player where username = :username")
            .bind("username", username)
            .mapTo(User::class.java)
            .singleOrNull()

    override fun getByCredentials(username: String, password: String): User? =
        handle.createQuery("select id, username, password, state from player where username = :username AND password = :password")
            .bind("username", username)
            .bind("password", password)
            .mapTo(User::class.java)
            .singleOrNull()

    override fun createPlayer(name: String, password: String, state: PlayerState): User {
        handle.createUpdate(
            "insert into player(username, password, state) values (:username, :password, :state)"
        )
            .bind("username", name)
            .bind("password", password)
            .bind("state", state.toString())
            .execute()

        return handle.createQuery("select id, username, password, state from player where username = :username")
            .bind("username", name)
            .mapTo(User::class.java)
            .first()
    }

    override fun createToken(token: Token, maxTokens: Int) {
        handle.createUpdate(
            """
            delete from token
            where userID = :userID 
                and token in (
                    select token from token where userID = :userID 
                        offset :offset
                )
            """.trimIndent()
        )
            .bind("userID", token.userID)
            .bind("offset", maxTokens - 1)
            .execute()

        handle.createUpdate(
            """
                insert into token(userID, token) 
                values (:userID, :token)
            """.trimIndent()
        )
            .bind("userID", token.userID)
            .bind("token", token.token)
            .execute()
    }

    override fun removeToken(token: String): Int =
        handle.createUpdate(
            "delete from token where token = :token"
        )
            .bind("token", token)
            .execute()

    override fun getTokenById(id: Int): Token? =
        handle.createQuery("select token, userID from token where userID = :userID")
            .bind("userID", id)
            .mapTo(Token::class.java)
            .firstOrNull()

    override fun getPlayerState(id: Int): PlayerState =
        handle.createQuery("select state from player where id = :id")
            .bind("id", id)
            .mapTo(PlayerState::class.java)
            .first()

    override fun updatePlayerState(id: Int, state: PlayerState): User {
        handle.createUpdate("update player set state=:state where id=:id")
            .bind("id", id)
            .bind("state", state)
            .execute()

        return handle.createQuery("select id, username, password, state from player where id = :id")
            .bind("id", id)
            .mapTo(User::class.java)
            .first()
    }

    override fun getGameID(playerID: Int): Int?{
        return handle.createQuery("select id from game where board LIKE :board AND (playerw = :playerw OR playerb = :playerw)")
                .bind("board", "%BOARD_RUN%")
                .bind("playerw", playerID)
                .mapTo(Int::class.java)
                .singleOrNull()
    }

    override fun  getPlayerID(token: String): Int?{
        return handle.createQuery("select userid from token where token = :token")
                .bind("token", token)
                .mapTo(Int::class.java)
                .singleOrNull()
    }
}