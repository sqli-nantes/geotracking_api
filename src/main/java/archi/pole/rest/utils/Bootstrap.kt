package archi.pole.rest.utils

import archi.pole.rest.entities.Company
import archi.pole.rest.entities.Consultant
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient

/**
 * Created by johann on 31/03/17.
 */

class Bootstrap {

    private val MOCK_DATA by lazy {
        listOf(
                Consultant("Pierre", "Dupont", Company("SG", "Saint Herblain")),
                Consultant("Jean", "Dho", Company("La Poste", "Nantes")),
                Consultant("Carine", "Dupont", Company("SG", "Saint Herblain")),
                Consultant("Lucie", "Buton", Company("ADP", "Carquefou")),
                Consultant("Luc", "Dart", Company("La Poste", "Nantes"))
        )
    }

    fun setupInitialData(client: MongoClient) {
        client.dropCollection(Constants().COLLECTION, { res ->
            if (res.succeeded()) {
                println("Collection dropped")
            } else {
                res.cause().printStackTrace()
            }
        })
        MOCK_DATA.forEachIndexed { index, consultant ->
            client.save(Constants().COLLECTION, JsonObject.mapFrom(consultant), {
                println("Mock $index saved in db")
            })
        }

    }

}