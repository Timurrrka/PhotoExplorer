package ru.musintimur.photoexplorer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.photo_item.view.*
import ru.musintimur.photoexplorer.R
import ru.musintimur.photoexplorer.data.Photo

class PhotosRecyclerViewAdapter(private val photos: MutableSet<Photo>)
    : RecyclerView.Adapter<PhotosRecyclerViewAdapter.Companion.PhotosRecyclerViewHolder>() {

    companion object {
        class PhotosRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }

    fun addPhotos(newPhotos: List<Photo>) {
        photos.addAll(newPhotos)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_item, parent, false)
        return PhotosRecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun onBindViewHolder(holder: PhotosRecyclerViewHolder, position: Int) {
        val photo = photos.elementAt(position)
        holder.itemView.run {
            Picasso.get()
                .load(photo.url_small)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(collectionCover)
        }
    }
}