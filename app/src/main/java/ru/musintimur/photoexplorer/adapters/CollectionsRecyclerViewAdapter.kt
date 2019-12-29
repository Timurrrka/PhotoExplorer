package ru.musintimur.photoexplorer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.collection_item.view.*
import ru.musintimur.photoexplorer.R
import ru.musintimur.photoexplorer.data.collection.Collection
import ru.musintimur.photoexplorer.data.collection.CollectionDiffCallback

class CollectionsRecyclerViewAdapter :
    PagedListAdapter<Collection, CollectionsRecyclerViewAdapter.Companion.CollectionsViewHolder>(CollectionDiffCallback()) {

    var onItemClick: ((Collection) -> Unit)? = null

    companion object {
        class CollectionsViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.collection_item, parent, false)
        return CollectionsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionsViewHolder, position: Int) {
        getItem(position)?.let { collection ->
            holder.itemView.run {
                val title = "${position.inc()}) ${collection.title}"
                collectionTitle.text = title
                Picasso.get()
                    .load(collection.coverPhoto?.url_small)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(collectionCover)
                collectionTitle.setOnClickListener { onItemClick?.invoke(collection) }
                collectionCover.setOnClickListener { onItemClick?.invoke(collection) }
            }
        }
    }
}