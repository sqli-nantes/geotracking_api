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

        val uri = "mongodb://172.17.0.2:27017"
        val db = "test"
        val mongoconfig = JsonObject.mapFrom(MongoConfig(uri, db))
        client = MongoClient.createShared(vertx, mongoconfig)
        setupInitialData()

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

    private val MOCK_DATA by lazy {
        val cons1 = mutableListOf<Consultant>()
        cons1.add(0, Consultant("Moi", "ici"))
        cons1.add(1, Consultant("Lui", "là"))
        listOf(
                Company("La Poste", "Auguste Pageot", cons1),
                Company("IBP", "Nantes", cons1),
                Company("SG", "Saint Herblain", cons1),
                Company("CBP", "Nantes", cons1)
        )
    }

    fun HttpServerResponse.endWithJson(obj: Any) {
        this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(obj))
    }

    fun setupInitialData() {
        client.dropCollection("companies", { res ->
            if (res.succeeded()) {
                println("Collection dropped")
            } else {
                res.cause().printStackTrace()
            }
        })
        MOCK_DATA.forEachIndexed { index, company ->
            client.save("companies", JsonObject.mapFrom(company), { id ->
//                println("Inserted id: ${id.result()}")
                println("Mock $index saved in db")
            })
        }

    }
}

data class MongoConfig(val connection_string: String, val db_name: String)
