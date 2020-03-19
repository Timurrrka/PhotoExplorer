package ru.musintimur.photoexplorer.ui.home

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kotlinx.coroutines.*
import ru.musintimur.photoexplorer.data.photo.Photo
import ru.musintimur.photoexplorer.data.photo.getPhotosFromJson
import ru.musintimur.photoexplorer.data.preferences.Properties
import ru.musintimur.photoexplorer.ui.CommonViewModel
import java.util.concurrent.TimeUnit

class HomeViewModel(application: Application) : CommonViewModel(application) {

    private var lastDownload: Long = preferences.getLong(Properties.PREF_LAST_DOWNLOAD.alias, 0L)
    private var lastPhoto: Photo? = Gson().fromJson(preferences.getString(Properties.PREF_LAST_PHOTO.alias, ""), Photo::class.java)

    private val _photo = MutableLiveData<Photo>().apply {
        CoroutineScope(Dispatchers.Main).launch {
            setPhoto()
        }
    }

    fun getPhoto(): LiveData<Photo> = _photo

    suspend fun setPhoto(): Unit = withContext(Dispatchers.Main) {
        _photo.value = if (lastPhoto == null
            || lastDownload == 0L
            || daysGone(lastDownload) > 0
        )
            loadRandomPhoto()?.also {
                updatePreferences(it)
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

    private fun updatePreferences(photo: Photo) {
        if (photo != Gson().fromJson(
                preferences.getString(Properties.PREF_LAST_PHOTO.alias, ""),
                Photo::class.java
            )
        ) {
            preferences.edit {
                putString(Properties.PREF_LAST_PHOTO.alias, Gson().toJson(photo, Photo::class.java))
                putLong(Properties.PREF_LAST_DOWNLOAD.alias, System.currentTimeMillis())
            }
        }
    }

    private fun daysGone(lastTime: Long): Long =
        TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()) -
        TimeUnit.MILLISECONDS.toDays(lastTime)

}