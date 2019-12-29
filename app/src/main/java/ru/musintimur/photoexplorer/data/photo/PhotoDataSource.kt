package ru.musintimur.photoexplorer.data.photo

import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Response
import ru.musintimur.photoexplorer.network.NetworkFactory
import ru.musintimur.photoexplorer.utils.logD
import ru.musintimur.photoexplorer.utils.logE

private const val TAG = "PhotoDataSource"

class PhotoDataSource(private val scope: CoroutineScope,
                      private val networkFactory: NetworkFactory,
                      private val collectionId: Int = 0,
                      private val query: String = "",
                      private val firstPage: Int = 1) :
    PageKeyedDataSource<Int, Photo>() {
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
                networkFactory.getDataAsync(getApiData(firstPage))?.let { data ->
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
                networkFactory.getDataAsync(getApiData(params.key))?.let { data ->
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
                networkFactory.getDataAsync(getApiData(params.key))?.let { data ->
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

    private suspend fun getApiData(key: Int): Response<String> {
        return networkFactory.run {
            when {
                (collectionId == 0) -> apiServices.queryPhotos(query, key)
                (query.isBlank()) -> apiServices.photoCollection(collectionId, key)
                else -> apiServices.queryPhotos(query, key, collectionId.toString())
            }
        }
    }

    private fun getObjects(data: String): List<Photo> {
        return when {
            (collectionId == 0) -> getPhotosFromSearchResult(data)
            (query.isBlank()) -> getPhotosFromJson(data)
            else -> getPhotosFromSearchResult(data)
        }
    }

}