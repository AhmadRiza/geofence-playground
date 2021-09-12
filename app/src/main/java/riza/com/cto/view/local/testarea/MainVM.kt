package riza.com.cto.view.local.testarea

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import riza.com.cto.data.db.AppDB
import riza.com.cto.data.db.Area

/**
 * Created by riza@deliv.co.id on 1/21/20.
 */

class MainVM(application: Application) : AndroidViewModel(application) {

    private val repository =
        MainRepository(AppDB.getDatabase(application, viewModelScope).mainDao())

    val areas: LiveData<List<Area>> = repository.areas


    fun deleteArea(area: Area) = viewModelScope.launch {
        repository.delete(area)
    }

    fun addArea(area: Area) = viewModelScope.launch {
        repository.addArea(area)
    }


    fun initMalang() = viewModelScope.launch {
        repository.initMalangData()
    }

}
