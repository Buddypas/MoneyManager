package com.inFlow.moneyManager.shared.base

interface UiModel

abstract class UiState<T : UiModel> {
    abstract val uiModel: T

    // TODO: Try to add this to state class
//    abstract fun fetchInitialState(): UiState<T>
}

interface UiEvent

