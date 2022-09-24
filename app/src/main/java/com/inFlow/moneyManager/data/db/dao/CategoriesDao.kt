package com.inFlow.moneyManager.data.db.dao

import androidx.room.*
import com.inFlow.moneyManager.data.db.entity.CategoryDto

@Dao
interface CategoriesDao {
    @Query("SELECT * FROM categories")
    suspend fun getAll(): List<CategoryDto>

    @Query("SELECT * FROM categories WHERE categoryType='expense'")
    suspend fun getAllExpenseCategories(): List<CategoryDto>

    @Query("SELECT * FROM categories WHERE categoryType='income'")
    suspend fun getAllIncomeCategories(): List<CategoryDto>

    @Query("SELECT * FROM categories WHERE categoryId=:id")
    fun getById(id: Int): CategoryDto?

    @Update
    fun updateCategories(vararg categories: CategoryDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg categories: CategoryDto)

    @Delete
    fun delete(category: CategoryDto)
}
