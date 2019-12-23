package ru.musintimur.photoexplorer.utils

import android.util.Log

const val DEBUGLOG = "DEBUGLOG"
const val ERRORLOG = "ERRORLOG"

fun String.logD(header: String = DEBUGLOG) {
    Log.d(header, this)
}

fun String.logE(header: String = ERRORLOG) {
    Log.e(header, this)
}