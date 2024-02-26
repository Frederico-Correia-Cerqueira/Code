package gomoku.domainEntities

data class Social(val uri: String, val image: String)
data class Developer(val name: String, val number: String, val socials: List<Social>, val email: String, val image: String)

data class Stats(
    val player: String, val elo: Double, val victories: Int, val defeats: Int, val totalGames: Int, val winRate: Int, val rank: Rank
)

data class StatsList(val stats:List<Stats>)
data class Information(val version: String, val developers:List<Developer>)
enum class Rank {UNRANKED, NOOB, BEGINNER, INTERMEDIATE, EXPERT, PRO}