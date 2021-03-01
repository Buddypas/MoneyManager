package com.inFlow.moneyManager.db

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.ZoneOffset

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? = value?.let {
        LocalDate.parse(it.toString())
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? =
        date?.atStartOfDay()?.toEpochSecond(ZoneOffset.UTC)
}