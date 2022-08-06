package com.inFlow.moneyManager.presentation.shared.extension

import android.text.SpannableStringBuilder
import android.widget.EditText

fun EditText.clear() {
    text = SpannableStringBuilder("")
}

fun EditText.setSpannable(newText: String) {
    text = SpannableStringBuilder(newText)
}