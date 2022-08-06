package com.inFlow.moneyManager.presentation.dashboard.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.YearMonth

@Parcelize
data class Filters(
    var period: PeriodMode = PeriodMode.WHOLE_MONTH,
    var showTransactionsOfType: ShowTransactions? = ShowTransactions.SHOW_BOTH,
    var yearMonth: YearMonth? = YearMonth.now(),
    var customRange: Pair<LocalDate?, LocalDate?> = Pair(null, null),
    var sortBy: SortBy = SortBy.SORT_BY_DATE,
    var isDescending: Boolean = true
) : Parcelable
