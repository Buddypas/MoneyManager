package com.inFlow.moneyManager.db.entities

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@Entity(
    tableName = "categories", indices = [Index(
        value = ["categoryName"],
        unique = true
    )]
)
data class Category(
    @PrimaryKey(autoGenerate = true) val categoryId: Int,
    val categoryName: String,
    val categoryType: String, // will be either income or expense
    val categoryIconUrl: String? = null
)

@Dao
interface CategoriesDao {
    @Query("SELECT * FROM categories")
    fun getAll(): List<Category>

    @Query("SELECT * FROM categories WHERE categoryType='expense'")
    fun getAllExpenseCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE categoryType='income'")
    fun getAllIncomeCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE categoryId=:id")
    fun getById(id: Int): Category

    @Update
    fun updateCategories(vararg categories: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg categories: Category)

    @Delete
    fun delete(category: Category)
}