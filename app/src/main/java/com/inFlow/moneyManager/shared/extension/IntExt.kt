package com.inFlow.moneyManager.shared.extension

import android.content.res.Resources
import kotlin.math.roundToInt

// TODO: Sort extensions by receiver type and layer

fun Int.dpToPx(): Int {
    val metrics = Resources.getSystem().displayMetrics
    val px = this * (metrics.densityDpi / 160f)
    return px.roundToInt()
}
