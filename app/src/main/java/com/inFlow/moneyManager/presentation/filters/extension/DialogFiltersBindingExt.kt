package com.inFlow.moneyManager.presentation.filters.extension

import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.DialogDashboardFiltersBinding
import com.inFlow.moneyManager.shared.extension.getContextColor

fun DialogDashboardFiltersBinding.selectWholeMonth() {
    disableCustomRangeFields()
    enableWholeMonthFields()
}

fun DialogDashboardFiltersBinding.selectCustomRange() {
    enableCustomRangeFields()
    disableWholeMonthFields()
}

private fun DialogDashboardFiltersBinding.enableCustomRangeFields() {
    textFromLabel.setTextColor(root.context.getContextColor(R.color.black))
    textToLabel.setTextColor(root.context.getContextColor(R.color.black))
    editTextLayoutFrom.isEnabled = true
    editTextLayoutTo.isEnabled = true
}

private fun DialogDashboardFiltersBinding.disableCustomRangeFields() {
    textFromLabel.setTextColor(root.context.getContextColor(R.color.gray))
    textToLabel.setTextColor(root.context.getContextColor(R.color.gray))
    editTextLayoutFrom.isEnabled = false
    editTextLayoutTo.isEnabled = false
}

private fun DialogDashboardFiltersBinding.enableWholeMonthFields() {
    textMonthLabel.setTextColor(root.context.getContextColor(R.color.black))
    dropdownLayoutMonth.isEnabled = true
    dropdownLayoutYear.isEnabled = true
}

private fun DialogDashboardFiltersBinding.disableWholeMonthFields() {
    textMonthLabel.setTextColor(root.context.getContextColor(R.color.gray))
    dropdownLayoutMonth.isEnabled = false
    dropdownLayoutYear.isEnabled = false
}