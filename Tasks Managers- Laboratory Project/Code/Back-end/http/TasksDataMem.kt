package pt.isel.ls.http

import kotlinx.serialization.Serializable

@Serializable
class User(val id: Int, val name: String, val email: String, val token: String, val password: String) {
    val boards = mutableListOf<Board>()
}

@Serializable
class Board(val id: Int, val name: String, val description: String) {
    val users = mutableListOf<User>()
    val lists = mutableListOf<BList>()
}

@Serializable
class BList(val id: Int, val name: String) {
    val cards = mutableListOf<Card>()
}

@Serializable
data class Card(val id: Int, val name: String, val description: String, val dueDate: String?, val pos: Int)

@Serializable
val users = mutableListOf<User>()

@Serializable
val boards = mutableListOf<Board>()
