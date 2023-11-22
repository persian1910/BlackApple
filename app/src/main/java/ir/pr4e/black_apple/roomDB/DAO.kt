package ir.pr4e.black_apple.roomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.pr4e.aeg_kotlin.roomDB.Reminder
import ir.pr4e.aeg_kotlin.roomDB.Settings

@Dao
interface DAO {
    @Insert
    fun insertReminder(setting: Reminder)
    @Update
    fun updateReminder(setting: Reminder)

    @Query("SELECT * FROM reminder WHERE id = :id")
    fun getReminderByID(id: Int): Reminder

    @Query("DELETE FROM reminder WHERE id = :id")
    fun deleteReminderByID(id: Int)

    @Query("SELECT * FROM reminder")
    fun listReminder(): List<Reminder>



    @Insert
    fun insertSettings(setting: Settings)
    @Update
    fun updateSettings(setting: Settings)

    @Query("SELECT * FROM settings WHERE id = :id")
    fun getSettingByID(id: Int): Settings


    @Query("SELECT * FROM settings")
    fun listSetting(): List<Settings>

}