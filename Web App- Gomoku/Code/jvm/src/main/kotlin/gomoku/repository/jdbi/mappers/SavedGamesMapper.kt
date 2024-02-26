package gomoku.repository.jdbi.mappers


import gomoku.http.models.SaveGameOutputModel
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.sql.SQLException

class SavedGamesMapper : RowMapper<SaveGameOutputModel> {

    @Throws(SQLException::class)
    override fun map(rs: ResultSet, ctx: StatementContext?): SaveGameOutputModel =
        SaveGameOutputModel(
            id = rs.getInt("id"),
            game = rs.getInt("game"),
            name = rs.getString("name"),
            description = rs.getString("description")
        )
}