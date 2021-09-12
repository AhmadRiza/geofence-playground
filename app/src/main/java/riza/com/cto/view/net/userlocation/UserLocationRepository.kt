package riza.com.cto.view.net.userlocation

import riza.com.cto.data.api.MainAPI
import riza.com.cto.support.base.BaseRepository

/**
 * Created by riza@deliv.co.id on 5/8/20.
 */

class UserLocationRepository(
    private val api: MainAPI
) : BaseRepository() {


    suspend fun getUser(id: Long) = callAPI {
        api.getUserAsync(id)
    }

}