package ru.musintimur.photoexplorer.data.collection

import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Response
import ru.musintimur.photoexplorer.network.NetworkFactory
import ru.musintimur.photoexplorer.utils.logD
import ru.musintimur.photoexplorer.utils.logE

private const val TAG = "CollectionDataSource"

class CollectionDataSource(
    private val scope: CoroutineScope,
    private val networkFactory: NetworkFactory,
    private val query: String = "",
    private val firstPage: Int = 1
) : PageKeyedDataSource<Int, Collection>() {
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
            try {
                networkFactory.getDataAsync(getApiData(firstPage))?.let { data ->
                    callback.onResult(getObjects(data), null, nextPage())
                }
            } catch (e: Exception) {
                e.message?.logE(TAG)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Collection>) {
        scope.launch {
            networkFactory.getDataAsync(getApiData(params.key))?.let { data ->
                callback.onResult(getObjects(data), nextPage())
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Collection>) {
        scope.launch {
            networkFactory.getDataAsync(getApiData(params.key))?.let { data ->
                callback.onResult(getObjects(data), prevPage())
            }
        }
    }

    override fun invalidate() {
        super.invalidate()
        scope.cancel()
    }

    private suspend fun getApiData(key: Int): Response<String> {
        return networkFactory.run {
            when {
                (query.isBlank()) -> apiServices.collectionsPage(key)
                else -> apiServices.queryCollections(query, key)
            }
        }
    }

        private fun getObjects(data: String): List<Collection> {
            return when {
                (query.isBlank()) -> getCollectionsFromJson(data)
                else -> getCollectionsFromSearchResult(data)
            }
        }
    }