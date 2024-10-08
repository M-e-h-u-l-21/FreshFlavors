package com.example.foody.util

import androidx.recyclerview.widget.DiffUtil
import com.example.foody.models.Result

class RecipesDiffUtil<T>(
    private val oldList:List<T>,
    private val newList:List<T>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition] // Checks if the memory they are pointing to is also same
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition] // Checks if the content of two item is same
    }

}