package ru.musintimur.photoexplorer.ui.collections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.musintimur.photoexplorer.data.getCollectionsFromJson
import ru.musintimur.photoexplorer.network.ApiServices
import ru.musintimur.photoexplorer.network.getApiService
import ru.musintimur.photoexplorer.network.getDataAsync
import ru.musintimur.photoexplorer.utils.logD

private const val TAG = "CollectionsViewModel"

class CollectionsViewModel : ViewModel() {

    lateinit var api_key: String
    val apiServices: ApiServices by lazy { getApiService(api_key) }

    private val _collections = MutableLiveData<List<ru.musintimur.photoexplorer.data.Collection>>().apply {
        CoroutineScope(Dispatchers.Main).launch {
            value = loadCollections()
        }
    }
    var collections: LiveData<List<ru.musintimur.photoexplorer.data.Collection>> = _collections

    suspend fun loadCollections(page: Int = 1): List<ru.musintimur.photoexplorer.data.Collection> = withContext(Dispatchers.IO) {
        getDataAsync( apiServices.collectionsPage(page) ).let { data ->
            "Json data recieved:\n$data".logD(TAG)
            getCollectionsFromJson(data)
        }
    }
}