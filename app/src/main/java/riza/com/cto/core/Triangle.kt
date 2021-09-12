package riza.com.cto.core

import com.google.android.gms.maps.model.LatLng
import org.poly2tri.triangulation.delaunay.DelaunayTriangle
import riza.com.cto.support.debugLog
import java.lang.IndexOutOfBoundsException

data class Triangle(
    var a: Point,
    var b: Point,
    var c: Point
) {

    val points get() = listOf(LatLng(a.y, a.x), LatLng(b.y, b.x), LatLng(c.y, c.x))

    companion object {

        fun fromTriangulation(t: DelaunayTriangle): Triangle? {

            try {
                return Triangle(
                    a = Point(t.points[0].x, t.points[0].y),
                    b = Point(t.points[1].x, t.points[1].y),
                    c = Point(t.points[2].x, t.points[2].y)
                )

            } catch (e: IndexOutOfBoundsException) {
                debugLog(e.localizedMessage)
            }

            return null
        }

    }
}