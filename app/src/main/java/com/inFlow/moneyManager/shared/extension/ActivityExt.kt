package com.inFlow.moneyManager.shared.extension

import android.app.Activity
import android.view.MotionEvent
import android.view.View
import android.widget.EditText

fun Activity.autoHideKeyboard(ev: MotionEvent) {
    val view: View? = currentFocus
    if (view != null &&
        (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) &&
        view is EditText &&
        !view.javaClass.name.startsWith("android.webkit.")
    ) {
        val screenCoordinates = IntArray(2)
        view.getLocationOnScreen(screenCoordinates)
        val x: Float = ev.rawX + view.getLeft() - screenCoordinates[0]
        val y: Float = ev.rawY + view.getTop() - screenCoordinates[1]
        if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) view.hideKeyboard()
    }
}
