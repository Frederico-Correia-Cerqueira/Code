package gomoku
import gomoku.repository.jdbi.mappers.*
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class GomokuApplication  {

    @Bean
    fun jdbi() : Jdbi {
        val jdbcDatabaseURL =
            System.getenv("JDBC_DATABASE_URL")
                ?: "jdbc:postgresql://localhost/postgres?user=postgres&password=PedroAdmin"
        val dataSource = PGSimpleDataSource()
        dataSource.setURL(jdbcDatabaseURL)
        return Jdbi.create(dataSource)
            .registerRowMapper(PlayerMapper())
            .registerRowMapper(BoardMapper())
            .registerRowMapper(GameMapper())
            .registerRowMapper(WaiterMapper())
            .registerRowMapper(StatsMapper())
            .registerRowMapper(UserMapper())
            .registerRowMapper(TokenMapper())
            .registerRowMapper(SavedGamesMapper())
    }
}

fun main(args: Array<String>) {
    runApplication<GomokuApplication>(*args)
}
