import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.IOException

fun main(args: Array<String>) {
    val goddess = Person("Goddess", Int.MAX_VALUE, null)
    val eve = Person("Eve", Int.MAX_VALUE - 1, goddess)

    val kain = Person("Kain", Int.MAX_VALUE - 20, eve)
    val abel = Person("Abel", Int.MAX_VALUE - 21, eve)
    val aclima = Person("Aclima", Int.MAX_VALUE - 22, eve)
    val seth = Person("Seth", Int.MAX_VALUE - 23, eve)

    val enoch = Person("Enoch", Int.MAX_VALUE - 40, kain)
    val lamech = Person("Lamech", Int.MAX_VALUE - 41, kain)

    val lemech = Person("Lemech", Int.MAX_VALUE - 60, enoch)
    val ada = Person("Ada", Int.MAX_VALUE - 61, enoch)

    val enosh = Person("Enosh", Int.MAX_VALUE - 60, seth)

    val people = sequenceOf(goddess, eve, kain, abel, aclima, seth, enoch, lamech, lemech, ada, enosh)

    var directDescendantsOfEve = people.filter { descendant("Eve", it) }
    var immediateDescendantsOfEve = people.filter { immediateDescendant("Eve", it) }
    var notImmediateDescendantsOfEve = people.filter { !immediateDescendant("Eve", it) }
}

fun descendant(name : String, person : Person?) : Boolean {
    if (person == null)
        return false

    return when (person.name) {
        name -> true
        else  -> descendant(name, person.mother)
    }
}

fun immediateDescendant(name : String, person : Person) : Boolean {
    return person.name == "Eve" || person.mother?.name == name
}

fun Person.isChildOfEve() : Boolean {
    return this.mother?.name == "Eve"
}

fun readGenealogy() {
    try {
        val genealogy = File("genealogy.txt")
        val input = FileReader(genealogy)
        val lines = input.readLines()
        val persons = lines.map {
            val split = it.split("$ +^")
            Person(split[0], split[1].toInt(), null)
        }
    } catch (e : IOException) {
        println(e.message)
    }
}