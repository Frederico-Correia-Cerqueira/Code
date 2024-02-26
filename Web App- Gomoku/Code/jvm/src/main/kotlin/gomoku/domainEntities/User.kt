package gomoku.domainEntities

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val state: PlayerState
)