package com.inFlow.moneyManager.shared.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import kotlinx.coroutines.launch

open class BaseFragment<Model : UiModel, State : UiState<Model>, Event : UiEvent> : Fragment() {
    private val viewModel: BaseViewModel<Model, State, Event> by viewModels()

    // TODO: Add view binding

    fun handleState(callback: (State) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.collectState(this) { state ->
                    callback(state)
                }
            }
        }
    }

    fun handleEvents(callback: (Event) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.collectEvents(this) { event ->
                    callback(event)
                }
            }
        }
    }

    fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
    }
}
