package ru.musintimur.photoexplorer.ui.collections

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_collections.*
import ru.musintimur.photoexplorer.OnSearchClick
import ru.musintimur.photoexplorer.R
import ru.musintimur.photoexplorer.adapters.CollectionsRecyclerViewAdapter
import ru.musintimur.photoexplorer.data.collection.Collection
import ru.musintimur.photoexplorer.data.preferences.Preferences
import ru.musintimur.photoexplorer.data.preferences.Properties
import ru.musintimur.photoexplorer.ui.home.HomeFragmentDirections
import ru.musintimur.photoexplorer.utils.logD

private const val TAG = "CollectionsFragment"

class CollectionsFragment : Fragment() {

    private val prefs: SharedPreferences? by lazy { context?.getSharedPreferences(Preferences.PREFERENCES.fileName, Context.MODE_PRIVATE) }
    private lateinit var collectionsViewModel: CollectionsViewModel
    private val collectionsAdapter = CollectionsRecyclerViewAdapter()
    private val args: CollectionsFragmentArgs by navArgs()
    private var tagName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        "onCreate called".logD(TAG)
        super.onCreate(savedInstanceState)
        tagName = args.argQuery
        "Data from bundle:\n$$tagName".logD(TAG)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        "onCreateView called".logD(TAG)
        collectionsViewModel =
                ViewModelProviders.of(this).get(CollectionsViewModel::class.java).apply {
                    apiKey = getString(R.string.api_key)
                    query = tagName
                }

        return inflater.inflate(R.layout.fragment_collections, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        "onViewCreated called".logD(TAG)
        super.onViewCreated(view, savedInstanceState)
        recyclerViewCollections.layoutManager = LinearLayoutManager(context)
        recyclerViewCollections.adapter = collectionsAdapter
        collectionsAdapter.onItemClick = { collection ->
            val action = CollectionsFragmentDirections.actionCollectionToPhotos(collection.id, collection.title)
            findNavController().navigate(action)
        }
        collectionsViewModel.getCollections().observe(this, Observer<PagedList<Collection>> {
            "Observer've got data\n$it".logD(TAG)
            collectionsAdapter.submitList(it)
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is OnSearchClick) {
            throw RuntimeException("$context must implement OnSearchClick")
        }
    }

    override fun onResume() {
        super.onResume()
        "onResume: setting onSearchClick".logD(TAG)
        (context as OnSearchClick).setOnSearchClick(getString(R.string.search_collections)) {
            val query = prefs?.getString(Properties.PREF_SEARCH_QUERY.alias, "") ?: ""
            "in setOnSearchClick: query=$query".logD(TAG)
            val action = CollectionsFragmentDirections.actionCollectionSearch(query)
            findNavController().navigate(action)
        }
    }
}