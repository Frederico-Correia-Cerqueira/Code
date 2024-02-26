package gomoku.repository.jdbi.mappers

import gomoku.domainEntities.Waiter
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.sql.SQLException

class WaiterMapper : RowMapper<Waiter> {
    @Throws(SQLException::class)
    override fun map(rs: ResultSet, ctx: StatementContext?): Waiter {
        return Waiter(
            id = rs.getInt("id"),
            playerID = rs.getInt("playerID")
        )
    }
}