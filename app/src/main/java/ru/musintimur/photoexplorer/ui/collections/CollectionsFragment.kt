package ru.musintimur.photoexplorer.ui.collections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_collections.*
import ru.musintimur.photoexplorer.R
import ru.musintimur.photoexplorer.adapters.CollectionsRecyclerViewAdapter
import ru.musintimur.photoexplorer.data.Collection

class CollectionsFragment : Fragment() {

    private lateinit var collectionsViewModel: CollectionsViewModel
    private val collectionsAdapter = CollectionsRecyclerViewAdapter(mutableSetOf())

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        collectionsViewModel =
                ViewModelProviders.of(this).get(CollectionsViewModel::class.java)
        collectionsViewModel.api_key = getString(R.string.api_key)
        val root = inflater.inflate(R.layout.fragment_collections, container, false)

        collectionsViewModel.collections.observe(this, Observer {
            collectionsAdapter.addCollections(it)
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewCollections.layoutManager = LinearLayoutManager(context)
        recyclerViewCollections.adapter = collectionsAdapter

    }
}