package com.inFlow.moneyManager.data.mapper

import com.inFlow.moneyManager.data.db.entity.TransactionDto
import com.inFlow.moneyManager.domain.SuspendingMapper
import com.inFlow.moneyManager.domain.transaction.model.Transaction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class TransactionDtoToTransactionMapper(
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SuspendingMapper<TransactionDto, Transaction>(defaultDispatcher) {
    override suspend fun TransactionDto.toMappedEntity(): Transaction =
        Transaction(
            id = transactionId,
            amount = transactionAmount,
            date = transactionDate,
            description = transactionDescription,
            categoryId = transactionCategoryId,
            balanceAfter = transactionBalanceAfter
        )
}
