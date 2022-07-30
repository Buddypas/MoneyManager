package com.inFlow.moneyManager.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// TODO: Inject dispatchers
abstract class SuspendingMapper<in T, out R>(
    private val defaultDispatcher: CoroutineDispatcher
) {
    abstract suspend fun T.toMappedEntity(): R

    suspend fun map(model: T): R =
        withContext(defaultDispatcher) {
            model.toMappedEntity()
        }

    suspend fun mapCollection(model: Collection<T>): Collection<R> =
        withContext(defaultDispatcher) {
            model.map {
                map(it)
            }
        }

    suspend fun mapList(model: List<T>): List<R> =
        withContext(defaultDispatcher) {
            model.map {
                map(it)
            }
        }
}
