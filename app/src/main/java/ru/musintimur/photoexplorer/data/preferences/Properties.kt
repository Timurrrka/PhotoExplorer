package ru.musintimur.photoexplorer.data.preferences

//Параметры в созданных файлах настроек
enum class Properties(fileName: Preferences, val alias: String) {
    //Последний поисковой запрос
    PREF_SEARCH_QUERY(Preferences.PREFERENCES, "SEARCH_QUERY"),
    //Последний запрос фотографии дня
    PREF_LAST_DOWNLOAD(Preferences.PREFERENCES, "LAST_DOWNLOAD"),
    //Последняя фотография дня (json)
    PREF_LAST_PHOTO(Preferences.PREFERENCES, "LAST_PHOTO")
}