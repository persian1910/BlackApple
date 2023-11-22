package ir.pr4e.aeg_kotlin.roomDB

import android.content.Context
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ir.pr4e.black_apple.roomDB.AppDB
import ir.pr4e.black_apple.roomDB.DAO

class RoomBase(activity: Context) {
    val context = activity
    private var database: AppDB? = null
    private var dao: DAO? = null
    var reminder: Reminder = Reminder()
    var setting: Settings = Settings()


    fun createDB() {
        database = AppDB.getInstance(context)
        dao = database?.dao()

        createDefault()
    }

    private fun createDefault(){
        if (settingList(false)?.isEmpty() == true) {
            insertSetting(1, true, "") //TimeFormat
            insertSetting(2, false, "") // Sport MODE
            insertSetting(3,false, "0") // Calibration Temp
            insertSetting(4,false, "0") // Calibration Humidity
            insertSetting(5, false, "{\"from\":1,\"to\":1}") // Set Point Temp
            insertSetting(6, false, "{\"from\":1,\"to\":1}") // Set Point Humidity
        }
    }

    fun listReminder(isShow: Boolean): List<Reminder>? {
        val reminderList = dao?.listReminder()
        if (isShow) {
            if (reminderList != null) {
                for (reminder: Reminder in reminderList) {
                    Log.w(
                        "id--> ${reminder.id}", "time--> ${reminder.time} " +
                                "Duration--> ${reminder.duration} " +
                                "Enable--> ${reminder.isEnable} " +
                                "\nEnd of Column ######"
                    )
                }
            }
        }
        return reminderList
    }

    fun insertReminder(time: String, duration: Int, isEnable: Boolean) {
        reminder = Reminder()
        reminder.time = time
        reminder.duration = duration
        reminder.isEnable = isEnable

        CompositeDisposable().add(Observable.fromCallable {
            if (database != null)
                dao?.insertReminder(reminder)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                listReminder(true)
            })
    }

    fun updateReminder(id: Int, time: String, duration: Int, isEnable: Boolean) {
        reminder = Reminder()
        reminder.id = id
        reminder.time = time
        reminder.duration = duration
        reminder.isEnable = isEnable


        CompositeDisposable().add(Observable.fromCallable {
            if (database != null)
                dao?.updateReminder(reminder)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                listReminder(true)
            })
    }

    fun getReminderByID(id: Int): Reminder? {
        return dao?.getReminderByID(id)
    }

    fun deleteReminderByID(id: Int) {
        CompositeDisposable().add(Observable.fromCallable {
            if (database != null)
                dao?.deleteReminderByID(id)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                listReminder(true)
            })
    }


    //##### Settings ########################
    fun settingList(isShow: Boolean): List<Settings>? {
        val settingList = dao?.listSetting()
        if (isShow) {
            if (settingList != null) {
                for (setting: Settings in settingList) {
                    Log.w(
                        "id--> ${setting.id}", "Enable--> ${setting.enable} " +
                                "temp Json--> ${setting.temp} " +
                                "\nEnd of Column ######"
                    )
                }
            }
        }
        return settingList
    }

    fun getSettingByID(id: Int): Settings? {
        return dao?.getSettingByID(id)
    }

    fun insertSetting(id: Int, enable: Boolean, temp: String) {
        setting = Settings()
        setting.id = id
        setting.enable = enable
        setting.temp = temp

        dao?.insertSettings(setting)
        settingList(true)
    }

    fun updateSetting(id: Int, enable: Boolean, temp: String) {
        setting = Settings()
        setting.id = id
        setting.enable = enable
        setting.temp = temp

        dao?.updateSettings(setting)
        settingList(true)
    }

}



