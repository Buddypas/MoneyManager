package com.inFlow.moneyManager.presentation.filters.model

import android.os.Parcelable
import com.inFlow.moneyManager.presentation.dashboard.model.FieldError
import com.inFlow.moneyManager.presentation.dashboard.model.PeriodMode
import com.inFlow.moneyManager.presentation.dashboard.model.ShowTransactions
import com.inFlow.moneyManager.presentation.dashboard.model.SortBy
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.YearMonth

@Parcelize
data class FiltersUiModel(
    val periodMode: PeriodMode = PeriodMode.WHOLE_MONTH,
    val showTransactions: ShowTransactions = ShowTransactions.SHOW_BOTH,
    val yearMonth: YearMonth? = YearMonth.now(),
    // TODO: Create class for date interval
    val dateFrom: LocalDate? = null,
    val dateTo: LocalDate? = null,
    val sortBy: SortBy = SortBy.SORT_BY_DATE,
    val isDescending: Boolean = true,
    val fieldError: FieldError? = null
): Parcelable
