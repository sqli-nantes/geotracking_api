package archi.pole.rest

/**
 * Created by johann on 30/03/17.
 */


data class Consultant(val name: String = "", val forename: String = "")

data class Company(val name: String = "", val address: String = "", val consultants: MutableList<Consultant> = mutableListOf())
