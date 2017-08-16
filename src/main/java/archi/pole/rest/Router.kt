package archi.pole.rest

import archi.pole.rest.services.Companies
import archi.pole.rest.services.Consultants
import archi.pole.rest.utils.Connection
import archi.pole.rest.utils.Bootstrap
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler

@Suppress("unused")
class Router : AbstractVerticle() {
    lateinit var client: MongoClient

    override fun start(fut: Future<Void>) {
        val router = Router.router(vertx)

        //Allow cors request for swagger
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.PUT))

        router.route().handler(BodyHandler.create())
        router.get("/companies").handler(handleCompanies)
        router.get("/company/:companyname").handler(handleCompany)
        router.get("/person/name/:consultantname").handler(handleConsultantName)
        router.get("/person/forename/:consultantforename").handler(handleConsultantForename)
        router.get("/people/").handler(handleConsultants)

        router.put("/consultant/:consultantname").handler(handleConsultantPut)

        vertx.createHttpServer().requestHandler({ router.accept(it) }).listen(8080, { res -> fut.complete() })

        client = Connection().init(vertx)

        Bootstrap().setupInitialData(client)
    }

    val handleCompanies = Handler <RoutingContext> { req ->
        Companies().getCompanies(req, client)
    }

    val handleCompany = Handler <RoutingContext> { req ->
        Companies().getCompany(req, client)
    }

    val handleConsultantName = Handler <RoutingContext> { req ->
        Consultants().getConsultantByName(req, client)
    }
    val handleConsultantForename = Handler <RoutingContext> { req ->
        Consultants().getConsultantByForename(req, client)
    }
    val handleConsultants = Handler <RoutingContext> { req ->
        Consultants().getConsultants(req, client)
    }

    val handleConsultantPut = Handler <RoutingContext> { req ->
        Consultants().updateConsultant(req, client)
    }
}