package ru.musintimur.photoexplorer.ui.photocollection

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ru.musintimur.photoexplorer.data.photo.Photo
import ru.musintimur.photoexplorer.data.photo.PhotoDataSource
import ru.musintimur.photoexplorer.ui.CommonViewModel

class PhotoCollectionViewModel(application: Application,
                               private val collectionId: Int,
                               private val query: String,
                               private val page: Int = 1) : CommonViewModel(application) {

    private val _album: LiveData<PagedList<Photo>>

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(false)
            .build()
        _album = getPagedListBuilder(config).build()
    }

    fun getAlbum(): LiveData<PagedList<Photo>> = _album

    private fun getPagedListBuilder(config: PagedList.Config)
            : LivePagedListBuilder<Int, Photo> {

        val dataSourceFactory = object : DataSource.Factory<Int, Photo>() {
            override fun create(): DataSource<Int, Photo> {
                return PhotoDataSource(viewModelScope, networkFactory, collectionId, query, page)
            }
        }
        return LivePagedListBuilder(dataSourceFactory, config)
    }

}