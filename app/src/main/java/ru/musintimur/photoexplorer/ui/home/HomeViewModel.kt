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
import java.util.concurrent.TimeUnit

private const val TAG = "HomeViewModel"

class HomeViewModel : ViewModel() {

    lateinit var api_key: String
    var lastDownload: Long = 0L
    var lastPhoto: Photo? = null
    val apiServices: ApiServices by lazy { getApiService(api_key) }

    private val _photo = MutableLiveData<Photo>().apply {
        CoroutineScope(Dispatchers.Main).launch {
            value = if (lastPhoto == null
                        || lastDownload == 0L
                        || daysGone(lastDownload) > 1)
                        loadRandomPhoto().also {
                            lastPhoto = it
                            lastDownload = System.currentTimeMillis()
                        }
                    else lastPhoto
        }
    }
    var photo: LiveData<Photo> = _photo

    private suspend fun loadRandomPhoto(): Photo = withContext(Dispatchers.IO) {
        getDataAsync( apiServices.randomPhoto() ).let { data ->
            "Json data recieved:\n$data".logD(TAG)
            getPhotosFromJson(data).first()
        }
    }

    private fun daysGone(lastTime: Long) : Long {
        val d1 = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())
        val d2 = TimeUnit.MILLISECONDS.toDays(lastTime)
        "Today: $d1".logD(TAG)
        "Old day: $d2".logD(TAG)
        val days = d1 - d2
        "Days gone: $days".logD(TAG)
        return days
    }

}