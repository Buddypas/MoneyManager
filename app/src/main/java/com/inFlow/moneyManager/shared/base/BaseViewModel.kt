package com.inFlow.moneyManager.shared.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<Model : UiModel, State : UiState<Model>, Event : UiEvent> :
    ViewModel() {

    private val initialState: State by lazy { fetchInitialState() }
    abstract fun fetchInitialState(): State

    private val _stateFlow: MutableStateFlow<State> = MutableStateFlow(initialState)
    private val stateFlow = _stateFlow.asStateFlow()

    private val eventChannel = Channel<Event>()
    private val eventFlow = eventChannel.receiveAsFlow()

    protected fun requireUiState(): State = stateFlow.value

    protected fun Event.emit() = viewModelScope.launch {
        eventChannel.send(this@emit)
    }

    protected fun updateCurrentUiStateWith(uiStateProvider: (Model) -> State) {
        _stateFlow.value = uiStateProvider.invoke(requireUiState().uiModel)
    }

    fun collectState(coroutineScope: CoroutineScope, callback: (State) -> Unit) {
        coroutineScope.launch {
            stateFlow.collectLatest { callback.invoke(it) }
        }
    }

    fun collectEvents(coroutineScope: CoroutineScope, callback: (Event) -> Unit) {
        coroutineScope.launch {
            eventFlow.collect { callback.invoke(it) }
        }
    }
}
