package gomoku.repository.jdbi.mappers

import gomoku.domainEntities.Board
import gomoku.domainEntities.Board.Companion.parseMovesString
import gomoku.domainEntities.parseBoardTypeString
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.sql.SQLException

class BoardMapper : RowMapper<Board> {
    @Throws(SQLException::class)
    override fun map(rs: ResultSet, ctx: StatementContext?): Board {
        return Board(
            moves = parseMovesString(rs.getString("moves")),
            turn = rs.getInt("turn"),
            type = parseBoardTypeString(rs.getString("type"))
        )
    }
}