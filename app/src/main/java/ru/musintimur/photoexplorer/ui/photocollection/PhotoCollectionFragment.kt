package ru.musintimur.photoexplorer.ui.photocollection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_photo_collection.*
import ru.musintimur.photoexplorer.R
import ru.musintimur.photoexplorer.adapters.PhotosRecyclerViewAdapter
import ru.musintimur.photoexplorer.utils.logD

private const val TAG = "PhotoCollectionFragment"

class PhotoCollectionFragment : Fragment() {

    private val args: PhotoCollectionFragmentArgs by navArgs()
    private var collectionId: Int = 0
    private var tagName: String = ""
    private lateinit var photoCollectionViewModel: PhotoCollectionViewModel
    private val photosRecyclerViewAdapter = PhotosRecyclerViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectionId = args.argCollectionId
        tagName = args.argQuery
        "Data from bundle:\n$collectionId\n$tagName".logD(TAG)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        photoCollectionViewModel =
            ViewModelProviders.of(this).get(PhotoCollectionViewModel::class.java).apply {
                apiKey = getString(R.string.api_key)
                colId = collectionId
                query = tagName
                getAlbum().observe( this@PhotoCollectionFragment, Observer {
                    photosRecyclerViewAdapter.submitList(it)
                })
            }
        return inflater.inflate(R.layout.fragment_photo_collection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (context as AppCompatActivity).supportActionBar?.title = tagName
        recyclerViewPhotoCollection.run {
            layoutManager = LinearLayoutManager(context)
            adapter = photosRecyclerViewAdapter
        }
        photosRecyclerViewAdapter.onItemClick = { photo ->
            val action = PhotoCollectionFragmentDirections.actionOpenPhoto(photo)
            findNavController().navigate(action)
        }
    }
}
