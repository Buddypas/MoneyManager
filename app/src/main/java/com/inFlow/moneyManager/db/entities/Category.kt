package com.inFlow.moneyManager.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories", indices = [Index(
        value = ["name"],
        unique = true
    )]
)
data class Category(
    @PrimaryKey val categoryId: Int,
    val name: String,
    val type: String, // will be either income or expense
    val iconUrl: String? = null
)
