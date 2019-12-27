package ru.musintimur.photoexplorer.data.photo

import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Response
import ru.musintimur.photoexplorer.network.getApiService
import ru.musintimur.photoexplorer.network.getDataAsync
import ru.musintimur.photoexplorer.utils.logD
import ru.musintimur.photoexplorer.utils.logE

private const val TAG = "PhotoDataSource"

class PhotoDataSource(private val scope: CoroutineScope,
                      api_key: String,
                      private val collectionId: Int = 0,
                      private val query: String = "",
                      private val firstPage: Int = 1) :
    PageKeyedDataSource<Int, Photo>() {
    private val apiServices = getApiService(api_key)
    private var page: Int

    init {
        "PhotoDataSource initialized".logD(TAG)
        page = firstPage
    }

    private fun nextPage(): Int {
        page = page.inc()
        return page
    }

    private fun prevPage(): Int {
        return page.dec()
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Photo>) {
        scope.launch {
            try {
                getDataAsync(getApiData(firstPage)).let { data ->
                    "Json data recieved in loadInitial:\nrequestedLoadSize = ${params.requestedLoadSize}\n$data".logD(TAG)
                    callback.onResult(getObjects(data), null, nextPage())
                }
            } catch (e: Exception) {
                e.message?.logE(TAG)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {
        scope.launch {
            try {
                getDataAsync(getApiData(params.key)).let { data ->
                    "Json data recieved in loadAfter:\n$data".logD(TAG)
                    callback.onResult(getObjects(data), nextPage())
                }
            } catch (e: Exception) {
                e.message?.logE(TAG)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {
        scope.launch {
            try {
                getDataAsync(getApiData(params.key)).let { data ->
                    "Json data recieved in loadBefore:\n$data".logD(TAG)
                    callback.onResult(getObjects(data), prevPage())
                }
            } catch (e: Exception) {
                e.message?.logE(TAG)
            }
        }
    }

    override fun invalidate() {
        super.invalidate()
        scope.cancel()
    }

    private suspend fun getApiData(key: Int): Response<String>  {
        return when {
            (collectionId == 0) -> apiServices.queryPhotos(query, key)
            (query.isBlank()) -> apiServices.photoCollection(collectionId, key)
            else -> apiServices.queryPhotos(query, key, collectionId.toString())
        }.also { "getApiData: ${it.body()}".logD(TAG) }
    }

    private fun getObjects(data: String): List<Photo> {
        return when {
            (collectionId == 0) -> getPhotosFromSearchResult(data)
            (query.isBlank()) -> getPhotosFromJson(data)
            else -> getPhotosFromSearchResult(data)
        }.also { "getObjects: $it".logD(TAG) }
    }

}