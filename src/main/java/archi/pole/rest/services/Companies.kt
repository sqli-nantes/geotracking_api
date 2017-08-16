package archi.pole.rest.services

import archi.pole.rest.utils.Constants
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.FindOptions
import io.vertx.ext.web.RoutingContext

import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

/**
 * Created by johann on 20/03/17.
 */


class Companies {

    /**
     * Get every company
     */
    fun getCompanies(req: RoutingContext, client: MongoClient) {
        val query = json {
            obj()
        }
        val options = FindOptions()
        options.fields = json {
            obj("company" to true, "_id" to false)
        }
        client.findWithOptions(Constants().COLLECTION, query, options, { res ->
            if (res.succeeded()) {
                req.response().endWithJson(res.result())
            }
        })
    }


    /**
     * Get company from name
     */
    fun getCompany(req: RoutingContext, client: MongoClient) {
        val name = req.request().getParam("companyname")
        val query = json {
            obj("company.name" to name)
        }

        val fields = json {
            obj("_id" to false, "name" to false, "forename" to false)
        }
        client.findOne(Constants().COLLECTION, query, fields, { res ->
            if (res.succeeded()) {
                if (res.result() === null) {
                    req.response().setStatusCode(404).endWithJson(Constants().COMPANY_NOT_FOUND)
                } else {
                    req.response().endWithJson(res.result())
                }
            }
        })

    }


    fun HttpServerResponse.endWithJson(obj: Any) {
        this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(JsonObject().put("result", obj)))
    }


}