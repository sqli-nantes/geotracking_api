package archi.pole.rest

import archi.pole.rest.services.Companies
import archi.pole.rest.services.Consultants
import archi.pole.rest.utils.Connection
import archi.pole.rest.utils.Bootstrap
import archi.pole.rest.utils.Constants
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.DecodeException
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import java.io.IOException

@Suppress("unused")
class Router : AbstractVerticle() {
    lateinit var client: MongoClient
    val logger: Logger = LoggerFactory.getLogger("Consultants")

    //    @Throws(Exception::class)
    override fun start(fut: Future<Void>) {
        val router = Router.router(vertx)

        //Allow cors request for swagger
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.PUT)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedHeader("Content-Type")
        )

        router.route().handler(BodyHandler.create())
        router.get("/companies").handler(handleCompanies)
        router.get("/company/:companyname").handler(handleCompany)
        router.get("/people/name/:consultantname").handler(handleConsultantName)
        router.get("/people/forename/:consultantforename").handler(handleConsultantForename)
        router.get("/person/id/:consultantid").handler(handlePersonId)
        router.get("/people/").handler(handleConsultants)

        router.put("/person/id/:consultantid").handler(handleConsultantPutId)

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
    val handlePersonId = Handler <RoutingContext> { req: RoutingContext ->
        Consultants().getPersonById(req, client)
    }
    val handleConsultantPutId = Handler <RoutingContext> { req ->
        try {
            Consultants().updateConsultantById(req, client)
        } catch (e: DecodeException) {
            logger.error(Constants().BODY_FORMAT_WRONG_UPDATE_PERSON)
            req.response().setStatusCode(400).endWithJson(Constants().BODY_FORMAT_WRONG_UPDATE_PERSON)
        }
    }

    fun HttpServerResponse.endWithJson(obj: Any) {
        this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(JsonObject().put("result", obj)))
    }

}