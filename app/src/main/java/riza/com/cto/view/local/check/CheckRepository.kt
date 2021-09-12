package riza.com.cto.view.local.check

import riza.com.cto.data.db.Area
import riza.com.cto.data.db.MainDao

/**
 * Created by riza@deliv.co.id on 2/29/20.
 */

class CheckRepository(val db: MainDao){

    suspend fun saveArea(area: Area) = db.insertArea(area)


}