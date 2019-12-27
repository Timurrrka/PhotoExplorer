package ru.musintimur.photoexplorer.data.collection

import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Response
import ru.musintimur.photoexplorer.data.photo.Photo
import ru.musintimur.photoexplorer.data.photo.getPhotosFromJson
import ru.musintimur.photoexplorer.data.photo.getPhotosFromSearchResult
import ru.musintimur.photoexplorer.network.getApiService
import ru.musintimur.photoexplorer.network.getDataAsync
import ru.musintimur.photoexplorer.utils.logD

private const val TAG = "CollectionDataSource"

class CollectionDataSource(
    private val scope: CoroutineScope,
    api_key: String,
    private val query: String = "",
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
            getDataAsync(getApiData(firstPage)).let { data ->
                "Json data recieved in loadInitial:\n$data".logD(TAG)
                callback.onResult(getObjects(data), null, nextPage())
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Collection>) {
        scope.launch {
            getDataAsync(getApiData(params.key)).let { data ->
                "Json data recieved in loadAfter:\n$data".logD(TAG)
                callback.onResult(getObjects(data), nextPage())
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Collection>) {
        scope.launch {
            getDataAsync(getApiData(params.key)).let { data ->
                "Json data recieved in loadBefore:\n$data".logD(TAG)
                callback.onResult(getObjects(data), prevPage())
            }
        }
    }

    override fun invalidate() {
        super.invalidate()
        scope.cancel()
    }

    private suspend fun getApiData(key: Int): Response<String> {
        return when {
            (query.isBlank()) -> apiServices.collectionsPage(key)
            else -> apiServices.queryCollections(query, key)
        }.also { "getApiData: ${it.body()}".logD(TAG) }
    }

    private fun getObjects(data: String): List<Collection> {
        return when {
            (query.isBlank()) -> getCollectionsFromJson(data)
            else -> getCollectionsFromSearchResult(data)
        }.also { "getObjects: $it".logD(TAG) }
    }
}