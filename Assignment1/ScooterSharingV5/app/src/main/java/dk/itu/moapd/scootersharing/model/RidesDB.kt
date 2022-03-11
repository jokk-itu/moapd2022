package dk.itu.moapd.scootersharing.model

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
                Scooter("Bruce Lee", "Fields"),
                Scooter("Rambo", "Kobenhavns Lufthavn"),
                Scooter("Ninja", "Frederiksvaerk")
            )
        )
    }

    fun getScooters(): List<Scooter> {
        return rides
    }

    fun addScooter(name: String, where: String) {
        val scooter = Scooter(name, where)
        lastScooter = scooter
        rides.add(scooter)
    }

    fun updateScooter(name: String, where: String) : Boolean {
        val scooter = rides.parallelStream()
            .filter { it.name == name }
            .findAny()

        if(scooter.isPresent) {
            scooter.get().apply { this.where = where }
            lastScooter = scooter.get()
            return true
        }

        return false
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