package com.inFlow.moneyManager.domain.transaction.usecase

import com.inFlow.moneyManager.domain.SuspendingNonParameterUseCase
import com.inFlow.moneyManager.domain.transaction.mapper.BalanceDataToBalanceDataUiModelMapper
import com.inFlow.moneyManager.domain.transaction.repository.TransactionRepository
import com.inFlow.moneyManager.presentation.dashboard.model.BalanceDataUiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetExpensesAndIncomesUseCase(
    private val transactionRepository: TransactionRepository,
    private val balanceDataToBalanceDataUiModelMapper: BalanceDataToBalanceDataUiModelMapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SuspendingNonParameterUseCase<BalanceDataUiModel> {
    override suspend fun execute(): BalanceDataUiModel = withContext(ioDispatcher) {
        return@withContext runCatching {
            transactionRepository.calculateExpensesAndIncomes()
        }.mapCatching {
            balanceDataToBalanceDataUiModelMapper.map(it)
        }.getOrThrow()
    }
}
