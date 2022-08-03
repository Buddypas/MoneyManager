package com.inFlow.moneyManager.presentation.dashboard.extensions

import java.math.BigDecimal
import java.math.RoundingMode

fun Double.roundToDecimals(numberOfDecimals: Int = 2) =
    BigDecimal(this).setScale(numberOfDecimals, RoundingMode.HALF_UP)
