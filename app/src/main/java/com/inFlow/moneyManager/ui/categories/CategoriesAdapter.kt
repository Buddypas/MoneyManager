package com.inFlow.moneyManager.ui.categories

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inFlow.moneyManager.R
import com.inFlow.moneyManager.databinding.ItemCategoryBinding
import com.inFlow.moneyManager.db.entities.Category
import com.inFlow.moneyManager.db.entities.Transaction
import com.inFlow.moneyManager.shared.kotlin.getContextColor

class CategoriesAdapter :
    ListAdapter<Category, CategoriesAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoryBinding.inflate(inflater, parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Category) {
            binding.apply {
                nameTxt.text = item.categoryName
                if (item.categoryType == "income") typeImg.apply {
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

class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category) =
        oldItem.categoryId == newItem.categoryId

    override fun areContentsTheSame(oldItem: Category, newItem: Category) =
        oldItem == newItem
}
