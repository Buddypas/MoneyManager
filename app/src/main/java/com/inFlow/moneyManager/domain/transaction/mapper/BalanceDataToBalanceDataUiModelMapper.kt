package com.inFlow.moneyManager.domain.transaction.mapper

import com.inFlow.moneyManager.domain.SuspendingMapper
import com.inFlow.moneyManager.domain.transaction.model.BalanceData
import com.inFlow.moneyManager.presentation.dashboard.model.BalanceDataUiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class BalanceDataToBalanceDataUiModelMapper(
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SuspendingMapper<BalanceData, BalanceDataUiModel>(defaultDispatcher) {

    override suspend fun BalanceData.toMappedEntity(): BalanceDataUiModel =
        BalanceDataUiModel(this.incomes, this.expenses)
}
