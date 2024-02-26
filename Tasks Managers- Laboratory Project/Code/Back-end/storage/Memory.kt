package pt.isel.ls.storage

import pt.isel.ls.http.BList
import pt.isel.ls.http.Board
import pt.isel.ls.http.Card
import pt.isel.ls.http.User
import pt.isel.ls.http.boards
import pt.isel.ls.http.users
import java.util.UUID

class Memory : Storage {

    /** User Management **/
    override fun addUser(name: String, email: String, password: String): Pair<String, Int> {
        val id: Int = users.size
        val token = UUID.randomUUID().toString()
        val user = User(id, name, email, token, password)
        users += user
        return Pair(token, id)
    }

    override fun login(email: String, password: String): Pair<String, Int>? {
        val user = users.find { it.email == email && it.password == password }
        return if (user != null) Pair(user.token, user.id)
        else null
    }

    override fun getUser(userID: Int) = users.find { it.id == userID }

    /** Board Management **/
    override fun addBoard(name: String, description: String): Int {
        val id = boards.size
        val board = Board(id, name, description)
        boards += board
        return id
    }

    override fun addUserToBoard(user: User, board: Board): List<Int> {
        board.users.add(user)
        user.boards.add(board)
        return board.users.map { it.id }
    }

    override fun getUserBoards(user: User, limit: Int, skip: Int): List<Board> {
        val boards = user.boards.drop(skip)
        return if (boards.size > limit) boards.subList(0, limit) else boards
    }

    override fun getBoardUsers(board: Board, limit: Int, skip: Int): List<User> {
        val users = board.users.drop(skip)
        return if (users.size > limit) users.subList(0, limit) else users
    }

    override fun getBoard(boardID: Int): Board? {
        return boards.find { it.id == boardID }
    }

    override fun searchBoard(name: String?, description: String?, lsize: Int?): List<Board>? {
        return when {
            name != null -> {
                val board = boards.find { it.name == name }
                if (board != null) listOf(board)
                else emptyList()
            }
            description != null -> boards.filter { it.description == description }
            lsize != null -> boards.filter { it.lists.size == lsize }
            else -> null
        }
    }

    /** List Management **/
    override fun addList(board: Board, listName: String): Int {
        val newList = BList(board.lists.size, listName)
        board.lists.add(newList)
        return newList.id
    }

    override fun getBoardLists(board: Board, limit: Int, skip: Int): List<BList> {
        val lists = board.lists.drop(skip)
        return if (lists.size > limit) lists.subList(0, limit) else lists
    }

    override fun getList(board: Board, listID: Int) = board.lists.find { it.id == listID }
    override fun getListID(board: Board, name: String): Int? {
        val list = board.lists.find { it.name == name }
        return list?.id
    }
    override fun deleteList(board: Board, listID: Int): Int? {
        val listDelete = board.lists.find { it.id == listID } ?: return null
        board.lists.remove(listDelete)
        return listID
    }

    /** Card Management **/
    override fun addCard(list: BList, name: String, description: String, dueDate: String?, pos: Int): Int {
        val id = list.cards.size
        list.cards.add(Card(id, name, description, dueDate, pos))
        return id
    }

    override fun getListCards(boardList: BList, limit: Int, skip: Int): List<Card> {
        val cards = boardList.cards.drop(skip)
        return if (cards.size > limit) cards.subList(0, limit) else cards
    }

    override fun getCard(list: BList, card: Int) = list.cards.find { it.id == card }
    override fun getCardID(list: BList, cardName: String) = list.cards.find { it.name == cardName }?.id

    override fun moveCard(oldList: BList, card: Card, newList: BList, pos: Int) {
        oldList.cards.remove(card)
        oldList.cards.forEachIndexed { idx, card ->
            if (card.pos > pos) {
                oldList.cards[idx] = card.copy(pos = card.pos - 1)
            }
        }
        newList.cards.forEachIndexed { idx, card ->
            if (card.pos >= pos) {
                newList.cards[idx] = card.copy(pos = card.pos + 1)
            }
        }
        newList.cards.add(pos - 1, card.copy(pos = pos))
    }

    override fun deleteCard(list: BList, card: Int): Int? {
        val cardDelete = list.cards.find { it.id == card } ?: return null
        list.cards.remove(cardDelete)
        return card
    }

    /**
     *  Auxiliar Functions
     */
    override fun existingUser(email: String): Boolean = users.find { user -> user.email == email } != null
    override fun existingBoard(boardName: String): Boolean = boards.find { board -> board.name == boardName } != null
    override fun existingList(board: Board, listName: String): Boolean =
        board.lists.find { list -> list.name == listName } != null

    override fun existingUserOnBoard(userID: Int, boardID: Int): Boolean {
        val board: Board = boards.find { it.id == boardID } ?: return false
        board.users.find { user -> user.id == userID } ?: return false
        return true
    }
}
