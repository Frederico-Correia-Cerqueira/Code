package pt.isel.ls.http

import isValidDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

/**
 * implementation of the HTTP routes that make up the REST API of the web application
 */
@Serializable
data class UserInfo(val name: String, val email: String, val password: String)

@Serializable
data class BoardInfo(val name: String, val description: String)

@Serializable
data class ListInfo(val name: String)

@Serializable
data class CardInfo(val name: String, val description: String, val dueDate: String?)

@Serializable
data class MoveCardInfo(val listID: String, val pos: String)

@Serializable
data class GetUserBoards(val boards: List<Board>)

@Serializable
class GetBoardUsers(val users: List<String>)

@Serializable
data class searchBoard(val boards: List<Board>)

fun httpRoutes(): Pair<RoutingHttpHandler, RoutingHttpHandler> {
    val userManagement = routes(
        "users" bind POST to { req: Request -> addUser(req) },
        "users/getID" bind GET to { req: Request -> login(req) },
        "users/{uid}" bind GET to { req: Request -> getUser(req) }
    )
    val boardManagement = routes(
        "boards" bind POST to { req: Request -> addBoard(req) },
        "boards/{bid}/addUser/{uid}" bind PUT to { req: Request -> addUserToBoard(req) },
        "users/{uid}/boards" bind GET to { req: Request -> getUserBoards(req) },
        "boards/{bid}" bind GET to { req: Request -> getBoard(req) },
        "boards/{bid}/users" bind GET to { req: Request -> getBoardUsers(req) },
        "searchboards" bind GET to { req: Request -> searchBoard(req) },

        "boards/{bid}/lists" bind POST to { req: Request -> addList(req) },
        "boards/{bid}/getLists" bind GET to { req: Request -> getBoardLists(req) },
        "boards/{bid}/lists/getID" bind GET to { req: Request -> getListID(req) },
        "boards/{bid}/lists/{lid}" bind GET to { req: Request -> getList(req) },
        "boards/{bid}/deleteList/{lid}" bind DELETE to { req: Request -> deleteList(req) },

        "boards/{bid}/lists/{lid}/addCard" bind POST to { req: Request -> addCard(req) },
        "boards/{bid}/lists/{lid}/getCards" bind GET to { req: Request -> getListCards(req) },
        "boards/{bid}/lists/{lid}/cards/getID" bind GET to { req: Request -> getCardID(req) },
        "boards/{bid}/lists/{lid}/cards/{cid}" bind GET to { req: Request -> getCard(req) },
        "boards/{bid}/lists/{lid}/deleteCard/{cid}" bind DELETE to { req: Request -> deleteCard(req) },
        "boards/{bid}/lists/{lid}/cards/{cid}" bind PUT to { req: Request -> moveCard(req) }
    )
    return Pair(userManagement, boardManagement)
}

