package com.inFlow.moneyManager.presentation.dashboard.model

enum class PeriodMode { WHOLE_MONTH, CUSTOM_RANGE }
enum class ShowTransactions { SHOW_EXPENSES, SHOW_INCOMES, SHOW_BOTH, SHOW_NONE }
enum class SortBy(val sortName: String) {
    SORT_BY_DATE("Date"),
    SORT_BY_CATEGORY("CategoryDto"),
    SORT_BY_AMOUNT("Amount")
}