package ru.musintimur.photoexplorer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.photo_item.view.*
import ru.musintimur.photoexplorer.R
import ru.musintimur.photoexplorer.data.photo.Photo
import ru.musintimur.photoexplorer.data.photo.PhotoDiffCallback

class PhotosRecyclerViewAdapter
    : PagedListAdapter<Photo, PhotosRecyclerViewAdapter.Companion.PhotosRecyclerViewHolder>(PhotoDiffCallback()) {

    var onItemClick: ((Photo) -> Unit)? = null

    companion object {
        class PhotosRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        return PhotosRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotosRecyclerViewHolder, position: Int) {
        getItem(position)?.let { photo ->
            holder.itemView.run {
                Picasso.get()
                    .load(photo.url_small)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(collectionCover)
                collectionCover.setOnClickListener { onItemClick?.invoke(photo) }
            }
        }
    }
}