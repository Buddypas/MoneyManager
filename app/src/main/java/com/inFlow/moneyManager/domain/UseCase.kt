package com.inFlow.moneyManager.domain

internal interface UseCase<in UseCaseParameter, out UseCaseResult> {
    fun execute(parameter: UseCaseParameter): UseCaseResult
}

internal interface NonParameterUseCase<out UseCaseResult> {
    fun execute(): UseCaseResult
}

internal interface SuspendingUseCase<in UseCaseParameter, out UseCaseResult> {
    suspend fun execute(parameter: UseCaseParameter): UseCaseResult
}

interface SuspendingNonParameterUseCase<out UseCaseResult> {
    suspend fun execute(): UseCaseResult
}
