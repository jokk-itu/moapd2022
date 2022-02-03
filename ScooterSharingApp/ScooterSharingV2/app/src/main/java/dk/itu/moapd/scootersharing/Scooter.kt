package dk.itu.moapd.scootersharing

data class Scooter(var name : String = "", var where : String = "") {
    override fun toString(): String {
        return "$name is placed at $where"
    }
}