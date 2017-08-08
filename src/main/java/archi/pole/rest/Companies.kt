package archi.pole.rest

import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext

import io.vertx.ext.mongo.MongoClient

/**
 * Created by johann on 20/03/17.
 */


class Companies {

    /**
     * Get every company
     */
    fun getCompanies(req: RoutingContext, client: MongoClient) {
        client.find("companies", JsonObject(), { res ->
            if (res.succeeded()) {
                var companies = JsonArray()
                for (company in res.result()) {
                    val consultantsJson = company.getJsonArray("consultants")
                    val consultants = mutableListOf<Consultant>()
                    for ((index, consultant) in consultantsJson.withIndex()) {
                        //TODO: Smart casts de Kotlin: faire confirmer
                        if (consultant is JsonObject) {
                            consultants.add(index, Consultant(consultant.getString("name"), consultant.getString("forename")))
                        }
                    }
                    companies.add(JsonObject.mapFrom(Company(company.getString("name"), company.getString("address"), consultants)))
                }
                req.response().endWithJson(companies)
            } else {
                res.cause().printStackTrace()
            }
        })
    }


    /**
     * Get company from name
     */
    fun getCompany(req: RoutingContext, id: String, client: MongoClient) {
        client.find("companies", JsonObject(), { res ->
            if (res.succeeded()) {
                var foundCompany = Company()
                for (company in res.result()) {
                    if (company is JsonObject) {
                        if (company.getString("name").equals(id)) {
                            val consultantsJson = company.getJsonArray("consultants")
                            val consultants = mutableListOf<Consultant>()
                            for ((index, consultant) in consultantsJson.withIndex()) {
                                if (consultant is JsonObject) {
                                    consultants.add(index, Consultant(consultant.getString("name"), consultant.getString("forename")))
                                }
                                foundCompany = Company(company.getString("name"), company.getString("address"), consultants)
                            }
                        }
                    }
                }

                if (foundCompany.name != "") {
                    req.response().endWithJson(foundCompany.toJsonObject())
                } else {
                    req.response().endWithJson("Company not found")
                }

            }
        })
    }

    fun HttpServerResponse.endWithJson(obj: Any) {
        this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(JsonObject().put("result", obj)))
    }


}