package ru.musintimur.photoexplorer.network

interface CoroutineCallback {
    fun onSuccess(result: String)
    fun onError(e: Exception)
}