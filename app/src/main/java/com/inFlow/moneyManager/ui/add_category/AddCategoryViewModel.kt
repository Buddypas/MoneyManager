package com.inFlow.moneyManager.ui.add_category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inFlow.moneyManager.repository.AppRepository
import com.inFlow.moneyManager.ui.add_transaction.AddTransactionEvent
import com.inFlow.moneyManager.ui.add_transaction.CategoryType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddCategoryViewModel(private val repository: AppRepository) : ViewModel() {
    var categoryType = CategoryType.EXPENSE

    val eventChannel = Channel<AddCategoryEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    fun saveCategory(name: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveCategory(categoryType, name)
        showSuccess("Category added.")
        navigateUp()
    }

    private suspend fun showError(msg: String? = null) {
        eventChannel.send(AddCategoryEvent.ShowErrorMessage(msg))
    }

    private suspend fun showSuccess(msg: String) {
        eventChannel.send(AddCategoryEvent.ShowSuccessMessage(msg))
    }

//    private suspend fun showLoading(shouldShow: Boolean = true) {
//        eventChannel.send(_root_ide_package_.com.inFlow.moneyManager.ui.add_category.AddCategoryEvent.ShowLoading(shouldShow))
//    }

    private suspend fun navigateUp() {
        eventChannel.send(AddCategoryEvent.NavigateUp)
    }
}

sealed class AddCategoryEvent {
    data class ShowErrorMessage(val msg: String?) : AddCategoryEvent()
    data class ShowSuccessMessage(val msg: String) : AddCategoryEvent()

    //    data class ShowLoading(val shouldShow: Boolean) : AddCategoryEvent()
    object NavigateUp : AddCategoryEvent()
}