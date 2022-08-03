package com.inFlow.moneyManager.data.di

import com.inFlow.moneyManager.data.mapper.CategoryDtoToCategoryMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object MapperModule {
    @Provides
    @Singleton
    fun provideCategoryDtoToCategoryMapper() = CategoryDtoToCategoryMapper()
}
