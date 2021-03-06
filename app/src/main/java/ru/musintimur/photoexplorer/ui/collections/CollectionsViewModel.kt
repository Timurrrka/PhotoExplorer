package ru.musintimur.photoexplorer.ui.collections

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ru.musintimur.photoexplorer.data.collection.Collection
import ru.musintimur.photoexplorer.data.collection.CollectionDataSource
import ru.musintimur.photoexplorer.ui.CommonViewModel

class CollectionsViewModel(application: Application, private val query: String) : CommonViewModel(application) {

    private val _collection: LiveData<PagedList<Collection>>

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(false)
            .build()
        _collection = getPagedListBuilder(config).build()
    }

    fun getCollections(): LiveData<PagedList<Collection>> = _collection

    private fun getPagedListBuilder(config: PagedList.Config)
        : LivePagedListBuilder<Int, Collection> {

        val dataSourceFactory = object : DataSource.Factory<Int, Collection>() {
            override fun create(): DataSource<Int, Collection> {
                return CollectionDataSource(viewModelScope, networkFactory, query)
            }
        }
        return LivePagedListBuilder(dataSourceFactory, config)
    }
}