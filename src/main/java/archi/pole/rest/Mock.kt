package archi.pole.rest

import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient

/**
 * Created by johann on 31/03/17.
 */

class Mock {

    private val MOCK_DATA by lazy {
        val cons1 = mutableListOf<Consultant>()
        cons1.add(0, Consultant("Moi", "ici"))
        cons1.add(1, Consultant("Lui", "là"))
        listOf(
                Company("La Poste", "Auguste Pageot", cons1),
                Company("IBP", "Nantes", cons1),
                Company("SG", "Saint Herblain", cons1),
                Company("CBP", "Nantes", cons1)
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