package archi.pole.rest.services

import archi.pole.rest.utils.Constants
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
    fun getPersonById(req: RoutingContext, client: MongoClient){
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
    fun updateConsultantById(req: RoutingContext, client: MongoClient) {
        val id = req.request().getParam("consultantid")
        val body = req.bodyAsJson

        // Match one consultant with name and forename from request parameter
        val query = json {
            obj("_id" to id)
        }

        val newName: String? = body.getString("name")
        val newForename: String? = body.getString("forename")

        // Update name or/and forename
        val update = json {
            obj("\$set" to obj("name" to newName, "forename" to newForename))
        }

        //Update the document
        client.findOneAndUpdate(Constants().COLLECTION, query, update, { res ->
            if (res.succeeded()) {
                if (res.result() === null) {
                    req.response().setStatusCode(404).endWithJson(Constants().PERSON_NOT_FOUND)
                } else {
                    //Send the updated document
                    client.find(Constants().COLLECTION, json { obj("name" to newName) }, { res ->
                        if (res.succeeded()) {
                            req.response().endWithJson(res.result())
                        } else {
                            res.cause().printStackTrace()
                        }
                    })
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