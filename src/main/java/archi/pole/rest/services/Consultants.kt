package archi.pole.rest.services


import archi.pole.rest.utils.Constants

import io.vertx.core.json.Json
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.DecodeException
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.FindOptions
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.mongo.UpdateOptions
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.*
import io.vertx.core.logging.LoggerFactory


class Consultants {

    var logger = LoggerFactory.getLogger("Consultants")

    /**
     * Get every consultant
     */
    fun getConsultants(req: RoutingContext, client: MongoClient) {
        client.find(Constants().COLLECTION, JsonObject(), { res ->
            if (res.succeeded()) {
                req.response().endWithJson(res.result())
            }
        })
    }

    /**
     * Get consultants by name following the hierarchy of the mongo document
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
        client.findWithOptions(Constants().COLLECTION, query, FindOptions(), { res ->
            if (res.succeeded()) {
                if (res.result().isEmpty()) {
                    req.response().setStatusCode(404).endWithJson(Constants().PERSON_NOT_FOUND)
                } else {
                    req.response().endWithJson(res.result())
                }
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
        client.findWithOptions(Constants().COLLECTION, query, FindOptions(), { res ->
            if (res.succeeded()) {
                if (res.result().isEmpty()) {
                    req.response().setStatusCode(404).endWithJson(Constants().PERSON_NOT_FOUND)
                } else {
                    req.response().endWithJson(res.result())
                }

            }

        })
    }

    /**
     * Get a person by Id
     */
    fun getPersonById(req: RoutingContext, client: MongoClient) {
        val id = req.request().getParam("consultantid")
        val query = json {
            obj("_id" to id)
        }
        client.findWithOptions(Constants().COLLECTION, query, FindOptions(), { res ->
            if (res.succeeded()) {
                if (res.result().isEmpty()) {
                    req.response().setStatusCode(404).endWithJson(Constants().PERSON_NOT_FOUND)
                } else {
                    req.response().endWithJson(res.result())
                }

            }

        })

    }


    /**
     * Update a consultant by ID
     */
    @Throws(DecodeException::class)
    fun updateConsultantById(req: RoutingContext, client: MongoClient) {
        val id = req.request().getParam("consultantid")

        val body = req.bodyAsJson

        // Match one person with id from request parameter
        val query = json {
            obj("_id" to id)
        }

        //List of allowed parameters to prevent insert of unknown fields
        val allowedParams = listOf("name", "forename", "company")
        val allowedParamsCompany = listOf("name", "address")

        //Construction of update request
        val paramUpdate = json { obj() }
        body.map.forEach { param, _ ->
            //Allow only first level properties
            if (!param.isEmpty() && allowedParams.contains(param)) {
                if (param !== "company") {
                    paramUpdate.put(param, body.getString(param))
                } else {
                    //Allow properties inside company object
                    body.getJsonObject(param).map.forEach { paramCompany, value ->
                        if (!paramCompany.isEmpty() && allowedParamsCompany.contains(paramCompany)) {
                            paramUpdate.put("company." + paramCompany, value)
                        }
                    }
                }
            }
        }

        // Update name or/and forename
        val update = json {
            obj("\$set" to paramUpdate)
        }

        val updateOptions = UpdateOptions()
        updateOptions.setReturningNewDocument(true)

        //Update the document
        client.findOneAndUpdateWithOptions(Constants().COLLECTION, query, update, FindOptions(), updateOptions, { res ->
            if (res.succeeded()) {
                if (res.result() === null) {
                    req.response().setStatusCode(404).endWithJson(Constants().PERSON_NOT_FOUND)
                } else {
                    logger.info("Person of id $id updated")
                    req.response().endWithJson(res.result())
                }

            } else {
                res.cause().printStackTrace()
            }
        })


    }

    fun HttpServerResponse.endWithJson(obj: Any) {
        this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(JsonObject().put("result", obj)))
    }


}