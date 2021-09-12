package riza.com.cto.core

import kotlin.math.*

/**
 * Created by riza@deliv.co.id on 2/29/20.
 */


object PolygonUtils {


    fun calculateCentroid(data: List<Point>): Point {

        var latSum: Double = 0.0
        var lonSum: Double = 0.0

        data.forEach {
            latSum += it.y
            lonSum += it.x
        }

        return Point(
            (lonSum/data.size),
            (latSum/data.size)
        )

    }

    fun measure(
        lat1: Double,
        lon1: Double,
        lat2:Double,
        lon2:Double
    ): Double{  // generally used geo measurement function
        val R = 6378.137; // Radius of earth in KM
        val dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        val dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        val a = sin(dLat/2) * sin(dLat/2) +
                cos(lat1 * Math.PI / 180) * cos(lat2 * Math.PI / 180) *
                sin(dLon/2) * sin(dLon/2);
        val c = 2 * atan2(sqrt(a), sqrt(1-a));
        val d = R * c;
        return d * 1000; // meters
    }

    /*
    https://gis.stackexchange.com/questions/2951/algorithm-for-offsetting-a-latitude-longitude-by-some-amount-of-meters
     */

    fun meterToDegree(meter: Double) = meter/ 111111

    fun degreeToMeter(degree: Double) = degree * 111111

    fun getOuterRadius(meter: Double, polygon: Polygon): Double{

        val bounding = BoundingBox(polygon)
        val center = calculateCentroid(polygon.points)

        val outerPoints = arrayListOf(
            abs(bounding.xMin - center.x),
            abs(bounding.xMax - center.x),
            abs(bounding.yMin - center.y),
            abs(bounding.yMax - center.y)
        )

        val outer = outerPoints.max()


        return (outer?:0.0) + meterToDegree(meter)

    }


}