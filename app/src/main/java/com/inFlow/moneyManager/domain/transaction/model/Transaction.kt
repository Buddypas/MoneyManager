package com.inFlow.moneyManager.domain.transaction.model

import java.util.*

data class Transaction(
    val amount: Double,
    val description: String,
    val categoryId: Int,
    val id: Int? = null,
    val date: Date? = null,
    val balanceAfter: Double? = null
)
