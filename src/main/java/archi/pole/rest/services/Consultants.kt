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
     * Get a consultant by name
     */
    fun getConsultantByName(req: RoutingContext, name: String, client: MongoClient) {

        client.find("companies", JsonObject(), { res ->
            if (res.succeeded()) {
                var foundConsultant = JsonObject()
                for (company in res.result()) {
                    val consultantsJson = company.getJsonArray("consultants")
                    for ((index, consultant) in consultantsJson.withIndex()) {
                        if (consultant is JsonObject) {
                            if (consultant.getString("name").equals(name)) {
                                foundConsultant = consultant
                                foundConsultant.put("company", company.getString("name"))
                            }
                        }
                    }

                }
                if (foundConsultant.getString("name") != null) {
                    req.response().endWithJson(foundConsultant)
                } else {
                    req.response().endWithJson("Consultant not found")
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
            obj("consultants.forename" to forename)
        }
        //Mongo projection, i.e.: fields to be returned
        //!!!!!!!!!!!!!!This option only returns the first consultant in every company!!!!!!!!!!!!!!!!!
        val options = FindOptions()
        options.fields = json {
            obj("name" to true, "consultants.name" to true, "consultants.forename" to true, "consultants" to obj("\$slice" to 1))
//            obj("name" to true, "consultants.name" to true, "consultants.forename" to true, "consultants" to obj("\$filter" to obj("forename" to forename)))
        }

        client.findWithOptions("companies", query, options, { res ->
            if (res.succeeded()) {
                if (res.result().isEmpty()) {
                    req.response().endWithJson("Consultant not found")
                } else {
                    req.response().endWithJson(res.result())
                }

            } else {
                res.cause().printStackTrace()
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
            obj("consultants.name" to oldName)
        }

        val newName: String? = body.getString("name")
        val newForename: String? = body.getString("forename")

        // Update name or/and forename
        val update = json {
            obj("\$set" to obj("consultants.$.name" to newName, "consultants.$.forename" to newForename))
        }

        //Update the document
        client.findOneAndUpdate("companies", query, update, { res ->
            if (res.succeeded()) {
                //Send the updated document
                client.find("companies", json { obj("consultants.name" to newName) }, { res ->
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