package com.inFlow.moneyManager.domain.di

import com.inFlow.moneyManager.domain.category.repository.CategoryRepository
import com.inFlow.moneyManager.domain.category.usecase.GetCategoriesUseCase
import com.inFlow.moneyManager.domain.category.usecase.GetExpenseCategoriesUseCase
import com.inFlow.moneyManager.domain.category.usecase.GetIncomeCategoriesUseCase
import com.inFlow.moneyManager.domain.category.usecase.SaveCategoryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides
    fun provideGetCategoriesUseCase(categoryRepository: CategoryRepository) =
        GetCategoriesUseCase(categoryRepository)

    @Provides
    fun provideGetExpenseCategoriesUseCase(categoryRepository: CategoryRepository) =
        GetExpenseCategoriesUseCase(categoryRepository)

    @Provides
    fun provideGetIncomeCategoriesUseCase(categoryRepository: CategoryRepository) =
        GetIncomeCategoriesUseCase(categoryRepository)

    @Provides
    fun provideSaveCategoryUseCase(categoryRepository: CategoryRepository) =
        SaveCategoryUseCase(categoryRepository)
}
