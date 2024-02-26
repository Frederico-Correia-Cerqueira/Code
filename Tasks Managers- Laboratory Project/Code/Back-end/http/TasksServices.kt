package pt.isel.ls.http

import isEmailValid
import kotlinx.serialization.Serializable

/**
 * implementation of the logic of each of the application's functionalities
 */

@Serializable
data class UserData(val id: Int, val name: String, val email: String, val boards: List<Board>)

@Serializable
data class BoardData(
    val id: Int,
    val name: String,
    val description: String,
    val users: List<String>,
    val lists: List<BListAndCard>
)

@Serializable
data class ListData(val id: Int, val name: String, val cards: List<String>)

@Serializable
data class Credentials(val token: String, val id: Int)

@Serializable
data class BListAndCard(val list: BList, val cards: List<Card>)

/** User Management **/
fun addUserServices(userInfo: UserInfo): Credentials {
    if (!userInfo.email.isEmailValid()) error("EMAIL_NOT_VALID")
    if (storage.existingUser(userInfo.email)) error("EXISTING_USER")
    val user = storage.addUser(userInfo.name, userInfo.email, userInfo.password)
    return Credentials(user.first, user.second)
}
fun login(email: String, password: String): Credentials {
    val user = storage.login(email, password) ?: error("ID_NOT_FOUND")
    return Credentials(user.first, user.second)
}

fun getUserServices(userID: Int) = storage.getUser(userID) ?: error("USER_NOT_FOUND")

// This functions allows us to return a user without his token and with his corresponding boards
fun getUserDetails(userID: Int): UserData {
    val user = getUserServices(userID)
    val boards = getUserBoardsServices(userID, null, null)
    return UserData(user.id, user.name, user.email, boards)
}

/** Board Management **/
fun addBoardServices(boardInfo: BoardInfo) =
    if (storage.existingBoard(boardInfo.name)) error("EXISTING_BOARD")
    else storage.addBoard(boardInfo.name, boardInfo.description)

fun addUserToBoardServices(userID: Int, boardID: Int) =
    if (storage.existingUserOnBoard(userID, boardID)) error("EXISTING_USER_AT_BOARD")
    else storage.addUserToBoard(getUserServices(userID), getBoardServices(boardID))

fun getUserBoardsServices(userID: Int, l: Int? = 100, s: Int? = 0): List<Board> {
    val limit = l ?: 100
    val skip = s ?: 0
    return storage.getUserBoards(getUserServices(userID), limit, skip)
}

fun getBoardUsersServices(boardID: Int, l: Int? = 100, s: Int? = 0): List<User> {
    val limit = l ?: 100
    val skip = s ?: 0
    return storage.getBoardUsers(getBoardServices(boardID), limit, skip)
}

fun getBoardServices(boardID: Int) = storage.getBoard(boardID) ?: error("BOARD_NOT_FOUND")

fun searchBoard(name: String?, description: String?, lsize: Int?): List<Board> {
    return storage.searchBoard(name, description, lsize) ?: error("BOARDS_NOT_FOUND")
}

// This functions allows us to return a board with his corresponding users and lists
fun getBoardDetails(boardID: Int): BoardData {
    val board = getBoardServices(boardID)
    val boardUsers = getBoardUsersServices(boardID, null, null)
    val boardLists = getBoardListsServices(boardID, null, null)
    return BoardData(
        board.id, board.name, board.description,
        boardUsers.map { u ->
            "{ID: ${u.id}, Name: ${u.name}, Email: ${u.email}}"
        },
        boardLists
    )
}

/** List Management **/
fun addListServices(boardID: Int, listInfo: ListInfo): Int {
    val board = getBoardServices(boardID)
    if (storage.existingList(board, listInfo.name)) error("EXISTING_LIST")
    return storage.addList(board, listInfo.name)
}

fun getBoardListsServices(boardID: Int, l: Int? = 100, s: Int? = 0): List<BListAndCard> {
    val ListsAndCards = mutableListOf<BListAndCard>()
    val limit = l ?: 100
    val skip = s ?: 0
    val lists = storage.getBoardLists(getBoardServices(boardID), limit, skip)
    lists.forEach {
        ListsAndCards.add(BListAndCard(it, storage.getListCards(it, 100)))
    }
    return ListsAndCards
}

fun getListServices(boardID: Int, listID: Int) =
    storage.getList(getBoardServices(boardID), listID) ?: error("LIST_NOT_FOUND")

fun getListIDServices(boardID: Int, name: String) =
    storage.getListID(getBoardServices(boardID), name) ?: error("ID_NOT_FOUND")

// This functions allows us to return a list with his corresponding cards
fun getListDetails(boardID: Int, listID: Int): ListData {
    val list = getListServices(boardID, listID)
    val cards = getListCardsServices(boardID, listID)
    return ListData(listID, list.name, cards.map { c -> "{ID: ${c.id}, Name: ${c.name}, Description: ${c.description}, dueDate: ${c.dueDate}, Position: ${c.pos}}" })
}

fun deleteListServices(boardID: Int, listID: Int): Int? {
    val board = getBoardServices(boardID)
    // Verificar que a lista existe
    getListDetails(boardID, listID)
    return storage.deleteList(board, listID)
}

/** Card Management **/
fun addCardServices(boardID: Int, listID: Int, cardInfo: CardInfo): Int {

    return storage.addCard(
        getListServices(boardID, listID),
        cardInfo.name,
        cardInfo.description,
        cardInfo.dueDate,
        getListCardsServices(boardID, listID).size
    )
}

fun getListCardsServices(boardID: Int, listID: Int, l: Int? = 100, s: Int? = 0): List<Card> {
    val limit = l ?: 100
    val skip = s ?: 0
    return storage.getListCards(getListServices(boardID, listID), limit, skip)
}

fun getCardServices(boardID: Int, listID: Int, cardID: Int) =
    storage.getCard(getListServices(boardID, listID), cardID) ?: error("CARD_NOT_FOUND")

fun getCardIDServices(boardID: Int, listID: Int, cardName: String): Int {
    return storage.getCardID(getListServices(boardID, listID), cardName) ?: error("ID_NOT_FOUND")
}
fun deleteCardServices(boardID: Int, listID: Int, cardID: Int): Int? {
    getCardServices(boardID, listID, cardID)
    val list = getListServices(boardID, listID)
    return storage.deleteCard(list, cardID)
}

fun moveCardServices(boardID: Int, oldList: Int, newList: Int, cardID: Int, pos: Int) =
    storage.moveCard(
        getListServices(boardID, oldList),
        getCardServices(boardID, oldList, cardID),
        getListServices(boardID, newList),
        pos
    )
