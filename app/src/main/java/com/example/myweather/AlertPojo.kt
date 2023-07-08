package com.example.myweather

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

@Entity(tableName = "AlertTable")
@TypeConverters(TypeConverter::class)

data class AlertPojo(
    @PrimaryKey @ColumnInfo(name = "entryid") var id: String = UUID.randomUUID().toString(),
    val start: Long, val end: Long,
    val kind: String,
    var lon: Double = 0.0,
    var lat: Double = 0.0
)

object AlertKind {
    const val NOTIFICATION = "NOTIFICATION"
    const val ALARM = "ALARM"
}