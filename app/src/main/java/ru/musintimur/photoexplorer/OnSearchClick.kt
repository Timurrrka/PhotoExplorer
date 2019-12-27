package ru.musintimur.photoexplorer

//интерфейс для настройки поисковой панели
interface OnSearchClick {
    fun onSearchClick()
    fun setOnSearchClick(hintText: String, onSearchClick: () -> Unit)
}