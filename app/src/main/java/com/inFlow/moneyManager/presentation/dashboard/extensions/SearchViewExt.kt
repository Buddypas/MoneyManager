package com.inFlow.moneyManager.presentation.dashboard

import androidx.appcompat.widget.SearchView
import com.inFlow.moneyManager.shared.kotlin.onQueryTextChanged

fun SearchView.setQueryChangedListener(callback: (String) -> Unit) {
    onQueryTextChanged { callback.invoke(it) }
}