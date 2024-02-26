package gomoku.http

object PathTemplate {
    const val GAME_BY_ID = "/{id}"
    const val GET_GAME_ID = "/getGameID/{id}"
    const val PLAY = "/{id}"
    const val USER_BY_ID = "/{id}"
    const val SAVE_GAME = "/{id}/savegame/{pid}"
    const val SAVED_GAMES_BY_NAME = "/savedgame/{pid}"
    const val DELETE_GAME = "/{id}/deletegame/{pid}"
    const val SAVED_GAMES_BY_PLAYER = "/savedgames/{pid}"
    const val CREDITS = "/credits"
    const val STATS = "/stats"
    const val PLAYER_STATS = "/stats/{id}"
    const val SWAP = "/{id}/swap"
    const val SWAP_FIRST_MOVE = "/{id}/swap_first_move"
    const val LOGIN = "/login"
    const val LOGOUT = "/logout"
    const val GET_AUTH_COOKIE = "/getAuthCookie"
}