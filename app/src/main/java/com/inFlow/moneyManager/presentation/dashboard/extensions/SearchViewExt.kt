package com.inFlow.moneyManager.presentation.dashboard.extensions

import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView

fun SearchView.getSearchText(): TextView = findViewById(androidx.appcompat.R.id.search_src_text)
fun SearchView.getCloseButton(): ImageView = findViewById(androidx.appcompat.R.id.search_close_btn)

fun SearchView.setQueryChangedListener(callback: (String) -> Unit) {
    onQueryTextChanged { callback.invoke(it) }
}

private inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit) {
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean = true

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}
