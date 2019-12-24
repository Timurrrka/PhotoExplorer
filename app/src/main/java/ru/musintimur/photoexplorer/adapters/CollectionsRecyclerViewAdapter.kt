package ru.musintimur.photoexplorer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.collection_item.view.*
import ru.musintimur.photoexplorer.R
import ru.musintimur.photoexplorer.data.Collection

class CollectionsRecyclerViewAdapter(private val collections: MutableSet<Collection>)
    : RecyclerView.Adapter<CollectionsRecyclerViewAdapter.Companion.CollectionsViewHolder>() {

    fun addCollections(newCollections: List<Collection>) {
        collections.addAll(newCollections)
        notifyDataSetChanged()
    }

    companion object {
        class CollectionsViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.collection_item, parent, false)
        return CollectionsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return collections.size
    }

    override fun onBindViewHolder(holder: CollectionsViewHolder, position: Int) {
        val collection = collections.elementAt(position)
                holder.itemView.run {
            collectionTitle.text = collection.title
            Picasso.get()
                .load(collection.coverPhoto.url_small)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(collectionCover)
        }
    }
}