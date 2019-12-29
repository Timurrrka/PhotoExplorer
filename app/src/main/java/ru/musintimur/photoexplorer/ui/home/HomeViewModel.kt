package ru.musintimur.photoexplorer.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.musintimur.photoexplorer.data.photo.Photo
import ru.musintimur.photoexplorer.data.photo.getPhotosFromJson
import ru.musintimur.photoexplorer.network.NetworkFactory
import ru.musintimur.photoexplorer.utils.logD
import java.util.concurrent.TimeUnit

private const val TAG = "HomeViewModel"

class HomeViewModel : ViewModel() {

    lateinit var apiKey: String
    var lastDownload: Long = 0L
    var lastPhoto: Photo? = null
    lateinit var networkFactory: NetworkFactory

    private val _photo = MutableLiveData<Photo>().apply {
        CoroutineScope(Dispatchers.Main).launch {
            value = if (lastPhoto == null
                        || lastDownload == 0L
                        || daysGone(lastDownload) > 1)
                        loadRandomPhoto()?.also {
                            lastPhoto = it
                            lastDownload = System.currentTimeMillis()
                        }
                    else lastPhoto
        }
    }
    var photo: LiveData<Photo>? = _photo

    private suspend fun loadRandomPhoto(): Photo? = withContext(Dispatchers.IO) {
        networkFactory.run {
            getDataAsync( apiServices.randomPhoto() )?.let { data ->
                "Json data recieved:\n$data".logD(TAG)
                getPhotosFromJson(data).firstOrNull()
            }
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