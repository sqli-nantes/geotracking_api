package archi.pole.rest

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler


import io.vertx.ext.mongo.MongoClient

/**
 * Created by johann on 20/03/17.
 */

@Suppress("unused")
class PremierREST : AbstractVerticle() {

    override fun start(fut: Future<Void>) {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.get("/products").handler(handleListProducts)
        vertx.createHttpServer().requestHandler { router.accept(it) }.listen(8080, { res -> fut.complete() })

        val uri = "mongodb://172.17.0.2:27017"
        val db = "test"
        val mongoconfig = JsonObject.mapFrom(MongoConfig(uri, db))
        val client = MongoClient.createShared(vertx, mongoconfig)

        client.save("products", JsonObject.mapFrom(Country("Russia", "RU")), { id ->
            println("Inserted id: ${id.result()}")
        })

    }

    val handleListProducts = Handler <RoutingContext> { req ->
        req.response().endWithJson(MOCK_ISLANDS)
    }

    private val MOCK_ISLANDS by lazy {
        listOf(
                Island("Kotlin", Country("Russia", "RU")),
                Island("Stewart Island", Country("New Zealand", "NZ")),
                Island("Cockatoo Island", Country("Australia", "AU")),
                Island("Tasmania", Country("Australia", "AU"))
        )
    }

    fun HttpServerResponse.endWithJson(obj: Any) {
        this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
    }
}

data class Country(val name: String, val code: String)

data class Island(val name: String, val country: Country)

data class MongoConfig(val connection_string: String, val db_name: String)
