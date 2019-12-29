package ru.musintimur.photoexplorer

import okhttp3.Response

//интерфейс для настройки поисковой панели
interface OnSearchClick {
    fun onSearchClick()
    fun setOnSearchClick(hintText: String, onSearchClick: () -> Unit)
}

//Интерфейс для отображения сетевых ошибок
interface NetworkCallback {
    fun onSuccess()
    fun onError(e: Exception)
}