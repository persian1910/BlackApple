package ir.pr4e.black_apple.roomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ir.pr4e.aeg_kotlin.roomDB.Reminder
import ir.pr4e.aeg_kotlin.roomDB.Settings

@Database(entities = [Reminder::class, Settings::class], version = 1, exportSchema = false)
abstract class AppDB: RoomDatabase() {
    abstract fun dao(): DAO

    companion object {
        private var INSTANCE: AppDB? = null

        fun getInstance(context: Context): AppDB? {
            if (INSTANCE == null) {
                synchronized(AppDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDB::class.java, "db_black_apple" // Database Name
                    ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
                }
            }
            return INSTANCE
        }

//        fun destroyInstance() {
//            INSTANCE = null
//        }
    }
}