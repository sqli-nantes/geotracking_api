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
        val cons1 = mutableListOf<Consultant>()
        cons1.add(0, Consultant("Pierre", "Dupont"))
        cons1.add(1, Consultant("Jean", "Dho"))
        cons1.add(2, Consultant("Carine", "Dupont"))
          val cons2 = mutableListOf<Consultant>()
        cons2.add(0, Consultant("Lucie", "Buton"))
        val cons3 = mutableListOf<Consultant>()
        cons3.add(0, Consultant("Jeanne", "Dart"))
        cons3.add(1, Consultant("Herve", "Pour"))
        val cons4 = mutableListOf<Consultant>()
        cons4.add(0, Consultant("Gilles","Dupont"))
                listOf(
                Company("La Poste", "Auguste Pageot", cons1),
                Company("IBP", "Nantes", cons2),
                Company("SG", "Saint Herblain", cons3),
                Company("CBP", "Nantes", cons4)
        )
    }

    fun setupInitialData(client: MongoClient) {
        client.dropCollection("companies", { res ->
            if (res.succeeded()) {
                println("Collection dropped")
            } else {
                res.cause().printStackTrace()
            }
        })
        MOCK_DATA.forEachIndexed { index, company ->
            client.save("companies", JsonObject.mapFrom(company), {
                println("Mock $index saved in db")
            })
        }

    }

}