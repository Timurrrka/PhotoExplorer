package ru.musintimur.photoexplorer.ui.photocollection

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ru.musintimur.photoexplorer.data.photo.Photo
import ru.musintimur.photoexplorer.data.photo.PhotoDataSource
import ru.musintimur.photoexplorer.utils.logD

private const val TAG = "PhotoCollectionViewModel"

class PhotoCollectionViewModel : ViewModel() {

    lateinit var apiKey: String
    var colId: Int = 0
    var query: String = ""
    var page: Int = 1
    private val _album: LiveData<PagedList<Photo>>

    init {
        "PhotoCollectionViewModel initialized!".logD(TAG)
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
                "DataSource.Factory create called".logD(TAG)
                return PhotoDataSource(viewModelScope, apiKey, colId, query, page)
            }
        }
        return LivePagedListBuilder<Int, Photo>(dataSourceFactory, config)
    }

}