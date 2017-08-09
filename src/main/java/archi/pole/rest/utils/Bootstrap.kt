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
        cons1.add(1, Consultant("Jean", "Dupond"))
          val cons2 = mutableListOf<Consultant>()
        cons2.add(0, Consultant("Lucie", "Bupont"))
        listOf(
                Company("La Poste", "Auguste Pageot", cons1),
                Company("IBP", "Nantes", cons2),
                Company("SG", "Saint Herblain", cons1),
                Company("CBP", "Nantes", cons2)
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