package archi.pole.rest;

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
            val uri = "mongodb://172.17.0.2:27017"
            val db = "test"
            val mongoconfig = JsonObject.mapFrom(MongoConfig(uri, db))
            client = MongoClient.createShared(vertx, mongoconfig)
            return client
        }
}

data class MongoConfig(val connection_string: String, val db_name: String)
