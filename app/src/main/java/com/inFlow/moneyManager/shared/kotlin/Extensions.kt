@file:Suppress("unused")

package com.inFlow.moneyManager.shared.kotlin

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.TransitionDrawable
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.inFlow.moneyManager.R
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import kotlin.math.roundToInt

/**
 * This method is zero based, i.e. January returns 0, December returns 11
 */
fun String.getMonthPosition(): Int {
    return when (this) {
        "Jan" -> 0
        "Feb" -> 1
        "Mar" -> 2
        "Apr" -> 3
        "May" -> 4
        "Jun" -> 5
        "Jul" -> 6
        "Aug" -> 7
        "Sep" -> 8
        "Oct" -> 9
        "Nov" -> 10
        else -> 11
    }
}

/**
 * Call this method (in onActivityCreated or later) to set
 * the width of the dialog to a percentage of the current
 * screen width.
 */
fun DialogFragment.setFullWidth() {
    if (dialog != null && dialog?.window != null) {
        val params = dialog!!.window!!.attributes.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }
}

inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit) {
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}

fun String?.isValidPassword() = this?.length in 4..30

fun String?.isValidEmail(): Boolean {
    return if (this.isNullOrEmpty()) false
    else android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Returns null if string is not a valid date
 */
fun String?.toLocalDate(): LocalDate? {
    if (this.isNullOrBlank()) return null
    else {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val date: LocalDate
        try {
            date = LocalDate.parse(this, formatter)
        } catch (e: DateTimeParseException) {
            Timber.e(e)
            return null
        }
        val today = LocalDate.now()
        if(date.isAfter(today)) return null
        return date
    }
}

fun Context.getContextColor(colorId: Int) = ContextCompat.getColor(this, colorId)
fun Context.getContextDrawable(drawableId: Int) = ContextCompat.getDrawable(this, drawableId)
fun Context.getContextFont(fontId: Int) = ResourcesCompat.getFont(this, fontId)

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE
    else View.GONE
}

fun View.hideKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
        hideSoftInputFromWindow(windowToken, 0)
    }
}

fun View.setAsRootView() {
    isFocusable = true
    isFocusableInTouchMode = true
    isClickable = true

    onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (hasFocus) v.hideKeyboard()
    }
}

fun View.showError(mError: String?) {
    val error = if (mError.isNullOrEmpty()) "An error occured"
    else mError
    Snackbar.make(
        this,
        error,
        Snackbar.LENGTH_SHORT
    ).show()
}

fun Context.showInfoDialog(messageResId: Int) {
    AlertDialog.Builder(this)
        .setMessage(messageResId)
        .setPositiveButton(
            R.string.ok,
            null
        )
        .show()
}

fun View.showWithAnimation() {
    if (visibility != View.VISIBLE)
        this.apply {
            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .setDuration(150)
                .setListener(null)
        }
}

fun View.hideWithAnimation() {
    if (this.visibility == View.VISIBLE) {
        this.animate()
            .alpha(0f)
            .setDuration(150)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    this@hideWithAnimation.visibility = View.GONE
                }
            })
    }
}

fun View.changeBackgroundAnimated(drawableId: Int) {
    val colorDrawables = arrayOf(
        background,
        ContextCompat.getDrawable(context, drawableId)
    )
    val transitionDrawable = TransitionDrawable(colorDrawables)
    background = transitionDrawable
    transitionDrawable.startTransition(200)
}

fun SearchView.getSearchText(): TextView {
    return findViewById(androidx.appcompat.R.id.search_src_text)
}

fun PopupWindow.setElementOnClick(id: Int, function: () -> Unit) {
    contentView.findViewById<View>(id).setOnClickListener {
        function()
    }
}

fun PopupWindow.dimBackground() {
    val container = this.contentView.rootView
    val context = this.contentView.context
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val lp = container.layoutParams as WindowManager.LayoutParams
    lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
    lp.dimAmount = 0.5f
    wm.updateViewLayout(container, lp)
}

fun Int.dpToPx(): Int {
    val metrics = Resources.getSystem().displayMetrics
    val px = this * (metrics.densityDpi / 160f)
    return px.roundToInt()
}

fun String?.formatDate(): String? {
    this?.let {
        val year = it.substring(0..3)
        val month = it.substring(5..6)
        val day = it.substring(8..9)
        return "$day.$month.$year."
    }
    return null
}

fun String?.formatDateAndTime(): String? {
    this?.let {
        val year = it.substring(0..3)
        val month = it.substring(5..6)
        val day = it.substring(8..9)
        val time = it.substring(11..15)
        return "$day.$month.$year - $time" + "h"
    }
    return null
}