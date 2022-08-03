package com.inFlow.moneyManager.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    indices = [
        Index(
            value = ["categoryName"],
            unique = true
        )
    ]
)
data class CategoryDto(
    @PrimaryKey(autoGenerate = true) val categoryId: Int = 0,
    val categoryName: String,
    val categoryType: String, // will be either income or expense
)

