package riza.com.cto.core

class PointInclusion {

    private val polygons by lazy { arrayListOf<Polygon>() }

    fun setDataSet(data: List<Polygon>) {
        polygons.clear()
        polygons.addAll(data)
    }


    /*
    CROSSING NUMBER
     */

    fun findPolygonSurroundingByCN(p: Point): ArrayList<Polygon> {

        val result = arrayListOf<Polygon>()

        polygons.forEach {
            if (analyzePointByCN(it, p)) result.add(it)
        }

        return result

    }

    fun analyzePointByCN(poly: Polygon, p: Point): Boolean {

        val boundingBox = BoundingBox(poly)

        if (boundingBox.isInside(p)) {
            val cn = countCN(poly, p)
//            println("cn is $cn")
            return (cn % 2 != 0)
        } else {
            return false
        }

    }

    private fun countCN(poly: Polygon, p: Point): Int {

        var cn = 0

        val V = poly.points
        val n = V.size - 1

        for (i in 0 until n) {

            if (V[i].x == V[i + 1].x) continue //rule 3

            if (
                (V[i].y <= p.y && V[i + 1].y > p.y)  // rule1
                ||
                (V[i].y > p.y && V[i + 1].y <= p.y) // rule 2
            ) {
                val ray = (p.y - V[i].y) / (V[i + 1].y - V[i].y)

                if (p.x < V[i].x + ray * (V[i + 1].x - V[i].x)) {
                    ++cn
                }

            }

        }

        return cn

    }


    /*
    WINDING NUMBER
     */

    fun findPolygonSurroundingByWN(p: Point): ArrayList<Polygon> {

        val result = arrayListOf<Polygon>()

        polygons.forEach {
            if (analyzePointByWN(it, p)) result.add(it)
        }

        return result

    }

    fun analyzePointByWN(poly: Polygon, p: Point): Boolean {

        val boundingBox = BoundingBox(poly)

        if (boundingBox.isInside(p)) {
            val wn = countWN(poly, p)
//            println("wn is $wn")
            return (wn != 0)
        } else {
            return false
        }
    }

    private fun countWN(poly: Polygon, p: Point): Int {

        var wn = 0

        val v = poly.points
        val n = v.size - 1

        for (i in 0 until n) {

            if (v[i].x == v[i + 1].x) continue //rule 3

            if(v[i].y<=p.y){

                if(v[i+1].y > p.y)
                    if(isLeft(v[i], v[i+1], p)) ++wn

            }else{
                if(v[i+1].y <= p.y)
                    if(!isLeft(v[i], v[i+1], p)) --wn
            }

        }

        return wn

    }

    private fun isLeft(p0: Point, p1: Point, p2: Point): Boolean {
        val n = (p1.x - p0.x) * (p2.y - p0.y) - (p2.x - p0.x) * (p1.y - p0.y)
        return (n > 0)
    }

}