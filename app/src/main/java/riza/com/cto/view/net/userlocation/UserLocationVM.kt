package riza.com.cto.view.net.userlocation

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import riza.com.cto.core.Point
import riza.com.cto.data.api.APIServiceFactory
import riza.com.cto.data.api.MainAPI
import riza.com.cto.model.AreaPromo
import riza.com.cto.model.User

/**
 * Created by riza@deliv.co.id on 1/21/20.
 */

class UserLocationVM(application: Application) : AndroidViewModel(application) {

    private val repository = UserLocationRepository(
        APIServiceFactory.createMain(MainAPI::class.java)
    )

    private val gson by lazy { Gson() }

    private val users = MutableLiveData<User>()
    private val areaPromo = MutableLiveData<List<AreaPromo>>()
    val error = MutableLiveData<String>()

    val colors = arrayOf(
        Color.RED,
        Color.BLUE,
        Color.GREEN,
        Color.YELLOW,
        Color.MAGENTA,
        Color.CYAN,
        Color.LTGRAY
    )

    val areas = Transformations.map(areaPromo) {

        val result = arrayListOf<List<LatLng>>()
        it.forEach {
            result.add(
                extractPoint(it.points)
            )
        }

        result
    }

    val points = Transformations.map(users) {

        val result = arrayListOf<Pair<String, List<LatLng>>>()

        result.add(
            Pair(
                "Monday",
                extractPoint(it.locations.monday)
            )
        )

        result.add(
            Pair(
                "Tuesday",
                extractPoint(it.locations.tuesday)
            )
        )

        result.add(
            Pair(
                "Wednesday",
                extractPoint(it.locations.wednesday)
            )
        )
        result.add(
            Pair(
                "Thursday",
                extractPoint(it.locations.thursday)
            )
        )

        result.add(
            Pair(
                "Friday",
                extractPoint(it.locations.thursday)
            )
        )

        result.add(
            Pair(
                "Saturday",
                extractPoint(it.locations.saturday)
            )
        )

        result.add(
            Pair(
                "Sunday",
                extractPoint(it.locations.sunday)
            )
        )

        result
    }

    val center = Transformations.map(users) {
        try {
            extractPoint(it.locations.monday)[0]
        } catch (e: Exception) {
            LatLng(-7.9503817, 112.6063938)
        }
    }

    fun getUser(id: Long) = viewModelScope.launch {

        repository.getUser(id).let {
            if (it.success) {
                it.data?.let { users.postValue(it) }
            } else {
                error.postValue(it.message.toString())
            }
        }

    }

    fun setAreaPromo(area: List<AreaPromo>) {
        areaPromo.value = area
    }

    private fun extractPoint(data: String): List<LatLng> {
        val listType = object : TypeToken<List<Point>>() {}.type
        val list = gson.fromJson<List<Point>>(data, listType)
        return list.map { LatLng(it.y.toDouble(), it.x.toDouble()) }
    }


}
