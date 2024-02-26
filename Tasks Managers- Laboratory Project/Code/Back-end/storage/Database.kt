package pt.isel.ls.storage

import org.postgresql.ds.PGSimpleDataSource
import org.slf4j.LoggerFactory
import pt.isel.ls.http.BList
import pt.isel.ls.http.Board
import pt.isel.ls.http.Card
import pt.isel.ls.http.User
import java.sql.Date
import java.sql.SQLException
import java.sql.Statement
import java.util.UUID

class Database : Storage {
    private val logger = LoggerFactory.getLogger(Database::class.java)

    private val dataSource = PGSimpleDataSource().apply {
        val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL")
        setURL(jdbcDatabaseURL)
    }

    /**
     Users
     **/

    override fun addUser(name: String, email: String, password: String): Pair<String, Int> {
        var id = -1
        val token = UUID.randomUUID().toString()
        dataSource.connection.use {
            val stm = it.prepareStatement(
                "insert into USERS(user_name,user_token,email,password) values (?, ?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS
            )
            stm.setString(1, name)
            stm.setString(2, token)
            stm.setString(3, email)
            stm.setString(4, password)
            stm.executeUpdate()
            val generatedKeys = stm.generatedKeys
            if (generatedKeys.next()) { // check if key insertion was successful
                id = generatedKeys.getInt("id")
            }
        }
        return Pair(token, id)
    }

    override fun getUser(userID: Int): User? {
        dataSource.connection.use {
            val stmUser = it.prepareStatement("select * from USERS where id = ?")
            stmUser.setInt(1, userID)
            val rsUser = stmUser.executeQuery()

            try {
                if (rsUser.next()) return User(
                    rsUser.getInt("id"),
                    rsUser.getString("user_name"),
                    rsUser.getString("email"),
                    rsUser.getString("user_token"),
                    rsUser.getString("password")
                )
                else return null
            } catch (e: SQLException) {
                return null
            }
        }
    }

    override fun login(email: String, password: String): Pair<String, Int>? {
        dataSource.connection.use {
            val stmUser = it.prepareStatement("select * from USERS where email = ? and password = ?")
            stmUser.setString(1, email)
            stmUser.setString(2, password)
            val rsUser = stmUser.executeQuery()

            try {
                return if (rsUser.next()) Pair(rsUser.getString("user_token"), rsUser.getInt("id"))
                else null
            } catch (e: SQLException) {

                return null
            }
        }
    }

    /**
     Boards
     **/

