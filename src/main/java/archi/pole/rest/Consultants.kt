package archi.pole.rest


import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.RoutingContext


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

    fun HttpServerResponse.endWithJson(obj: Any) {
        this.putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(JsonObject().put("result", obj)))
    }


}