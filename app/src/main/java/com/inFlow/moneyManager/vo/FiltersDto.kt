package com.inFlow.moneyManager.vo

import android.os.Parcelable
import com.inFlow.moneyManager.shared.kotlin.SORT_BY_DATE
import com.inFlow.moneyManager.ui.filters.PeriodMode
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.Month

@Parcelize
data class FiltersDto(
    var period: PeriodMode = PeriodMode.WHOLE_MONTH,
    var showIncomes: Boolean = true,
    var showExpenses: Boolean = true,
    var isDescending: Boolean = true,
    var sortBy: String = SORT_BY_DATE,
    var fromDate: LocalDate? = null,
    var toDate: LocalDate? = null
) : Parcelable