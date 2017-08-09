package archi.pole.rest.utils;

import io.vertx.ext.mongo.MongoClient
import io.vertx.core.json.JsonObject
import io.vertx.core.Vertx


/**
 * Created by johann on 30/03/17.
 */
class Connection {
    lateinit var client: MongoClient
     

    fun init(vertx: Vertx): MongoClient {
        println("Dans init()")

        val uri = System.getenv("MONGO_URI")
        val db = System.getenv("MONGO_DB")
        val mongoconfig = JsonObject.mapFrom(MongoConfig(uri, db))
        client = MongoClient.createShared(vertx, mongoconfig)
        return client
    }
}

data class MongoConfig(val connection_string: String, val db_name: String)