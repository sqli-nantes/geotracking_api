package archi.pole.rest.entities

import io.vertx.core.json.JsonObject

/**
 * Created by johann on 30/03/17.
 */


data class Consultant(val name: String = "", val forename: String = "")


/**
 * First option
 */
class Company constructor(val name: String ="", val address: String = "", val consultants: MutableList<Consultant> = mutableListOf()) {
    fun toJsonObject(): JsonObject {
        var json = JsonObject()
        json.put("name", this.name)
        json.put("address", this.address)
        json.put("consultants", this.consultants)
        return json
    }
}

/**
 * Second option
 */
//class Company {
//    var name: String
//    var address: String
//    var consultants: MutableList<Consultant>
//
//    constructor(name: String, address: String, consultants: MutableList<Consultant>) {
//        this.name = name
//        this.address = address
//        this.consultants = consultants
//    }
//
//    constructor() {
//        this.name = ""
//        this.address = ""
//        this.consultants = mutableListOf()
//    }
//
//    fun toJsonObject(): JsonObject {
//        var json = JsonObject()
//        json.put("name", this.name)
//        json.put("address", this.address)
//        json.put("consultants", this.consultants)
//        return json
//    }
//}