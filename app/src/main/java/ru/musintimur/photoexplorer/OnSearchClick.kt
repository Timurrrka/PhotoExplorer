package ru.musintimur.photoexplorer

//интерфейс для настройки поисковой панели
interface OnSearchClick {
    fun onSearchClick()
    fun setOnSearchClick(onSearchClick: () -> Unit)
}