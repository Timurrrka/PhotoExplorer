package ru.musintimur.photoexplorer.network

import okhttp3.Interceptor
import okhttp3.Response

class AccessKeyInterceptor(private val apiKey: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if (request.header("No-Authentication") == null) {

            if (apiKey.isNotEmpty()) {
                val finalToken = "Client-ID $apiKey"
                request = request.newBuilder()
                    .addHeader("Authorization", finalToken)
                    .build()
            } else {
                throw IllegalArgumentException("Access Key is empty! Please, provide it in string resources as api_key.")
            }
        }

        return chain.proceed(request)
    }

}