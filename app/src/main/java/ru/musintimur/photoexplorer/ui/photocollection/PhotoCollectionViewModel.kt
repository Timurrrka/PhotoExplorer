package ru.musintimur.photoexplorer.ui.photocollection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.musintimur.photoexplorer.data.Collection
import ru.musintimur.photoexplorer.data.Photo
import ru.musintimur.photoexplorer.data.getCollectionsFromJson
import ru.musintimur.photoexplorer.data.getPhotosFromJson
import ru.musintimur.photoexplorer.network.ApiServices
import ru.musintimur.photoexplorer.network.getApiService
import ru.musintimur.photoexplorer.network.getDataAsync
import ru.musintimur.photoexplorer.utils.logD

private const val TAG = "PhotoCollectionViewModel"

class PhotoCollectionViewModel : ViewModel() {

    lateinit var api_key: String
    val apiServices: ApiServices by lazy { getApiService(api_key) }
    var colId: Int = 0
    var page: Int = 1
    var perPage: Int = 10

    private val _album = MutableLiveData<List<Photo>>().apply {
        CoroutineScope(Dispatchers.Main).launch {
            value = loadPhotos()
        }
    }
    var album: LiveData<List<Photo>> = _album

    suspend fun loadPhotos(): List<ru.musintimur.photoexplorer.data.Photo> =
        withContext(Dispatchers.IO) {
            getDataAsync(apiServices.photoCollection(colId, page, perPage)).let { data ->
                "Json data recieved:\n$data".logD(TAG)
                getPhotosFromJson(data)
            }
        }

}