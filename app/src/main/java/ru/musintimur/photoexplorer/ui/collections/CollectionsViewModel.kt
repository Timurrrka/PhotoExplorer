package ru.musintimur.photoexplorer.ui.collections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import kotlinx.coroutines.*
import ru.musintimur.photoexplorer.data.Collection
import ru.musintimur.photoexplorer.data.getCollectionsFromJson
import ru.musintimur.photoexplorer.network.ApiServices
import ru.musintimur.photoexplorer.network.getApiService
import ru.musintimur.photoexplorer.network.getDataAsync
import ru.musintimur.photoexplorer.utils.logD

private const val TAG = "CollectionsViewModel"

class CollectionsViewModel : ViewModel() {

    lateinit var api_key: String

    private val _collection: LiveData<PagedList<Collection>>

    init {
        "CollectionsViewModel initialized!".logD(TAG)
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
                "DataSource.Factory create called".logD(TAG)
                return CollectionDataSource(api_key, viewModelScope)
            }
        }
        return LivePagedListBuilder<Int, Collection>(dataSourceFactory, config)
    }

    class CollectionDataSource(private val api_key: String, private val scope: CoroutineScope, private val firstPage: Int = 1)
        : PageKeyedDataSource<Int, Collection>() {
        private val apiServices = getApiService(api_key)
        private var page: Int

        init {
            "CollectionDataSource initialized".logD(TAG)
            page = firstPage
        }

        private fun nextPage(): Int {
            page = page.inc()
            return page
        }

        private fun prevPage(): Int {
            return page.dec()
        }

        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Collection>) {
            scope.launch {
                getDataAsync( apiServices.collectionsPage(firstPage) ).let { data ->
                    "Json data recieved in loadInitial:\nrequestedLoadSize = ${params.requestedLoadSize}\n$data".logD(TAG)
                    callback.onResult(getCollectionsFromJson(data), null, nextPage())
                }
            }
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Collection>) {
            scope.launch {
                getDataAsync( apiServices.collectionsPage(params.key, params.requestedLoadSize) ).let { data ->
                    "Json data recieved in loadAfter:\n$data".logD(TAG)
                    callback.onResult(getCollectionsFromJson(data), nextPage())
                }
            }
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Collection>) {
            scope.launch {
                getDataAsync( apiServices.collectionsPage(params.key, params.requestedLoadSize) ).let { data ->
                    "Json data recieved in loadBefore:\n$data".logD(TAG)
                    callback.onResult(getCollectionsFromJson(data), prevPage())
                }
            }
        }

        override fun invalidate() {
            super.invalidate()
            scope.cancel()
        }
    }
}