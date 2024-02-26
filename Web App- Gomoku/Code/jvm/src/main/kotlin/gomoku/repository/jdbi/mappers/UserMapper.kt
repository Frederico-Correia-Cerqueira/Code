package gomoku.repository.jdbi.mappers

import gomoku.domainEntities.User
import gomoku.domainEntities.parsePlayerStateString
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.sql.SQLException

class UserMapper : RowMapper<User> {

    @Throws(SQLException::class)
    override fun map(rs: ResultSet?, ctx: StatementContext?): User? {
        return if (rs != null) {
            User(
                id = rs.getInt("id"),
                username = rs.getString("username"),
                password = rs.getString("password"),
                state = parsePlayerStateString(rs.getString("state"))
            )
        } else null
    }
}