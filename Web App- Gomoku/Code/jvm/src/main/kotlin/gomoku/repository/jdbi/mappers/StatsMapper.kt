package gomoku.repository.jdbi.mappers

import gomoku.domainEntities.*
import kotlin.jvm.Throws
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.sql.SQLException
import org.jdbi.v3.core.mapper.RowMapper

class StatsMapper : RowMapper<Stats> {

    @Throws(SQLException::class)
    override fun map(rs: ResultSet?, ctx: StatementContext?): Stats? {
        return if (rs != null) {
            Stats(
                player = rs.getString("player"),
                elo = rs.getDouble("elo"),
                victories = rs.getInt("victories"),
                defeats = rs.getInt("defeats"),
                totalGames = rs.getInt("totalGames"),
                winRate = rs.getInt("winRate"),
                rank = Rank.valueOf(rs.getString("rank"))
            )
        } else null
    }
}

