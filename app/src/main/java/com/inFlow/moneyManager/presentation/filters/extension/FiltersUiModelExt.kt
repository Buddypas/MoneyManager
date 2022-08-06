package com.inFlow.moneyManager.presentation.filters.extension

import com.inFlow.moneyManager.presentation.dashboard.model.PeriodMode
import com.inFlow.moneyManager.presentation.filters.model.FiltersUiModel
import com.inFlow.moneyManager.shared.kotlin.toDate
import java.util.*

// TODO: Remove !!
fun FiltersUiModel.getStartAndEndDate(): Pair<Date, Date> {
    val startDate: Date
    val endDate: Date
    if (periodMode == PeriodMode.WHOLE_MONTH && yearMonth != null) {
        startDate = yearMonth.atDay(1).toDate()
        endDate = yearMonth.atEndOfMonth().toDate()
    } else {
        startDate = dateFrom!!.toDate()
        endDate = dateTo!!.toDate()
    }
    return Pair(startDate, endDate)
}
