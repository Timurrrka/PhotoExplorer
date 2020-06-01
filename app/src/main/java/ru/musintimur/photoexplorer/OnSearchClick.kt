package ru.musintimur.photoexplorer

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