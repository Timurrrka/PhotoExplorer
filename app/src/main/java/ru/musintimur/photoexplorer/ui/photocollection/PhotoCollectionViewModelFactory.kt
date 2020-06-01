package ru.musintimur.photoexplorer.ui.photocollection

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PhotoCollectionViewModelFactory(
    private val app: Application,
    private val collectionId: Int,
    private val query: String,
    private val startPage: Int = 1
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        PhotoCollectionViewModel(app, collectionId, query, startPage) as T
}