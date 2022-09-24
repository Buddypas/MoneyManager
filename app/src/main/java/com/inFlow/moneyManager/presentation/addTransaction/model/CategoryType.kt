package com.inFlow.moneyManager.presentation.addTransaction.model

enum class CategoryType(val rawValue: String) {
    EXPENSE("expense"), INCOME("income");

    companion object {
        fun fromRawValue(value: String): CategoryType =
            when (value.lowercase()) {
                EXPENSE.rawValue -> EXPENSE
                else -> INCOME
            }
    }
}
