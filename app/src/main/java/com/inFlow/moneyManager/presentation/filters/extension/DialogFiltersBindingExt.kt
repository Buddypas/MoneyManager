package com.inFlow.moneyManager.presentation.filters.extension

import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.DialogFiltersBinding
import com.inFlow.moneyManager.shared.kotlin.getContextColor

fun DialogFiltersBinding.selectWholeMonth() {
    disableCustomRangeFields()
    enableWholeMonthFields()
}

fun DialogFiltersBinding.selectCustomRange() {
    enableCustomRangeFields()
    disableWholeMonthFields()
}

private fun DialogFiltersBinding.enableCustomRangeFields() {
    textFromLabel.setTextColor(root.context.getContextColor(R.color.black))
    textToLabel.setTextColor(root.context.getContextColor(R.color.black))
    editTextLayoutFrom.isEnabled = true
    editTextLayoutTo.isEnabled = true
}

private fun DialogFiltersBinding.disableCustomRangeFields() {
    textFromLabel.setTextColor(root.context.getContextColor(R.color.gray))
    textToLabel.setTextColor(root.context.getContextColor(R.color.gray))
    editTextLayoutFrom.isEnabled = false
    editTextLayoutTo.isEnabled = false
}

private fun DialogFiltersBinding.enableWholeMonthFields() {
    textMonthLabel.setTextColor(root.context.getContextColor(R.color.black))
    dropdownLayoutMonth.isEnabled = true
    dropdownLayoutYear.isEnabled = true
}

private fun DialogFiltersBinding.disableWholeMonthFields() {
    textMonthLabel.setTextColor(root.context.getContextColor(R.color.gray))
    dropdownLayoutMonth.isEnabled = false
    dropdownLayoutYear.isEnabled = false
}