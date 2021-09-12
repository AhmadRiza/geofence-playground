package riza.com.cto.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import riza.com.cto.BuildConfig

/**
 * Created by riza@deliv.co.id on 9/30/19.
 */


@Database(
    entities = [
        Area::class
    ],
    version = 1
)

abstract class AppDB : RoomDatabase() {
    abstract fun mainDao(): MainDao

    companion object {
        @Volatile
        private var INSTANCE: AppDB? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDB {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDB::class.java,
                    "cto_database"
                )
                    .addCallback(MyDatabaseCallback(scope))
                    .build()
                INSTANCE = instance

                instance
            }
        }

        private class MyDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {


            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.mainDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)

                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        if (BuildConfig.DEBUG) debugScript(database.mainDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(posDao: MainDao) {

        }

        suspend fun debugScript(mainDao: MainDao) {
        }
    }

}
