package ru.musintimur.photoexplorer.data.collection

import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ru.musintimur.photoexplorer.network.getApiService
import ru.musintimur.photoexplorer.network.getDataAsync
import ru.musintimur.photoexplorer.utils.logD

private const val TAG = "CollectionDataSource"

class CollectionDataSource(
    private val scope: CoroutineScope,
    api_key: String,
    private val firstPage: Int = 1
) : PageKeyedDataSource<Int, Collection>() {
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
            getDataAsync(apiServices.collectionsPage(firstPage)).let { data ->
                "Json data recieved in loadInitial:\n$data".logD(TAG)
                callback.onResult(getCollectionsFromJson(data), null, nextPage())
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Collection>) {
        scope.launch {
            getDataAsync(apiServices.collectionsPage(params.key)).let { data ->
                "Json data recieved in loadAfter:\n$data".logD(TAG)
                callback.onResult(getCollectionsFromJson(data), nextPage())
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Collection>) {
        scope.launch {
            getDataAsync(apiServices.collectionsPage(params.key)).let { data ->
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