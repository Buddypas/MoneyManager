package com.inFlow.moneyManager.domain.transaction.model

import java.util.*

data class Transaction(
    val id: Int,
    val amount: Double,
    val date: Date,
    val description: String,
    val categoryId: Int,
    val balanceAfter: Double
)
