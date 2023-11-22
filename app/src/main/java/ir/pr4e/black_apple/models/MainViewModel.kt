package ir.pr4e.black_apple.models

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.AndroidViewModel
import ir.pr4e.aeg_kotlin.roomDB.RoomBase
import ir.pr4e.black_apple.R
import org.json.JSONArray
import org.json.JSONObject
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class MainViewModel(application: Application) : AndroidViewModel(application) {
    var mainObject: JSONObject = JSONObject()
    var mainArray: JSONArray = JSONArray()
    var reminderID = 0
    var reminderDuration = 2

    val roomBase: RoomBase by lazy { RoomBase(getApplication()) }


    fun tabArray(): JSONArray {
        mainObject = JSONObject()
        mainObject.put("name", R.string.timeDate)
        mainObject.put("img", R.drawable.ic_calendar)
        mainArray.put(mainObject)

        mainObject = JSONObject()
        mainObject.put("name", R.string.stopwatch)
        mainObject.put("img", R.drawable.ic_timer)
        mainArray.put(mainObject)

        mainObject = JSONObject()
        mainObject.put("name", R.string.temperature)
        mainObject.put("img", R.drawable.ic_temerature)
        mainArray.put(mainObject)

        return mainArray
    }

    fun toastMotion(ctx: Activity, motionToastStyle: MotionToastStyle, title: String, msg: String) {
        MotionToast.createColorToast(
            ctx,
            title,
            msg,
            motionToastStyle,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(getApplication(), R.font.font)
        )
    }
}