package ru.musintimur.photoexplorer

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import ru.musintimur.photoexplorer.data.preferences.Preferences

class App : Application() {

    val preferences: SharedPreferences by lazy {
        getSharedPreferences(
            Preferences.PREFERENCES.fileName,
            Context.MODE_PRIVATE
        )
    }
}