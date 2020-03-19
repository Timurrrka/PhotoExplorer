package ru.musintimur.photoexplorer.ui

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.musintimur.photoexplorer.App
import ru.musintimur.photoexplorer.NetworkCallback
import ru.musintimur.photoexplorer.R
import ru.musintimur.photoexplorer.network.NetworkFactory

abstract class CommonViewModel(application: Application) : AndroidViewModel(application)
    , NetworkCallback
{

    protected val preferences: SharedPreferences by lazy { getApplication<App>().preferences }
    private val resources: Resources by lazy { getApplication<App>().resources }
    private val apiKey: String by lazy { resources.getString(R.string.api_key) }
    protected val networkFactory: NetworkFactory by lazy { NetworkFactory(apiKey, this) }

    private val _error = MutableLiveData<Exception?>()

    fun getError(): LiveData<Exception?> = _error

    override fun onSuccess() {
        _error.postValue(null)
    }

    override fun onError(e: Exception) {
        _error.postValue(e)
    }
}