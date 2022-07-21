package com.inFlow.moneyManager.data.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.inFlow.moneyManager.data.db.entities.CategoryDto
import com.inFlow.moneyManager.data.db.entities.TransactionDto

data class CategoriesWithTransactions(
    @Embedded val category: CategoryDto,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "transactionCategoryId"
    )
    val transactions: List<TransactionDto>
)