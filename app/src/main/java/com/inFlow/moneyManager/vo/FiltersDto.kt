package com.inFlow.moneyManager.vo

import android.os.Parcelable
import com.inFlow.moneyManager.ui.dashboard.PeriodMode
import com.inFlow.moneyManager.ui.dashboard.SortBy
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.Month

@Parcelize
data class FiltersDto(
    var period: PeriodMode = PeriodMode.WHOLE_MONTH,
    var showIncomes: Boolean = true,
    var showExpenses: Boolean = true,
    var isDescending: Boolean = true,
    var sortBy: SortBy = SortBy.SORT_BY_DATE,
    var fromDate: LocalDate? = null,
    var toDate: LocalDate? = null
) : Parcelable