package com.inFlow.moneyManager.db

import androidx.room.Database
import androidx.room.TypeConverters
import com.inFlow.moneyManager.db.entities.Category
import com.inFlow.moneyManager.db.entities.Transaction

@Database(entities = arrayOf(Transaction::class, Category::class), version = 1)
@TypeConverters(Converters::class)
class AppDatabase()