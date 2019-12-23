package ru.musintimur.photoexplorer.network

import android.accounts.NetworkErrorException
import kotlinx.coroutines.coroutineScope
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import kotlin.coroutines.suspendCoroutine

private const val API_URL = "https://api.unsplash.com/"

fun retrofitRequest(api_key: String): Retrofit =
    Retrofit.Builder()
        .client(getHttpClient(api_key))
        .baseUrl(API_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

fun getHttpClient(api_key: String) = OkHttpClient().newBuilder()
    .addInterceptor(AccessKeyInterceptor(api_key))
    .build()

fun getApiService(api_key: String): ApiServices =
    retrofitRequest(api_key).create(ApiServices::class.java)

suspend fun getDataAsync(requestFun: (Response<String>)): String {
    return suspendCoroutine { continuation ->
        getData(requestFun,
            object : CoroutineCallback {
                override fun onSuccess(result: String) {
                    continuation.resumeWith(Result.success(result))
                }

                override fun onError(e: Exception) {
                    continuation.resumeWith(Result.failure(e))
                }
            })
    }
}

fun getData(
    requestFun: (Response<String>),
    coroutineCallback: CoroutineCallback
) =
    try {
        requestFun.run {
            if (isSuccessful)
                body()?.let { coroutineCallback.onSuccess(it) } ?: throw IllegalStateException("Got empty body from api")
            else throw NetworkErrorException("Did't connect to api")
        }
    } catch (e: Exception) {
        coroutineCallback.onError(e)
    }