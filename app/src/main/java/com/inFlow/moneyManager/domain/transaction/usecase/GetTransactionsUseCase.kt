package com.inFlow.moneyManager.domain.transaction.usecase

import com.inFlow.moneyManager.domain.SuspendingUseCase
import com.inFlow.moneyManager.domain.transaction.model.Transaction
import com.inFlow.moneyManager.domain.transaction.repository.TransactionRepository
import com.inFlow.moneyManager.presentation.filters.model.FiltersUiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// TODO: Handle filter and query logic in use case instead of repository
class GetTransactionsUseCase(
    private val transactionRepository: TransactionRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SuspendingUseCase<Pair<FiltersUiModel?, String>, List<Transaction>> {
    override suspend fun execute(parameter: Pair<FiltersUiModel?, String>): List<Transaction> =
        withContext(ioDispatcher) {
            transactionRepository.getTransactions(
                filters = parameter.first,
                query = parameter.second
            )
        }
}
