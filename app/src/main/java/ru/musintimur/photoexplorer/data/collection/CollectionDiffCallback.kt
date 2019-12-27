package ru.musintimur.photoexplorer.data.collection

import androidx.recyclerview.widget.DiffUtil

class CollectionDiffCallback : DiffUtil.ItemCallback<Collection>() {

    override fun areItemsTheSame(oldItem: Collection, newItem: Collection): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Collection, newItem: Collection): Boolean {
        return oldItem == oldItem
    }

}