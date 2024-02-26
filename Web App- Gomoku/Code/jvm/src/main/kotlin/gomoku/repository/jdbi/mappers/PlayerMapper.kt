package gomoku.repository.jdbi.mappers

import gomoku.domainEntities.PlayerData
import gomoku.domainEntities.parsePlayerStateString
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.sql.SQLException
import java.util.UUID

class PlayerMapper : RowMapper<PlayerData> {
    @Throws(SQLException::class)
    override fun map(rs: ResultSet?, ctx: StatementContext?): PlayerData? {
        return if (rs != null) {
            PlayerData(
                id = rs.getInt("id"),
                username = rs.getString("username"),
                token = UUID.fromString(rs.getString("token")),
                state = parsePlayerStateString(rs.getString("state"))
            )
        } else null
    }
}