package ru.musintimur.photoexplorer.network

import android.accounts.NetworkErrorException
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.musintimur.photoexplorer.utils.logD
import ru.musintimur.photoexplorer.utils.logE
import kotlin.coroutines.suspendCoroutine

private const val TAG = "NetLib"
private const val API_URL = "https://api.unsplash.com/"

fun retrofitRequest(apiKey: String): Retrofit =
    Retrofit.Builder()
        .client(getHttpClient(apiKey))
        .baseUrl(API_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

fun getHttpClient(apiKey: String) = OkHttpClient().newBuilder()
    .addInterceptor(AccessKeyInterceptor(apiKey))
    .build()

fun getApiService(apiKey: String): ApiServices =
    retrofitRequest(apiKey).create(ApiServices::class.java)

suspend fun getDataAsync(requestFun: (Response<String>)): String {
    return suspendCoroutine { continuation ->
        getData(requestFun,
            object : CoroutineCallback {
                override fun onSuccess(result: String) {
                    "Successfull getDataAsync: $result".logD(TAG)
                    continuation.resumeWith(Result.success(result))
                }

                override fun onError(e: Exception) {
                    "Error in getDataAsync: ${e.message}".logE(TAG)
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
