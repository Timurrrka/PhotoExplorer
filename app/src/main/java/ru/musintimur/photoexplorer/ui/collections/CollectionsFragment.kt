package ru.musintimur.photoexplorer.ui.collections

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_collections.*
import kotlinx.coroutines.*
import ru.musintimur.photoexplorer.NetworkCallback
import ru.musintimur.photoexplorer.OnSearchClick
import ru.musintimur.photoexplorer.R
import ru.musintimur.photoexplorer.adapters.CollectionsRecyclerViewAdapter
import ru.musintimur.photoexplorer.data.preferences.Preferences
import ru.musintimur.photoexplorer.data.preferences.Properties
import ru.musintimur.photoexplorer.network.EmptyResultException
import ru.musintimur.photoexplorer.utils.logE

private const val TAG = "CollectionsFragment"

class CollectionsFragment : Fragment() {

    private val prefs: SharedPreferences? by lazy {
        context?.getSharedPreferences(
            Preferences.PREFERENCES.fileName,
            Context.MODE_PRIVATE
        )
    }
    private lateinit var collectionsViewModel: CollectionsViewModel
    private lateinit var mainActivity: NetworkCallback
    private val collectionsAdapter = CollectionsRecyclerViewAdapter()
    private val args: CollectionsFragmentArgs by navArgs()
    private var tagName: String = ""
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tagName = args.argQuery
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        collectionsViewModel =
            ViewModelProvider(this).get(CollectionsViewModel::class.java).apply {
                query = tagName
                getError().observe(this@CollectionsFragment, Observer {
                    it?.let {
                        "error.observe've got value: ${it.message}".logE(TAG)
                        mainActivity.onError(it)
                    } ?: mainActivity.onSuccess()
                })
            }

        return inflater.inflate(R.layout.fragment_collections, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewCollections.layoutManager = LinearLayoutManager(context)
        recyclerViewCollections.adapter = collectionsAdapter
        collectionsAdapter.onItemClick = { collection ->
            val action = CollectionsFragmentDirections.actionCollectionToPhotos(collection.id, collection.title)
            findNavController().navigate(action)
        }
        collectionsViewModel.getCollections().observe(this, Observer {
            collectionsAdapter.submitList(it)
            job = CoroutineScope(Dispatchers.Default).launch {
                delay(5000)
                if (it.size == 0) {
                    mainActivity.onError(EmptyResultException(getString(R.string.empty_result)))
                } else {
                    mainActivity.onSuccess()
                }
            }
        })
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
        (context as OnSearchClick).setOnSearchClick(getString(R.string.search_collections)) {
            val query = prefs?.getString(Properties.PREF_SEARCH_QUERY.alias, "") ?: ""
            val action = CollectionsFragmentDirections.actionCollectionSearch(query)
            findNavController().navigate(action)
        }
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
    }
}