package com.inFlow.moneyManager.domain.transaction.usecase

import com.inFlow.moneyManager.domain.SuspendingUseCase
import com.inFlow.moneyManager.domain.transaction.model.Transaction
import com.inFlow.moneyManager.domain.transaction.repository.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaveTransactionUseCase(
    private val transactionRepository: TransactionRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SuspendingUseCase<Transaction, Unit> {
    override suspend fun execute(parameter: Transaction) = withContext(ioDispatcher) {
        transactionRepository.saveTransaction(parameter)
    }
}
