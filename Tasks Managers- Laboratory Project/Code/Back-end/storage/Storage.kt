package pt.isel.ls.storage

import pt.isel.ls.http.BList
import pt.isel.ls.http.Board
import pt.isel.ls.http.Card
import pt.isel.ls.http.User

interface Storage {
    /*
            Users
     */
    fun getUser(userID: Int): User?
    fun login(email: String, password: String): Pair<String, Int>?
    fun addUser(name: String, email: String, password: String): Pair<String, Int>
    fun getBoardUsers(board: Board, limit: Int, skip: Int): List<User>

    /*
            Boards
     */
    fun getBoard(boardID: Int): Board?
    fun searchBoard(name: String? = null, description: String? = null, lsize: Int? = null): List<Board>?
    fun addBoard(name: String, description: String): Int
    fun getUserBoards(user: User, limit: Int, skip: Int = 0): List<Board>
    fun addUserToBoard(user: User, board: Board): List<Int>

    /*
            Lists
     */
    fun getList(board: Board, listID: Int): BList?
    fun getListID(board: Board, name: String): Int?
    fun addList(board: Board, listName: String): Int
    fun getBoardLists(board: Board, limit: Int, skip: Int = 0): List<BList>
    fun deleteList(board: Board, listID: Int): Int?

    /*
            Cards
     */
    fun getCard(list: BList, card: Int): Card?
    fun getCardID(list: BList, cardName: String): Int?
    fun addCard(list: BList, name: String, description: String, dueDate: String?, pos: Int): Int
    fun getListCards(boardList: BList, limit: Int, skip: Int = 0): List<Card>
    fun moveCard(oldList: BList, card: Card, newList: BList, pos: Int)
    fun deleteCard(list: BList, card: Int): Int?

    /**
     *  Auxiliary Functions
     */
    fun existingUser(email: String): Boolean
    fun existingBoard(boardName: String): Boolean
    fun existingList(board: Board, listName: String): Boolean
    fun existingUserOnBoard(userID: Int, boardID: Int): Boolean
}
