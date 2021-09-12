package riza.com.cto.view.net.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import riza.com.cto.data.api.APIServiceFactory
import riza.com.cto.data.api.MainAPI
import riza.com.cto.model.AreaPromo
import riza.com.cto.model.Promo
import riza.com.cto.model.PromoRequest
import riza.com.cto.model.UserIds

/**
 * Created by riza@deliv.co.id on 1/21/20.
 */

class HomeVM(application: Application) : AndroidViewModel(application) {

    private val repository = HomeRepository(
        APIServiceFactory.createMain(MainAPI::class.java)
    )

    val promos = MutableLiveData<List<Promo>>()
    val users = arrayListOf<UserIds>()
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()


    val totalPromo = Transformations.map(promos) {
        it.size
    }

    val totalUser = Transformations.map(promos) {
        val listUser = arrayListOf<UserIds>()
        it.forEach {
            listUser.addAll(it.users)
        }
        listUser.groupBy { it.id }.size
    }

    val totalArea = Transformations.map(promos) {
        val areas = arrayListOf<AreaPromo>()
        it.forEach { areas.addAll(it.areas) }
        areas.groupBy { it.id }.size
    }


    fun getAllPromo() = viewModelScope.launch {

        loading.postValue(true)

        val response = repository.getAllPromo()

        if (response.success) {
            response.data?.let {
                promos.postValue(it)
            }

        }

        loading.postValue(false)

    }

    fun getAllUser() = viewModelScope.launch {

        val response = repository.getAllUsers()

        if (response.success) {
            response.data?.let {
                users.clear()
                users.addAll(it.map { UserIds(it.id, it.name) })
            }

        }

    }


    fun addPromo(addPromoRequest: PromoRequest) = viewModelScope.launch {
        repository.addPromo(addPromoRequest).let {
            if (it.success) getAllPromo()
            else error.postValue(it.message.toString())
        }
    }


    fun getUserList(data: List<UserIds>): ArrayList<UserIds> {

        val result = arrayListOf<UserIds>()

        result.addAll(data)

        users.forEach { u1: UserIds ->
            var found = false
            for (u2 in data) {
                if (u2.id == u1.id) {
                    found = true
                    break
                }
            }

            if (!found) result.add(u1)
        }


        return result

    }


}
