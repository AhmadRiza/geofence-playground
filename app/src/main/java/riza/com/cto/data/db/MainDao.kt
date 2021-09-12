package riza.com.cto.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.gson.Gson
import riza.com.cto.core.AreaData

/**
 * Created by riza@deliv.co.id on 2/29/20.
 */

@Dao
abstract class MainDao {

    @Insert
    abstract suspend fun insertArea(area: Area): Long

    @Delete
    abstract suspend fun delete(area: Area): Int

    @Query("SELECT * FROM area")
    abstract fun loadAllArea(): LiveData<List<Area>>

    @Query("SELECT * FROM area WHERE name = :areaName")
    abstract fun searchArea(areaName: String): Area?


    @Transaction
    open suspend fun initMalangArea() {

        val gson = Gson()

        AreaData.areaMalang.forEach {
            if (searchArea(it.name) == null) insertArea(Area(0, it.name, gson.toJson(it.points)))
        }


    }


}