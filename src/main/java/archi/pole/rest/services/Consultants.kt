package archi.pole.rest.services

import io.vertx.core.json.Json
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.FindOptions
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.*


class Consultants {

    /**
     * Get every consultant
     */
    fun getConsultants(req: RoutingContext, client: MongoClient) {
        client.find("consultants", JsonObject(), { res ->
            if (res.succeeded()) {
                req.response().endWithJson(res.result())
            }
        })
    }

    /**
     * Get a consultant by name
     */
    fun getConsultantByName(req: RoutingContext, client: MongoClient) {
        val name = req.request().getParam("consultantname")
        val query = json {
            obj("name" to name)
        }
//        val options = FindOptions()
//        options.fields = json {
//            //            obj("name" to true, "forename" to true, "company" to true, "consultants" to obj("\$slice" to 1))
//            obj("name" to true, "forename" to true, "company" to true)
//        }
        client.findWithOptions("consultants", query, FindOptions(), { res ->
            if (res.succeeded()) {
                req.response().endWithJson(res.result())
            }

        })
    }

    /**
     * Get consultants by Forename following the hierarchy of the mongo document
     */
    fun getConsultantByForename(req: RoutingContext, client: MongoClient) {
        val forename = req.request().getParam("consultantforename")
        val query = json {
            obj("forename" to forename)
        }
        client.findWithOptions("consultants", query, FindOptions(), { res ->
            if (res.succeeded()) {
                if(res.result().isEmpty()){
                    req.response().endWithJson("Blablabla consultant non trouvé")
                }else{
                    req.response().endWithJson(res.result())
                }

            }

        })
    }

    /**
     * Update a consultant by name
     */
    fun updateConsultant(req: RoutingContext, client: MongoClient) {
        val oldName = req.request().getParam("consultantname")
        val body = req.bodyAsJson

        // Match one consultant with name and forename from request parameter
        val query = json {
            obj("name" to oldName)
        }

        val newName: String? = body.getString("name")
        val newForename: String? = body.getString("forename")

        // Update name or/and forename
        val update = json {
            obj("\$set" to obj("name" to newName, "forename" to newForename))
        }

        //Update the document
        client.findOneAndUpdate("consultants", query, update, { res ->
            if (res.succeeded()) {
                //Send the updated document
                client.find("consultants", json { obj("name" to newName) }, { res ->
                    if (res.succeeded()) {
                        req.response().endWithJson(res.result())
                    } else {
                        res.cause().printStackTrace()
                    }
                })
            } else {
                res.cause().printStackTrace()
            }

        })


    }

    fun HttpServerResponse.endWithJson(obj: Any) {
        this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(JsonObject().put("result", obj)))
    }


}