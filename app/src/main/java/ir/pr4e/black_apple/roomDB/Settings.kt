package ir.pr4e.aeg_kotlin.roomDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
class Settings {
    @PrimaryKey(false)
    var id = 1

    @ColumnInfo
    var enable = false
    var temp = ""
}