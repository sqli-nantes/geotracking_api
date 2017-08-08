package archi.pole.rest

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler

@Suppress("unused")
class Router : AbstractVerticle() {
    lateinit var client: MongoClient

    override fun start(fut: Future<Void>) {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.get("/companies").handler(handleCompanies)
        router.get("/company/:companyid").handler(handleCompany)

        vertx.createHttpServer().requestHandler({ router.accept(it) }).listen(8080, { res -> fut.complete() })

        client = Connection().init(vertx)

        Mock().setupInitialData(client)
    }

    val handleCompanies = Handler <RoutingContext> { req ->
        PremierREST().getCompanies(req, client)
    }

    val handleCompany = Handler <RoutingContext> { req ->
        var id = req.request().getParam("companyid")
        PremierREST().getCompany(req, id, client)
    }
}