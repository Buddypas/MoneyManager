package com.inFlow.moneyManager.shared.extension

import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun String?.formatDate(): String? {
    this?.let {
        val year = it.substring(0..3)
        val month = it.substring(5..6)
        val day = it.substring(8..9)
        return "$day.$month.$year."
    }
    return null
}

fun String?.formatDateAndTime(): String? {
    this?.let {
        val year = it.substring(0..3)
        val month = it.substring(5..6)
        val day = it.substring(8..9)
        val time = it.substring(11..15)
        return "$day.$month.$year - $time" + "h"
    }
    return null
}

// TODO: Use runCatching and throw
/**
 * Returns null if string is not a valid date
 */
fun String?.toLocalDate(): LocalDate? {
    if (this.isNullOrBlank()) return null
    else {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val date: LocalDate
        try {
            date = LocalDate.parse(this, formatter)
        } catch (e: DateTimeParseException) {
            Timber.e(e)
            return null
        }
        return date
    }
}

fun String?.isValidPassword() = this?.length in 4..30

fun String?.isValidEmail(): Boolean =
    if (this.isNullOrEmpty()) false
    else android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

