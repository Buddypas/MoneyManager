package com.inFlow.moneyManager.shared.extension

import android.text.SpannableStringBuilder
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Returns null if string is not a valid date
 */
fun String?.toLocalDate(): LocalDate? =
    runCatching {
        require(!this.isNullOrBlank()) { "Date string cannot be null or blank" }
        this
    }.mapCatching {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        LocalDate.parse(this, formatter)
    }.onFailure {
        Timber.e("Failed to parse string to LocalDate: $it")
    }.getOrNull()

fun String.toSpannableStringBuilder() = SpannableStringBuilder(this)
