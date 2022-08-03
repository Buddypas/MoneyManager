package com.inFlow.moneyManager.data.mapper

import com.inFlow.moneyManager.data.db.entity.TransactionDto
import com.inFlow.moneyManager.domain.SuspendingMapper
import com.inFlow.moneyManager.domain.transaction.model.Transaction
import kotlinx.coroutines.CoroutineDispatcher

class TransactionDtoToTransactionMapper(
    defaultDispatcher: CoroutineDispatcher
) : SuspendingMapper<TransactionDto, Transaction>(defaultDispatcher) {
    override suspend fun TransactionDto.toMappedEntity(): Transaction =
        Transaction(
            transactionId,
            transactionAmount,
            transactionDate,
            transactionDescription,
            transactionCategoryId,
            transactionBalanceAfter
        )
}
