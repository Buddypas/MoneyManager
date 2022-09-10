package com.inFlow.moneyManager.shared.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.text.SpannableStringBuilder
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import androidx.annotation.StringRes
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.inFlow.moneyManager.R

fun View.hideKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
        hideSoftInputFromWindow(windowToken, 0)
    }
}

fun View.showSnackbar(msg: String? = null, @StringRes msgResId: Int? = null) {
    val error = when {
        msgResId != null -> context.getString(msgResId)
        !msg.isNullOrEmpty() -> msg
        else -> context.getString(R.string.error_default)
    }
    Snackbar.make(
        this,
        error,
        Snackbar.LENGTH_SHORT
    ).show()
}

fun View.showWithAnimation() {
    if (!isVisible)
        apply {
            alpha = 0f
            isVisible = true

            animate()
                .alpha(1f)
                .setDuration(150)
                .setListener(null)
        }
}

fun View.hideWithAnimation() {
    if (isVisible)
        animate()
            .alpha(0f)
            .setDuration(150)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    this@hideWithAnimation.isGone = true
                }
            })
}

// TODO: Fix first slash deleted error
fun TextInputLayout.addLiveDateFormatter() {
    editText?.let { inputField ->
        inputField.doOnTextChanged { text, _, before, count ->
            text?.let {
                var newText = it.toString()
                if (before < count) {
                    if (newText.length == 2 || newText.length == 5) {
                        newText += '/'
                        inputField.text = SpannableStringBuilder(newText)
                        inputField.setSelection(newText.length)
                    }
                }
            }
            this.error = null
        }
    }
}

inline fun AutoCompleteTextView.onSelectedItemChanged(crossinline listener: (Int) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            listener(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}