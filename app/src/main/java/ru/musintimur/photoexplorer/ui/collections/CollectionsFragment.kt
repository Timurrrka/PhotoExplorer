package ru.musintimur.photoexplorer.ui.collections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_collections.*
import ru.musintimur.photoexplorer.R
import ru.musintimur.photoexplorer.adapters.CollectionsRecyclerViewAdapter
import ru.musintimur.photoexplorer.data.Collection
import ru.musintimur.photoexplorer.ui.home.HomeFragmentDirections
import ru.musintimur.photoexplorer.utils.logD

private const val TAG = "CollectionsFragment"

class CollectionsFragment : Fragment() {

    private lateinit var collectionsViewModel: CollectionsViewModel
    private val collectionsAdapter = CollectionsRecyclerViewAdapter()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        "onCreateView called".logD(TAG)
        collectionsViewModel =
                ViewModelProviders.of(this).get(CollectionsViewModel::class.java)
        collectionsViewModel.api_key = getString(R.string.api_key)

        return inflater.inflate(R.layout.fragment_collections, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        "onViewCreated called".logD(TAG)
        super.onViewCreated(view, savedInstanceState)
        recyclerViewCollections.layoutManager = LinearLayoutManager(context)
        recyclerViewCollections.adapter = collectionsAdapter
        collectionsAdapter.onItemClick = { collection ->
            val action = CollectionsFragmentDirections.actionCollectionToPhotos(collection)
            findNavController().navigate(action)
        }
        collectionsViewModel.getCollections().observe(this, Observer<PagedList<Collection>> {
            "Observer've got data\n$it".logD(TAG)
            collectionsAdapter.submitList(it)
        })
    }
}