package com.inFlow.moneyManager.domain.transaction.usecase

import com.inFlow.moneyManager.domain.SuspendingNonParameterUseCase
import com.inFlow.moneyManager.domain.transaction.repository.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetExpensesAndIncomesUseCase(
    private val transactionRepository: TransactionRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SuspendingNonParameterUseCase<Pair<Double, Double>> {
    override suspend fun execute(): Pair<Double, Double> = withContext(ioDispatcher) {
        transactionRepository.calculateExpensesAndIncomes()
    }
}
