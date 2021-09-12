package riza.com.cto.core

data class Polygon(
    val name: String,
    val points : ArrayList<Point>
){
    init {
        points.getOrNull(0)?.let { points.add(it) }
    }
}