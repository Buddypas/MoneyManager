package com.inFlow.moneyManager.presentation.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inFlow.moneyManager.data.db.entity.TransactionDto
import com.inFlow.moneyManager.databinding.ItemTransactionBinding
import com.inFlow.moneyManager.domain.transaction.model.Transaction
import com.inFlow.moneyManager.presentation.shared.extension.setExpense
import com.inFlow.moneyManager.presentation.shared.extension.setIncome

// TODO: Improve syntax
class TransactionsAdapter :
    ListAdapter<Transaction, TransactionsAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTransactionBinding.inflate(inflater, parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TransactionViewHolder(val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Transaction) {
            with(binding) {
                nameTxt.text = item.description
                amountTxt.text = item.amount.toString()
                typeImg.apply {
                    takeIf { item.amount > 0 }?.setIncome() ?: setExpense()
                }
            }
        }
    }
}

class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction) =
        oldItem == newItem
}
