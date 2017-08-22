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
                Consultant("Pierre", "Dupont", Company("SG", "rue du Champ de Tir 44000 Nantes")),
                Consultant("Jean", "Dho", Company("La Poste", "12 boulevard Auguste Pageot Nantes")),
                Consultant("Carine", "Dupont", Company("SG", "rue du Champ de Tir 44000 Nantes")),
                Consultant("Lucie", "Buton", Company("ADP", "Rue Augustin Fresnel 44470 Carquefou")),
                Consultant("Luc", "Dart", Company("La Poste", "12 boulevard Auguste Pageot Nantes"))
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