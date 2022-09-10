package com.inFlow.moneyManager.domain.transaction.model

import java.time.LocalDate

// TODO: Add LocalDate to db
data class Transaction(
    val amount: Double,
    val description: String,
    val categoryId: Int,
    val id: Int? = null,
    val date: LocalDate? = null,
    val balanceAfter: Double? = null
)
