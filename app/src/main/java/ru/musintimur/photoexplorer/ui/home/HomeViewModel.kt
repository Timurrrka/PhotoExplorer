package ru.musintimur.photoexplorer.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.musintimur.photoexplorer.data.photo.Photo
import ru.musintimur.photoexplorer.data.photo.getPhotosFromJson
import ru.musintimur.photoexplorer.network.NetworkFactory
import java.util.concurrent.TimeUnit

class HomeViewModel : ViewModel() {

    lateinit var apiKey: String
    var lastDownload: Long = 0L
    var lastPhoto: Photo? = null
    lateinit var networkFactory: NetworkFactory

    private val _photo = MutableLiveData<Photo>().apply {
        CoroutineScope(Dispatchers.Main).launch {
            setPhoto()
        }
    }
    var photo: LiveData<Photo>? = _photo

    suspend fun setPhoto(): Unit = withContext(Dispatchers.Main) {
        _photo.value = if (lastPhoto == null
            || lastDownload == 0L
            || daysGone(lastDownload) > 1
        )
            loadRandomPhoto()?.also {
                lastPhoto = it
                lastDownload = System.currentTimeMillis()
            }
        else lastPhoto
    }

    private suspend fun loadRandomPhoto(): Photo? = withContext(Dispatchers.IO) {
        networkFactory.run {
            getDataAsync(apiServices.randomPhoto())?.let { data ->
                getPhotosFromJson(data).firstOrNull()
            }
        }
    }

    private fun daysGone(lastTime: Long): Long =
        TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()) -
                TimeUnit.MILLISECONDS.toDays(lastTime)

}