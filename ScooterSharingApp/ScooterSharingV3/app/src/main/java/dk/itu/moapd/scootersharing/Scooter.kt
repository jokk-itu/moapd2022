package dk.itu.moapd.scootersharing

data class Scooter(
    var name: String = "",
    var where: String = "",
    var timestamp: Long = System.currentTimeMillis()
) {
    override fun toString(): String {
        return "From $timestamp the scooter: $name is placed at $where"
    }
}