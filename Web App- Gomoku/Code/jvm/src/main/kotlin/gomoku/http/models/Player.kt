package gomoku.http.models

data class PlayerInputModel(val name: String, val password: String)
data class MatchmakingInputModel(val playerID: Int, val gameType: String, val openingRules: String)
