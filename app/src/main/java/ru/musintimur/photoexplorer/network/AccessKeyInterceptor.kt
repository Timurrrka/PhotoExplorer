package ru.musintimur.photoexplorer.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import kotlin.Exception

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
                return errorResponse(request, IllegalArgumentException("Access Key is empty! Please, provide it in string resources as api_key."))
            }
        }

       return try {
          chain.proceed(request)
       } catch(e: Exception) {
          errorResponse(request, e)
       }
    }

    private fun errorResponse(request: Request, error: Exception): Response {
        val data = "".toResponseBody("application/json; charset=utf-8".toMediaTypeOrNull())
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(503)
            .message(error.message ?: "Connection error")
            .body( data )
            .build()
    }

}