package com.inFlow.moneyManager.shared.extension

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

fun Context.getContextColor(colorId: Int) = ContextCompat.getColor(this, colorId)
fun Context.getContextDrawable(drawableId: Int) = ContextCompat.getDrawable(this, drawableId)
fun Context.getContextFont(fontId: Int) = ResourcesCompat.getFont(this, fontId)
