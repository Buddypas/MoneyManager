package com.inFlow.moneyManager.data.db.entities

import androidx.room.*
import kotlinx.coroutines.flow.Flow

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
    val categoryIconUrl: String? = null
)

@Dao
interface CategoriesDao {
    @Query("SELECT * FROM categories")
    fun getAll(): Flow<List<CategoryDto>>

    @Query("SELECT * FROM categories WHERE categoryType='expense'")
    suspend fun getAllExpenseCategories(): List<CategoryDto>

    @Query("SELECT * FROM categories WHERE categoryType='income'")
    suspend fun getAllIncomeCategories(): List<CategoryDto>

    @Query("SELECT * FROM categories WHERE categoryId=:id")
    fun getById(id: Int): CategoryDto

    @Update
    fun updateCategories(vararg categories: CategoryDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg categories: CategoryDto)

    @Delete
    fun delete(category: CategoryDto)
}
