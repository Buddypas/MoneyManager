package com.inFlow.moneyManager.presentation.dashboard.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.data.db.entities.TransactionDto
import com.inFlow.moneyManager.databinding.ItemTransactionBinding
import com.inFlow.moneyManager.shared.kotlin.getContextColor

// TODO: Improve syntax
class TransactionsAdapter :
    ListAdapter<TransactionDto, TransactionsAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

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
        fun bind(item: TransactionDto) {
            binding.apply {
                nameTxt.text = item.transactionDescription
                amountTxt.text = item.transactionAmount.toString()
                if (item.transactionAmount > 0) typeImg.apply {
                    setImageResource(R.drawable.arrow_down_right)
                    imageTintList =
                        ColorStateList.valueOf(context.getContextColor(R.color.green))
                } else typeImg.apply {
                    setImageResource(R.drawable.arrow_up_left)
                    imageTintList =
                        ColorStateList.valueOf(context.getContextColor(R.color.red))
                }
            }
        }
    }
}

class TransactionDiffCallback : DiffUtil.ItemCallback<TransactionDto>() {
    override fun areItemsTheSame(oldItem: TransactionDto, newItem: TransactionDto) =
        oldItem.transactionId == newItem.transactionId

    override fun areContentsTheSame(oldItem: TransactionDto, newItem: TransactionDto) =
        oldItem == newItem
}
