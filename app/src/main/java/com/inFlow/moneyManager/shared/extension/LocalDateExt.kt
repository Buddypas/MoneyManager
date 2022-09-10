package com.inFlow.moneyManager.shared.extension

import timber.log.Timber
import java.time.DateTimeException
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

fun LocalDate.toDate(): Date = Date.from(
    this.atStartOfDay()
        .atZone(ZoneId.systemDefault())
        .toInstant()
)

fun LocalDate.toFormattedDate(pattern: String = "dd/MM/yyyy"): String? = try {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    this.format(formatter).orEmpty()
} catch (e: DateTimeException) {
    Timber.e("Failed to format date: $e")
    null
}
