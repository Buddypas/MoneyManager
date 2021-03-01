package com.inFlow.moneyManager.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.inFlow.moneyManager.db.entities.Category
import com.inFlow.moneyManager.db.entities.Transaction

data class CategoriesWithTransactions(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "transactionCategoryId"
    )
    val transactions: List<Transaction>
)