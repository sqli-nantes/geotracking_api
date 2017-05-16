package archi.pole.rest

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
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
    lateinit var client: MongoClient

    override fun start(fut: Future<Void>) {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.get("/companies").handler(handleCompanies)
        vertx.createHttpServer().requestHandler { router.accept(it) }.listen(8080, { res -> fut.complete() })
        
        client = Connection().init(vertx);

        Mock().setupInitialData(client)
    }

    val handleCompanies = Handler <RoutingContext> { req ->
        getCompanies(req)
    }

    fun getCompanies(req: RoutingContext) {
        client.find("companies", JsonObject(), { res ->
            if (res.succeeded()) {
                var companies = JsonArray()
                for (json in res.result()) {
                    companies.add(JsonObject.mapFrom(Company(json.getString("name"), json.getString("address"), mutableListOf<Consultant>())))
                }

                req.response().endWithJson(companies)
            } else {
                res.cause().printStackTrace()
            }

        })
    }

    fun HttpServerResponse.endWithJson(obj: Any) {
        this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
    }


}

// data class MongoConfig(val connection_string: String, val db_name: String)
