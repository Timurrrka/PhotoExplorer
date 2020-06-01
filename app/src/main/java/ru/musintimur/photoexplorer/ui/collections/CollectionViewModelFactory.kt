package ru.musintimur.photoexplorer.ui.collections

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CollectionViewModelFactory(private val app: Application,
                                 private val query: String) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        CollectionsViewModel(app, query) as T
}