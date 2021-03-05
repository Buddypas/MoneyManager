@file:Suppress("unused")

package com.inFlow.moneyManager.shared.kotlin

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.net.Uri
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.inFlow.moneyManager.R
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.util.*
import kotlin.math.roundToInt

inline fun SearchView.onQueryTextChanged(crossinline listener:(String) -> Unit) {
    setOnQueryTextListener(object: SearchView.OnQueryTextListener {
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

//fun <T> AccountResource.Error<T>.formatErrorMessage(context: Context): String {
//    if(message != null && message.contains("resolve host"))
//        return context.getString(R.string.err_internet)
//
//    return if (errorBody != null)
//        context.getStringFromResName(errorBody.code.trim())
//    else context.getString(R.string.err_server)
//}

//fun View.showError(mError: String?) {
//    val defaultError = context?.getString(R.string.err_server) ?: ""
//    val error = if (mError.isNullOrEmpty()) defaultError
//    else mError
//    Snackbar.make(
//        this,
//        error,
//        Snackbar.LENGTH_SHORT
//    ).show()
//}

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