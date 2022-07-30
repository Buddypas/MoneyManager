package com.inFlow.moneyManager.shared.base

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

open class BaseFragment : Fragment() {

    // TODO: Refactor as an extension
    fun navigateSafely(destination: NavDirections) = with(findNavController()) {
        currentDestination?.getAction(destination.actionId)
            ?.let { navigate(destination) }
    }
}
