package gomoku.http.models

data class GamePlayInputModel(val playerID: Int, val l: Int, val c: Int)
data class SaveGameInputModel(val name : String, val description: String = "")
data class SaveGameOutputModel(val id: Int, val game: Int, val name : String, val description: String)