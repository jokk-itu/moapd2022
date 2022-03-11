package dk.itu.moapd.scootersharing

import android.content.Context
import java.util.*
import kotlin.collections.ArrayList

class RidesDB private constructor(context: Context) {
    private val rides = ArrayList<Scooter>()
    private var lastScooter = Scooter("", "", 0)

    companion object : RidesDBHolder<RidesDB, Context>(::RidesDB)

    init {
        rides.addAll(
            listOf(
                Scooter("Chuck Norris", "ITU"),
                Scooter("Brue Lee", "Fields"),
                Scooter("Rambo", "Kobenhans Lufthavn")
            )
        )
    }

    fun getScooters(): List<Scooter> {
        return rides
    }

    fun addScooter(name: String, where: String) {
        rides.add(Scooter(name, where))
    }

    fun updateScooter(name: String, where: String) {
        rides.parallelStream()
            .filter { it.name == name }
            .findFirst()
            .get()
            .apply { this.where = where }
    }

    fun getLastScooterInfo(): String {
        return lastScooter.toString()
    }

    private fun randomDate(): Long {
        val random = Random()
        val now = System.currentTimeMillis()
        val year = random.nextDouble() * 1000 * 60 * 60 * 24 * 365
        return (now - year).toLong()
    }
}

open class RidesDBHolder<out T : Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile
    private var instance: T? = null
    fun get(arg: A): T {
        val checkInstance = instance
        if (checkInstance != null)
            return checkInstance
        return synchronized(this) {
            val checkInstanceAgain = instance
            if (checkInstanceAgain != null)
                checkInstanceAgain
            else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}