/** User Management **/
fun addUser(request: Request): Response {
    return try {
        val userInfo = Json.decodeFromString<UserInfo>(request.bodyString())
        if (userInfo.name.isEmpty() || userInfo.email.isEmpty() || userInfo.password.isEmpty()) error("INVALID_PARAMETER")
        val newUser = addUserServices(userInfo)
        Response(Status.CREATED)
            .header("content-type", "application/json")
            .body(Json.encodeToString(newUser))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}

fun login(request: Request): Response {
    return try {
        val email = request.query("emailLogin") ?: error("EMAIL_NOT_FOUND")
        val password = request.query("password") ?: error("PASSWORD_NOT_FOUND")
        val credentials = login(email, password)
        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(credentials))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}

fun getUser(request: Request): Response {
    return try {
        val userID = request.path("uid")?.toIntOrNull() ?: error("ID_NOT_VALID")
        val user = getUserDetails(userID)
        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(user))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}

/** Board Management **/
fun addBoard(request: Request): Response {
    return try {
        val boardInfo = Json.decodeFromString<BoardInfo>(request.bodyString())
        if (boardInfo.name.isEmpty() || boardInfo.description.isEmpty()) error("INVALID_PARAMETER")
        val newBoard = addBoardServices(boardInfo)
        Response(Status.CREATED)
            .header("content-type", "application/json")
            .body(Json.encodeToString(newBoard))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}

fun addUserToBoard(request: Request): Response {
    return try {
        val userID = request.path("uid")?.toIntOrNull() ?: error("ID_NOT_VALID")
        val boardID = request.path("bid")?.toIntOrNull() ?: error("ID_NOT_VALID")
        val body = addUserToBoardServices(userID, boardID)
        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(body))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}

fun getUserBoards(request: Request): Response {
    return try {
        val userID = request.path("uid")?.toIntOrNull() ?: error("ID_NOT_VALID")
        val limit = request.query("limit")?.toIntOrNull()
        val skip = request.query("skip")?.toIntOrNull()
        val boards = getUserBoardsServices(userID, limit, skip)
        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(GetUserBoards(boards)))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}

fun getBoard(request: Request): Response {
    return try {
        val boardID = request.path("bid")?.toIntOrNull() ?: error("ID_NOT_VALID")
        val board = getBoardDetails(boardID)
        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(board))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}

fun searchBoard(request: Request): Response {
    return try {
        val name = request.query("name")
        val description = request.query("description")
        val lsize = request.query("lsize")?.toIntOrNull()
        val boards = searchBoard(name, description, lsize)
        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(searchBoard(boards)))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}

fun getBoardUsers(request: Request): Response {
    return try {
        val boardID = request.path("bid")?.toIntOrNull() ?: error("ID_NOT_VALID")
        val limit = request.query("limit")?.toIntOrNull()
        val skip = request.query("skip")?.toIntOrNull()
        val users = getBoardUsersServices(boardID, limit, skip)
        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(users))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}

/** List Management **/
fun addList(request: Request): Response {
    return try {
        val listInfo = Json.decodeFromString<ListInfo>(request.bodyString())
        if (listInfo.name.isEmpty()) error("INVALID_PARAMETER")
        val boardID = request.path("bid")?.toIntOrNull() ?: error("ID_NOT_VALID")
        val newList = addListServices(boardID, listInfo)
        Response(Status.CREATED)
            .header("content-type", "application/json")
            .body(Json.encodeToString(newList))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}

fun getBoardLists(request: Request): Response {
    return try {
        val limit = request.query("limit")?.toIntOrNull()
        val skip = request.query("skip")?.toIntOrNull()
        val boardID = request.path("bid")?.toIntOrNull() ?: error("ID_NOT_VALID")
        val lists = getBoardListsServices(boardID, limit, skip)

        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(lists))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}

fun getList(request: Request): Response {
    return try {
        val boardID = request.path("bid")?.toIntOrNull() ?: error("ID_NOT_VALID")
        val listID = request.path("lid")?.toIntOrNull() ?: error("LIST_NOT_VALID")
        val list = getListDetails(boardID, listID)
        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(list))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}

fun getListID(request: Request): Response {
    return try {
        val boardID = request.path("bid")?.toIntOrNull() ?: error("ID_NOT_VALID")
        val listID = request.query("name") ?: error("LIST_NOT_VALID")
        val list = getListIDServices(boardID, listID)
        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(list))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}

fun deleteList(request: Request): Response {
    return try {
        val boardID = request.path("bid")?.toIntOrNull() ?: error("ID_NOT_VALID")
        val listID = request.path("lid")?.toIntOrNull() ?: error("LIST_NOT_VALID")
        val listDeleted = deleteListServices(boardID, listID)
        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(listDeleted))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}

/** Cards Management **/
fun addCard(request: Request): Response {
    return try {
        val cardInfo = Json.decodeFromString<CardInfo>(request.bodyString())
        if (cardInfo.name.isEmpty() || cardInfo.description.isEmpty()) error("INVALID_PARAMETER")
        if (cardInfo.dueDate != "") {
            // verificar se a data está no tipo certo
            if (cardInfo.dueDate?.isValidDate() == false) error("INVALID_PARAMETER")
        }
        val listID = request.path("lid")?.toIntOrNull() ?: error("LIST_NOT_VALID")
        val boardID = request.path("bid")?.toIntOrNull() ?: error("ID_NOT_VALID")
        val card = addCardServices(boardID, listID, cardInfo)
        Response(Status.CREATED)
            .header("content-type", "application/json")
            .body(Json.encodeToString(card))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}

fun getListCards(request: Request): Response {
    return try {
        val limit = request.query("limit")?.toIntOrNull()
        val skip = request.query("skip")?.toIntOrNull()
        val boardID = request.path("bid")?.toIntOrNull() ?: error("ID_NOT_VALID")
        val listID = request.path("lid")?.toIntOrNull() ?: error("LIST_NOT_VALID")
        val cards = getListCardsServices(boardID, listID, limit, skip)
        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(cards))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}

fun getCard(request: Request): Response {
    return try {
        val boardID = request.path("bid")?.toIntOrNull() ?: error("ID_NOT_VALID")
        val listID = request.path("lid")?.toIntOrNull() ?: error("LIST_NOT_VALID")
        val cardID = request.path("cid")?.toIntOrNull() ?: error("CARD_NOT_VALID")
        val card = getCardServices(boardID, listID, cardID)
        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(card))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}

fun getCardID(request: Request): Response {
    return try {
        val boardID = request.path("bid")?.toIntOrNull() ?: error("ID_NOT_VALID")
        val listID = request.path("lid")?.toIntOrNull() ?: error("ID_NOT_VALID")
        val cardName = request.query("name") ?: error("CARD_NOT_VALID")
        val cardID = getCardIDServices(boardID, listID, cardName)
        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(cardID))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}

fun deleteCard(request: Request): Response {
    return try {
        val boardID = request.path("bid")?.toIntOrNull() ?: error("ID_NOT_VALID")
        val listID = request.path("lid")?.toIntOrNull() ?: error("LIST_NOT_VALID")
        val cardID = request.path("cid")?.toIntOrNull() ?: error("CARD_NOT_VALID")
        val cardDeleted = deleteCardServices(boardID, listID, cardID)
        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(cardDeleted))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}

fun moveCard(request: Request): Response {
    return try {

        val moveInfo = Json.decodeFromString<MoveCardInfo>(request.bodyString())
        val lid = moveInfo.listID.toIntOrNull() ?: error("INVALID_PARAMETER")
        val pos = moveInfo.pos.toIntOrNull() ?: error("INVALID_PARAMETER")

        val boardID = request.path("bid")?.toIntOrNull() ?: error("ID_NOT_VALID")
        val oldList = request.path("lid")?.toIntOrNull() ?: error("LIST_NOT_VALID")

        val cardID = request.path("cid")?.toIntOrNull() ?: error("CARD_NOT_VALID")
        val moveCard = moveCardServices(boardID, oldList, lid, cardID, pos)
        Response(Status.OK)
            .header("content-type", "application/json")
            .body(Json.encodeToString(moveCard))
    } catch (e: IllegalStateException) {
        Response(httpResponseErrors(e))
            .header("content-type", "application/json")
            .body(Json.encodeToString(httpResponseErrors(e).toString()))
    }
}
