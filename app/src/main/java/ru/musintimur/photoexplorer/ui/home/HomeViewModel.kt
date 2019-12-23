package ru.musintimur.photoexplorer.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.musintimur.photoexplorer.data.Photo
import ru.musintimur.photoexplorer.data.getPhotosFromJson
import ru.musintimur.photoexplorer.network.ApiServices
import ru.musintimur.photoexplorer.network.getApiService
import ru.musintimur.photoexplorer.network.getDataAsync
import ru.musintimur.photoexplorer.utils.logD

private const val TAG = "HomeViewModel"

class HomeViewModel : ViewModel() {

    lateinit var api_key: String
    val apiServices: ApiServices by lazy { getApiService(api_key) }

    private val _photo = MutableLiveData<Photo>().apply {
        CoroutineScope(Dispatchers.Main).launch {
            value = loadPhoto()
        }
    }
    var photo: LiveData<Photo> = _photo

    suspend fun loadPhoto(): Photo = withContext(Dispatchers.IO) {
            getDataAsync( apiServices.randomPhoto() ).let { data ->
                "Json data recieved:\n$data".logD(TAG)
                getPhotosFromJson(data).first()
            }
    }

}