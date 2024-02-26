package pt.isel.ls.http

import org.http4k.routing.ResourceLoader
import org.http4k.routing.routes
import org.http4k.routing.singlePageApp
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.LoggerFactory
import pt.isel.ls.storage.Database
import pt.isel.ls.storage.Storage

/**
 *  File that constitutes the entry point to the server application
 */

private val logger = LoggerFactory.getLogger("pt.isel.ls.http.HTTPServer")

/**
 *  Choose between Memory() or Database()
 */

val storage: Storage = Database()

fun main() {

    val app = routes(
        httpRoutes().first,
        httpRoutes().second,
        singlePageApp(ResourceLoader.Directory("static-content/sparouter"))
    )
    val port = System.getenv("PORT").toInt()
    val jettyServer = app.asServer(Jetty(port)).start()
    logger.info("server started listening on port $port")
    logger.info(System.getenv("JDBC_DATABASE_URL"))
}
