package ru.musintimur.photoexplorer

import ru.musintimur.photoexplorer.data.Photo

interface OnPhotoClick {
    fun onPhotoClick(photo: Photo)
}