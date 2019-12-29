package ru.musintimur.photoexplorer.network

import java.io.IOException
import java.lang.Exception
import java.net.ConnectException
import java.net.MalformedURLException

fun checkExceptionType(e: Exception, className: String): String {
    return when(e) {
        is MalformedURLException -> {
            "MalformedURLException in $className:\n\n${e.message}\n\nInvalid URL?"
        }
        is ConnectException -> {
            "ConnectException in $className: \n\n${e.message}\n\nCheck internet connection."
        }
        is IOException -> {
            "IOException reading data in $className:\n\n${e.message}"
        }
        is SecurityException -> {
            "Security exception in $className:\n\n${e.message}\n\nNeeds permission?"
        }
        else -> {
            "Unknown network exception in $className:\n\n${e.message}"
        }
    }
}