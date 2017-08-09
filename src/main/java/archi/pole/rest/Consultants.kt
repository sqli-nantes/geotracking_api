package archi.pole.rest


import io.vertx.core.json.Json
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonObject
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

    fun updateConsultant(req: RoutingContext, client: MongoClient) {
        var oldName = req.request().getParam("consultantname")
        var body = req.bodyAsJson

        // Match one consultants with name and forename from request parameter
        var query = json {
            obj("consultants.name" to oldName)
        }

        var newName: String? = body.getString("name")
        var newForename: String? = body.getString("forename")

        // Update name or/and forename
        var update = json {
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