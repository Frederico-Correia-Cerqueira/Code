package gomoku.repository.jdbi.mappers

import gomoku.domainEntities.*
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.sql.SQLException

class GameMapper : RowMapper<Game> {

    @Throws(SQLException::class)
    override fun map(rs: ResultSet, ctx: StatementContext?): Game {
        return Game(
            id = rs.getInt("id"),
            playerB = rs.getInt("playerB"),
            playerW = rs.getInt("playerW"),
            board = Board.fromString(rs.getString("board")),
            variants = parseGameVariantString(rs.getString("type")),
            openingRules = parseOpeningRulesString(rs.getString("rules")),
            info = rs.getBoolean("information")
        )
    }
}