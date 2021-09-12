package riza.com.cto

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import riza.com.cto.core.PointInclusion
import riza.com.cto.core.PolygonUtils

/**
 * Created by riza@deliv.co.id on 6/30/20.
 */
@RunWith(JUnit4::class)
class TestSpeedAlgorithm() {

    @Test
    fun testSpeed() {

        println("Testing Speen on WN & CN Algorithm with 100k data")

        val pointInclusion = PointInclusion()

        val centre = PolygonUtils.calculateCentroid(polygon.points)
        val nUser = 100000

        val target = getRandomPoint(centre, polygon, 10.0, nUser)

        var now = System.currentTimeMillis()

        target.forEach {
            pointInclusion.analyzePointByCN(polygon, it)
        }

        println("1. CN total time = ${System.currentTimeMillis() - now} ms")

        now = System.currentTimeMillis()

        target.forEach {
            pointInclusion.analyzePointByWN(polygon, it)
        }

        println("2. WN total time = ${System.currentTimeMillis() - now} ms")

    }


}