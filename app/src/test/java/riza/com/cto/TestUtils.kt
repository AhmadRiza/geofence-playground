package riza.com.cto

import riza.com.cto.core.Point
import riza.com.cto.core.Polygon
import riza.com.cto.core.PolygonUtils
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Created by riza@deliv.co.id on 6/30/20.
 */

internal fun getRandomPoint(
    centroid: Point,
    polygon: Polygon,
    meter: Double,
    nUser: Int
): List<Point> {

    val targetTest = arrayListOf<Point>()

    val radius = PolygonUtils.getOuterRadius(meter, polygon)


    for (i in 0 until nUser) {

        //random point in circle

        val a = Math.random() * 2 * PI
        val r = radius * sqrt(Math.random())

        val x = r * cos(a)
        val y = r * sin(a)

        targetTest.add(Point(centroid.x + x, centroid.y + y))

    }

    return targetTest
}

val polygon = Polygon(
    "test_polygon", arrayListOf(
        Point(-7.950309658525885, 112.6062972843647),
        Point(-7.949932777533384, 112.60676365345716),
        Point(-7.950511215002684, 112.60900933295488),
        Point(-7.9512596627336345, 112.60911092162132),
        Point(-7.951211847125289, 112.60816242545843),
        Point(-7.952644984744383, 112.60817315429449),
        Point(-7.954021336710398, 112.60827876627447),
        Point(-7.95397883423389, 112.60698158293961),
        Point(-7.951303493703064, 112.60651521384716),
        Point(-7.950309658525885, 112.6062972843647)
    )
)