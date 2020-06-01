package ru.musintimur.photoexplorer.ui.photocollection

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_photo_collection.*
import kotlinx.coroutines.*
import ru.musintimur.photoexplorer.NetworkCallback
import ru.musintimur.photoexplorer.OnSearchClick
import ru.musintimur.photoexplorer.R
import ru.musintimur.photoexplorer.adapters.PhotosRecyclerViewAdapter
import ru.musintimur.photoexplorer.data.preferences.Preferences
import ru.musintimur.photoexplorer.data.preferences.Properties
import ru.musintimur.photoexplorer.network.EmptyResultException
import ru.musintimur.photoexplorer.utils.logE

private const val TAG = "PhotoCollectionFragment"

class PhotoCollectionFragment : Fragment() {

    private val prefs: SharedPreferences? by lazy { context?.getSharedPreferences(Preferences.PREFERENCES.fileName, Context.MODE_PRIVATE) }
    private val args: PhotoCollectionFragmentArgs by navArgs()
    private var collectionId: Int = 0
    private var tagName: String = ""
    private lateinit var photoCollectionViewModel: PhotoCollectionViewModel
    private lateinit var mainActivity: NetworkCallback
    private val photosRecyclerViewAdapter = PhotosRecyclerViewAdapter()
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectionId = args.argCollectionId
        tagName = args.argQuery
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        photoCollectionViewModel =
            ViewModelProvider(this, PhotoCollectionViewModelFactory(requireActivity().application, collectionId, tagName))
                .get(PhotoCollectionViewModel::class.java)

        photoCollectionViewModel.run {
                getAlbum().observe( viewLifecycleOwner, Observer {
                    photosRecyclerViewAdapter.submitList(it)
                    job = CoroutineScope(Dispatchers.Default).launch {
                        delay(5000)
                        if (it.size == 0) {
                            mainActivity.onError(EmptyResultException(getString(R.string.empty_result)))
                        } else {
                            mainActivity.onSuccess()
                        }
                    }
                })
                getError().observe( viewLifecycleOwner, Observer {
                    it?.let {
                        "error.observe've got value: ${it.message}".logE(TAG)
                        mainActivity.onError(it)
                    } ?: mainActivity.onSuccess()
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is OnSearchClick) {
            throw RuntimeException(getString(R.string.error_implement, context, "OnSearchClick"))
        }
        if (context !is NetworkCallback) {
            throw RuntimeException(getString(R.string.error_implement, context, "NetworkCallback"))
        } else {
            mainActivity = context
        }
    }

    override fun onResume() {
        super.onResume()
        (context as OnSearchClick).setOnSearchClick(getString(R.string.search_photos_in_collection)) {
            val query = prefs?.getString(Properties.PREF_SEARCH_QUERY.alias, "") ?: ""
            val action = PhotoCollectionFragmentDirections.actionSearchInCollection(collectionId, query)
            findNavController().navigate(action)
        }
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
    }
}
