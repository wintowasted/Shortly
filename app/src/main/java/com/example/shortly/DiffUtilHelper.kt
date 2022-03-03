package com.example.shortly

import androidx.recyclerview.widget.DiffUtil

class DiffUtilHelper(
    private val oldList: MutableList<ShortLink>,
    private val newList: MutableList<ShortLink>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].long_url == newList[newItemPosition].long_url
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}