package ir.pr4e.aeg_kotlin.roomDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder")
class Reminder {
    @PrimaryKey(true)
    var id = 0

    @ColumnInfo
    var time = ""
    var duration = 0
    var isEnable = false
}