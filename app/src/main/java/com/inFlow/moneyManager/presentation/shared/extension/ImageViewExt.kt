package com.inFlow.moneyManager.presentation.shared.extension

import android.content.res.ColorStateList
import android.widget.ImageView
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.shared.kotlin.getContextColor

fun ImageView.setIncome() = run {
    setImageResource(R.drawable.arrow_down_right)
    imageTintList =
        ColorStateList.valueOf(context.getContextColor(R.color.green))
}

fun ImageView.setExpense() = run {
    setImageResource(R.drawable.arrow_up_left)
    imageTintList =
        ColorStateList.valueOf(context.getContextColor(R.color.red))
}