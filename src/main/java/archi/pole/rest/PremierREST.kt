package archi.pole.rest

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Handler
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

        client = Connection().init(vertx)

        Mock().setupInitialData(client)
    }

    val handleCompanies = Handler <RoutingContext> { req ->
        getCompanies(req)
    }

    fun getCompanies(req: RoutingContext) {
        client.find("companies", JsonObject(), { res ->
            if (res.succeeded()) {
                var companies = JsonArray()
                for (company in res.result()) {
                    val consultantsJson = company.getJsonArray("consultants")
                    val consultants = mutableListOf<Consultant>()
                    for ((index, consultant) in consultantsJson.withIndex()) {
                        // val x: JsonObject? = consultant as? JsonObject
                        // println(x?.getString("name"))

                        //TODO: Smart casts de Kotlin: faire confirmer
                        if (consultant is JsonObject) {
                            consultants.add(index, Consultant(consultant.getString("name"), consultant.getString("forename")))
                        }
                    }

                    companies.add(JsonObject.mapFrom(Company(company.getString("name"), company.getString("address"), consultants)))
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