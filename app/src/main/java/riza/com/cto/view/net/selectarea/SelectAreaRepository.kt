package riza.com.cto.view.net.selectarea

import riza.com.cto.data.api.MainAPI
import riza.com.cto.model.AddAreaRequest
import riza.com.cto.support.base.BaseRepository

/**
 * Created by riza@deliv.co.id on 2/29/20.
 */

class SelectAreaRepository(
    private val api: MainAPI
) : BaseRepository() {


    suspend fun getAllArea() = callAPI { api.getAllAreaAsync() }

    suspend fun addArea(request: AddAreaRequest) = callAPI { api.addAreaAsync(request) }


}