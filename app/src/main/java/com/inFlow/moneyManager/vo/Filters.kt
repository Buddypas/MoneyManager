package com.inFlow.moneyManager.vo

import com.inFlow.moneyManager.ui.filters.PeriodMode
import java.time.LocalDate
import java.time.Month

class Filters(
    var period: PeriodMode,
    var showIncomes: Boolean,
    var showExpenses: Boolean,
    var isDescending: Boolean,
    var sortBy: String,
    var monthAndYear: LocalDate? = null,
    var fromDate: LocalDate? = null,
    var toDate: LocalDate? = null
)