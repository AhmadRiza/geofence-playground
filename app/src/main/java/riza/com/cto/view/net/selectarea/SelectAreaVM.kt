package riza.com.cto.view.net.selectarea

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import riza.com.cto.core.Point
import riza.com.cto.data.api.APIServiceFactory
import riza.com.cto.data.api.MainAPI
import riza.com.cto.data.db.Area
import riza.com.cto.model.AddAreaRequest
import riza.com.cto.model.AreaPromo

/**
 * Created by riza@deliv.co.id on 1/21/20.
 */

class SelectAreaVM(application: Application) : AndroidViewModel(application) {


    private val repository = SelectAreaRepository(
        APIServiceFactory.createMain(MainAPI::class.java)
    )

    private val gson by lazy { Gson() }

    private val areasFromAPI = MutableLiveData<List<AreaPromo>>()
    val error = MutableLiveData<String>()


    val areas = Transformations.map(areasFromAPI) {

        it.map {
            Area(
                it.id,
                it.name,
                it.points
            )
        }

    }

    fun getAllArea() = viewModelScope.launch {
        val res = repository.getAllArea()

        if (res.success) {
            res.data?.let { areasFromAPI.postValue(it) }
        } else {
            error.postValue(res.message)
        }


    }


    fun deleteArea(area: Area) {

    }

    fun addArea(area: Area) = viewModelScope.launch {

        val listType = object : TypeToken<List<Point>>() {}.type
        val points = Gson().fromJson<List<Point>>(area.points, listType)

        repository.addArea(
            AddAreaRequest(
                area.name,
                points
            )
        )

        getAllArea()

    }

}
