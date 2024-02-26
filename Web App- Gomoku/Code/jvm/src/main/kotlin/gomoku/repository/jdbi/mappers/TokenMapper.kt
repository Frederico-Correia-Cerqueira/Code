package gomoku.repository.jdbi.mappers

import gomoku.domainEntities.Token
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.sql.SQLException
import java.util.UUID

class TokenMapper : RowMapper<Token> {
    @Throws(SQLException::class)
    override fun map(rs: ResultSet, ctx: StatementContext?): Token {
        return Token(
            token = UUID.fromString(rs.getString("token")),
            userID = rs.getInt("userID")
        )
    }
}