package riza.com.cto.view.local.check

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import earcut4j.Earcut
import kotlinx.coroutines.launch
import org.poly2tri.Poly2Tri
import org.poly2tri.geometry.polygon.PolygonPoint
import org.poly2tri.triangulation.Triangulatable
import org.poly2tri.triangulation.TriangulationAlgorithm
import riza.com.cto.core.*
import riza.com.cto.data.db.AppDB
import riza.com.cto.data.db.Area
import riza.com.cto.support.CSVWriterHelper
import riza.com.cto.support.debugLog
import java.io.File
import java.lang.Math.random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import org.poly2tri.geometry.polygon.Polygon as PolygonInput

/**
 * Created by riza@deliv.co.id on 1/21/20.
 */

class CheckVM(application: Application) : AndroidViewModel(application) {

    private val csvHelper = CSVWriterHelper(application)

    private val _polygonData = MutableLiveData<ArrayList<LatLng>>()
    val polygonData: LiveData<ArrayList<LatLng>> = _polygonData


    private val _centroid = MutableLiveData<LatLng>()
    val centroid: LiveData<LatLng> = _centroid

    private val _nUser = MutableLiveData<Int>()
    val nUser: LiveData<Int> = _nUser

    private val _radius = MutableLiveData<Int>()
    val radius: LiveData<Int> = _radius

    private val _milis = MutableLiveData<Long>()
    val milis: LiveData<Long> = _milis

    private val _listTest = MutableLiveData<ArrayList<Pair<LatLng, Boolean>>>()
    val listTest: LiveData<ArrayList<Pair<LatLng, Boolean>>> = _listTest

    private val targetTest = arrayListOf<Point>()

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _outputFolder = MutableLiveData<File>()
    val outputFolder: LiveData<File> = _outputFolder

    private val _triangles = MutableLiveData<List<Triangle>>()
    val triangle: LiveData<List<Triangle>> = _triangles


    private var mPolygon: Polygon? = null
    private var isUsingWN = true
    private val repository: CheckRepository
    private val geofencing: PointInclusion

    init {
        val db = AppDB.getDatabase(application, viewModelScope)
        repository = CheckRepository(db.mainDao())
        geofencing = PointInclusion()
    }

    fun setPolygonData(area: Area) = viewModelScope.launch {

        debugLog(area.toString())

        val listType = object : TypeToken<List<Point>>() {}.type
        val points = Gson().fromJson<List<Point>>(area.points, listType)

        mPolygon = Polygon(area.name, ArrayList(points))

        val data = arrayListOf<LatLng>()

        points.forEach {

            data.add(
                LatLng(it.y, it.x)
            )

        }

        _polygonData.postValue(data)

        val center = PolygonUtils.calculateCentroid(points)
        _centroid.postValue(LatLng(center.y, center.x))

        _radius.postValue(100)
        _nUser.postValue(100)

        triangulate(mPolygon!!.points)

    }
/*
    private fun slicePoligon(poligons: List<Point>){

        val factor = 100000

        val points = mutableListOf<Double>()
        poligons.forEach {
            points.add(it.x * factor)
            points.add(it.y * factor)
        }

        val triPoints = Earcut.earcut(points.toDoubleArray(), null, 2)

        val realTriPoints = triPoints.map {
            it.toDouble() / factor
        }

        val triangles = mutableListOf<Triangle>()

        var triangle = Triangle()
        realTriPoints.forEach {
            triangle.fill(it)
            if(triangle.isComplete) {
                triangles.add(triangle.copy())
                triangle = Triangle()
            }
        }

        _triangles.value = triangles
    }
    *
 */

    private fun triangulate(points: List<Point>) = viewModelScope.launch {

        val fixedPoint = points.subList(0, points.size-2)

        val input = PolygonInput(
            fixedPoint.map {
                PolygonPoint(it.x, it.y)
            }
        )



        debugLog(input.points)

        Poly2Tri.triangulate(input)

        val triangles = mutableListOf<Triangle>()

        input.triangles.map {
            Triangle.fromTriangulation(it)?.let { triangles.add(it) }
        }

        _triangles.value = triangles

    }

    fun setNUser(n: Int) {
        _nUser.value = n * 10
    }

    fun setRadius(r: Int) {
        _radius.value = r * 10
    }

    fun setisUsingWN(isWN: Boolean) = viewModelScope.launch {
        isUsingWN = isWN
        checkWith(isWN)
    }

    val displayRadius = Transformations.map(radius) {
        PolygonUtils.degreeToMeter(PolygonUtils.getOuterRadius(it.toDouble(), mPolygon!!))
    }

    fun singleTest() = viewModelScope.launch {

        val it = _centroid.value!!

        val result = arrayListOf<Pair<LatLng, Boolean>>()
        val radius = PolygonUtils.getOuterRadius((radius.value ?: 0).toDouble(), mPolygon!!)

        var time = 0L

        for (i in 0 until (nUser.value ?: 0)) {

            //random point in circle

            val a = random() * 2 * PI
            val r = radius * sqrt(random())

            val x = r * cos(a)
            val y = r * sin(a)


            val now = System.currentTimeMillis()
            val isInside = if (isUsingWN) {
                geofencing.analyzePointByWN(mPolygon!!, Point(it.longitude + x, it.latitude + y))
            } else {
                geofencing.analyzePointByCN(mPolygon!!, Point(it.longitude + x, it.latitude + y))
            }
            val end = System.currentTimeMillis()

            time += (end - now)

            result.add(
                Pair(LatLng(it.latitude + y, it.longitude + x), isInside)
            )

        }

        debugLog("time = $time")

        _milis.postValue(
            time
        )

        _listTest.postValue(result)


    }

    fun generateSingleTest() = viewModelScope.launch {

        val it = _centroid.value!!

        targetTest.clear()

        val radius = PolygonUtils.getOuterRadius((radius.value ?: 0).toDouble(), mPolygon!!)


        for (i in 0 until (nUser.value ?: 0)) {

            //random point in circle

            val a = random() * 2 * PI
            val r = radius * sqrt(random())

            val x = r * cos(a)
            val y = r * sin(a)

            targetTest.add(Point(it.longitude + x, it.latitude + y))

        }

        checkWith(isUsingWN)

    }

    private fun checkWith(isWN: Boolean) {

        val result = arrayListOf<Pair<LatLng, Boolean>>()

        if (targetTest.isEmpty()) return

        var time = 0L

        val now = System.currentTimeMillis()

        targetTest.forEach {
            val isInside = if (isUsingWN) {
                geofencing.analyzePointByWN(mPolygon!!, it)
            } else {
                geofencing.analyzePointByCN(mPolygon!!, it)
            }
            result.add(
                Pair(LatLng(it.y, it.x), isInside)
            )
        }

        val end = System.currentTimeMillis()
        time = end - now

        _milis.postValue(
            time
        )

        _listTest.postValue(result)

    }


    fun saveToCSV() = viewModelScope.launch {

        mPolygon?.let {
            csvHelper.writePoly(it)
        }

        _listTest.value?.let {
            csvHelper.writeCheckResult(it, if (isUsingWN) "WN" else "CN")
        }

        _message.postValue("Saved as CSV")

        _outputFolder.postValue(csvHelper.filePath)

    }

}