    override fun addBoard(name: String, description: String): Int {
        var id = -1
        dataSource.connection.use {
            val stm = it.prepareStatement(
                "insert into BOARD(board_name,descriptions) values (?, ?);", Statement.RETURN_GENERATED_KEYS
            )
            stm.setString(1, name)
            stm.setString(2, description)
            stm.executeUpdate()
            val generatedKeys = stm.generatedKeys
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1)
            }
        }
        return id
    }

    override fun addUserToBoard(user: User, board: Board): List<Int> {
        val userIds = mutableListOf<Int>()
        dataSource.connection.use {
            val stm = it.prepareStatement("insert into USER_BOARD(board_id,user_id) values (?, ?);")
            stm.setInt(1, board.id)
            stm.setInt(2, user.id)
            stm.executeUpdate()
        }
        dataSource.connection.use {
            val stm = it.prepareStatement("select user_id from USER_BOARD where board_id = ?")
            stm.setInt(1, board.id)
            val rs = stm.executeQuery()
            while (rs.next()) {
                userIds.add(rs.getInt("user_id"))
            }
        }
        return userIds
    }

    override fun getBoardUsers(board: Board, limit: Int, skip: Int): List<User> {
        val usersIds = mutableListOf<Int>()
        val detailsUser = mutableListOf<User>()
        dataSource.connection.use {
            val stm = it.prepareStatement("select user_id from USER_BOARD where board_id = ? OFFSET ? LIMIT ? ")
            stm.setInt(1, board.id)
            stm.setInt(2, skip)
            stm.setInt(3, limit)
            val rs = stm.executeQuery()
            while (rs.next()) {
                usersIds.add(rs.getInt("user_id"))
            }
        }
        for (i in usersIds) {
            val user = getUser(i)
            if (user != null) detailsUser.add(user)
        }
        return detailsUser
    }

    override fun getUserBoards(user: User, limit: Int, skip: Int): List<Board> {
        val boardIds = mutableListOf<Int>()
        val detailsBoard = mutableListOf<Board>()
        dataSource.connection.use {
            val stm = it.prepareStatement("select board_id from USER_BOARD where user_id = ? OFFSET ? LIMIT ?")
            stm.setInt(1, user.id)
            stm.setInt(2, skip)
            stm.setInt(3, limit)
            val rs = stm.executeQuery()
            while (rs.next()) {
                boardIds.add(rs.getInt("board_id"))
            }
        }
        for (i in boardIds) {
            val board = getBoard(i)
            if (board != null) detailsBoard.add(board)
        }
        return detailsBoard
    }

    override fun getBoard(boardID: Int): Board? {
        try {
            dataSource.connection.use { connection ->
                val stm = connection.prepareStatement("select * from BOARD where id = ?")
                stm.setInt(1, boardID)
                val rs = stm.executeQuery()
                if (rs.next()) {
                    val id = rs.getInt("id")
                    val boardName = rs.getString("board_name")
                    val descriptions = rs.getString("descriptions")
                    return Board(id, boardName, descriptions)
                } else {
                    return null
                }
            }
        } catch (ex: SQLException) {
            return null
        }
    }

    override fun searchBoard(name: String?, description: String?, lsize: Int?): List<Board>? {
        val boardsID = mutableListOf<Int>()
        val boardLists = mutableListOf<Board>()

        try {
            dataSource.connection.use { connection ->
                if (name == null && description == null && lsize == null) return null
                val query = StringBuilder("SELECT b.* FROM BOARD b")
                if (name != null) {
                    query.append(" WHERE b.board_name = ?")
                } else if (description != null) {
                    query.append(" WHERE b.descriptions = ?")
                } else if (lsize != null) {
                    if (lsize == 0) {
                        query.append(" LEFT JOIN LIST l ON b.id = l.board WHERE l.board IS NULL ")
                    } else {
                        query.append(
                            " INNER JOIN (SELECT l.board, COUNT(*) AS num_lists FROM LIST l " + "GROUP BY l.board HAVING COUNT(*)"
                        )

                        if (lsize < 4) query.append("= ?")
                        else query.append(">= ?")

                        query.append(" ) subquery ON b.id = subquery.board")
                    }
                }

                val stm = connection.prepareStatement(query.toString())

                when {
                    name != null -> stm.setString(1, name)
                    description != null -> stm.setString(1, description)
                    lsize == 0 -> stm
                    lsize != null -> stm.setInt(1, lsize)
                }
                val rs = stm.executeQuery()

                while (rs.next()) {

                    boardsID.add(rs.getInt("id"))
                }
                for (id in boardsID) {
                    val board = getBoard(id)
                    if (board != null) boardLists.add(board)
                }
                if (boardLists.isEmpty()) return null
                return boardLists
            }
        } catch (ex: SQLException) {
            return null
        }
    }

    /**
     Lists
     **/
    override fun addList(board: Board, listName: String): Int {
        var id = -1
        dataSource.connection.use {
            val stm = it.prepareStatement(
                "insert into LIST(list_name,board) values (?, ?);", Statement.RETURN_GENERATED_KEYS
            )
            stm.setString(1, listName)
            stm.setInt(2, board.id)
            stm.executeUpdate()
            val generatedKeys = stm.generatedKeys
            if (generatedKeys.next()) {
                id = generatedKeys.getInt("id")
            }
        }
        return id
    }

    override fun getBoardLists(board: Board, limit: Int, skip: Int): List<BList> {
        val listID = mutableListOf<Int>()
        val boardLists = mutableListOf<BList>()
        dataSource.connection.use {
            val stm = it.prepareStatement("select * from LIST where board = ? OFFSET ? LIMIT ?")
            stm.setInt(1, board.id)
            stm.setInt(2, skip)
            stm.setInt(3, limit)
            val rs = stm.executeQuery()
            while (rs.next()) {
                listID.add(rs.getInt("id"))
            }
        }
        for (id in listID) {
            val list = getList(board, id)
            if (list != null) boardLists.add(list)
        }
        return boardLists
    }

    override fun getListID(board: Board, name: String): Int? {
        try {
            dataSource.connection.use {
                val stm = it.prepareStatement("select * from LIST where board = ? and list_name = ?;")
                stm.setInt(1, board.id)
                stm.setString(2, name)
                val rs = stm.executeQuery()
                if (rs.next()) {
                    return rs.getInt("id")
                } else {
                    return null
                }
            }
        } catch (ex: SQLException) {
            return null
        }
    }

    override fun getList(board: Board, listID: Int): BList? {
        try {
            dataSource.connection.use {
                val stm = it.prepareStatement("select * from LIST where board = ? and id = ?;")
                stm.setInt(1, board.id)
                stm.setInt(2, listID)
                val rs = stm.executeQuery()
                if (rs.next()) {
                    return BList(
                        rs.getInt("id"), rs.getString("list_name")
                    )
                } else {
                    return null
                }
            }
        } catch (ex: SQLException) {
            return null
        }
    }

    override fun deleteList(board: Board, listID: Int): Int? {
        try {
            dataSource.connection.use {
                val stmCards =
                    it.prepareStatement("DELETE FROM CARD WHERE list IN (SELECT id FROM LIST WHERE board = ? AND id = ?);")
                stmCards.setInt(1, board.id)
                stmCards.setInt(2, listID)
                stmCards.executeUpdate()

                val stmL = it.prepareStatement("DELETE FROM LIST WHERE board = ? AND id = ?;")
                stmL.setInt(1, board.id)
                stmL.setInt(2, listID)
                stmL.executeUpdate()
            }
        } catch (ex: SQLException) {
            return null
        }
        return listID
    }

    /**
     Cards
     **/
    override fun addCard(list: BList, name: String, description: String, dueDate: String?, pos: Int): Int {
        var cardId = -1
        dataSource.connection.use {
            val stm = it.prepareStatement(
                "insert into CARD(descriptions,card_name,dueDate,positions,list) values (?, ?, ?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS
            )
            stm.setString(1, description)
            stm.setString(2, name)
            val data = if (dueDate != "") {
                Date.valueOf(dueDate)
            } else {
                null
            }
            stm.setDate(3, data)
            stm.setInt(4, pos)
            stm.setInt(5, list.id)
            stm.executeUpdate()
            val generatedKeys = stm.generatedKeys

            if (generatedKeys.next()) {
                cardId = generatedKeys.getInt(1)
            }
        }
        return cardId
    }

    override fun getListCards(boardList: BList, limit: Int, skip: Int): List<Card> {
        val cardIds = mutableListOf<Int>()
        val cards = mutableListOf<Card>()
        dataSource.connection.use {
            val stm = it.prepareStatement("select id from CARD where list = ? OFFSET ? LIMIT ?")
            stm.setInt(1, boardList.id)
            stm.setInt(2, skip)
            stm.setInt(3, limit)

            val rs = stm.executeQuery()
            while (rs.next()) {
                cardIds.add(rs.getInt("id"))
            }
        }
        for (id in cardIds) {
            val card = getCard(boardList, id)
            if (card != null) {
                cards.add(card)
            }
        }
        return cards
    }

    override fun getCard(list: BList, card: Int): Card? {
        dataSource.connection.use {
            val stm = it.prepareStatement("select * from CARD where id = ? and list = ?")
            stm.setInt(1, card)
            stm.setInt(2, list.id)
            val rs = stm.executeQuery()
            try {
                if (rs.next()) {
                    return Card(
                        rs.getInt("id"),
                        rs.getString("card_name"),
                        rs.getString("descriptions"),
                        rs.getString("dueDate"),
                        rs.getInt("positions")
                    )
                } else {
                    return null
                }
            } catch (ex: SQLException) {
                return null
            }
        }
    }

    override fun getCardID(list: BList, cardName: String): Int? {
        try {
            dataSource.connection.use {
                val stm = it.prepareStatement("select * from CARD where list = ? and card_name = ?;")
                stm.setInt(1, list.id)
                stm.setString(2, cardName)
                val rs = stm.executeQuery()
                if (rs.next()) {
                    return rs.getInt("id")
                } else {
                    return null
                }
            }
        } catch (ex: SQLException) {
            return null
        }
    }

    override fun moveCard(oldList: BList, card: Card, newList: BList, pos: Int) {
        dataSource.connection.use { connection ->
            connection.prepareStatement("UPDATE CARD SET list = ?, positions = ? WHERE id = ? AND list = ?;")
                .use { stm ->
                    stm.setInt(1, newList.id)
                    stm.setInt(2, pos)
                    stm.setInt(3, card.id)
                    stm.setInt(4, oldList.id)
                    stm.executeUpdate()
                }
            connection.prepareStatement("UPDATE CARD SET positions = positions + 1 WHERE list = ? AND positions >= ? AND id <> ?;")
                .use { stm ->
                    // Atualiza as posições das cards na lista original (list)
                    stm.setInt(1, newList.id)
                    stm.setInt(2, pos)
                    stm.setInt(3, card.id)
                    stm.executeUpdate()
                }
            connection.prepareStatement("UPDATE CARD SET positions = positions - 1 WHERE list = ? AND positions >= ? AND id <> ?;")
                .use { stm ->
                    // Atualiza as posições das cards na nova lista (newList)
                    stm.setInt(1, oldList.id)
                    stm.setInt(2, card.pos)
                    stm.setInt(3, card.id)
                    stm.executeUpdate()
                }
        }
    }

    override fun deleteCard(list: BList, card: Int): Int? {
        try {
            dataSource.connection.use {
                val stm = it.prepareStatement("DELETE FROM CARD WHERE list = ? AND id = ?;")
                stm.setInt(1, list.id)
                stm.setInt(2, card)
                stm.executeUpdate()
            }
        } catch (ex: SQLException) {
            return null
        }
        return card
    }

    /**
     *  Auxiliary Functions
     */
    override fun existingUser(email: String): Boolean {
        dataSource.connection.use {
            val stmUser = it.prepareStatement("select * from USERS where email = ?")
            stmUser.setString(1, email)
            val rsUser = stmUser.executeQuery()

            try {
                return rsUser.next()
            } catch (e: SQLException) {
                return false
            }
        }
    }

    override fun existingBoard(boardName: String): Boolean {
        dataSource.connection.use {
            val stmUser = it.prepareStatement("select * from BOARD where board_name = ?")
            stmUser.setString(1, boardName)
            val rsUser = stmUser.executeQuery()

            try {
                return rsUser.next()
            } catch (e: SQLException) {
                return false
            }
        }
    }

    override fun existingList(board: Board, listName: String): Boolean {
        dataSource.connection.use {
            val stmUList = it.prepareStatement("select * from LIST where list_name = ? and board = ?")
            stmUList.setString(1, listName)
            stmUList.setInt(2, board.id)
            val rsList = stmUList.executeQuery()

            try {
                return rsList.next()
            } catch (e: SQLException) {
                return false
            }
        }
    }

    override fun existingUserOnBoard(userID: Int, boardID: Int): Boolean {
        dataSource.connection.use {
            val stmUList = it.prepareStatement("select * from USER_BOARD where board_id = ? and user_id = ?")
            stmUList.setInt(1, boardID)
            stmUList.setInt(2, userID)
            val rsList = stmUList.executeQuery()

            try {
                return rsList.next()
            } catch (e: SQLException) {
                return false
            }
        }
    }
}
