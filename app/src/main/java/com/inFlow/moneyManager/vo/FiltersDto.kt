package com.inFlow.moneyManager.vo

import android.os.Parcelable
import com.inFlow.moneyManager.ui.dashboard.PeriodMode
import com.inFlow.moneyManager.ui.dashboard.ShowTransactions
import com.inFlow.moneyManager.ui.dashboard.SortBy
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.YearMonth

@Parcelize
data class FiltersDto(
    var period: PeriodMode = PeriodMode.WHOLE_MONTH,
    var show: ShowTransactions? = ShowTransactions.SHOW_BOTH,
    var yearMonth: YearMonth? = YearMonth.now(),
    var customRange: Pair<LocalDate?, LocalDate?> = Pair(null, null),
    var sortBy: SortBy = SortBy.SORT_BY_DATE,
    var isDescending: Boolean = true
) : Parcelable
