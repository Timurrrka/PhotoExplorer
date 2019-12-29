package ru.musintimur.photoexplorer.network

import android.accounts.NetworkErrorException
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.musintimur.photoexplorer.NetworkCallback
import ru.musintimur.photoexplorer.utils.logE

private const val TAG = "NetworkFactory"
private const val API_URL = "https://api.unsplash.com/"

class NetworkFactory(private val apiKey: String, private val networkCallback: NetworkCallback) {

    val apiServices: ApiServices

    init {
        apiServices = retrofitRequest().create(ApiServices::class.java)
    }

    private fun retrofitRequest(): Retrofit =
        Retrofit.Builder()
            .client(getHttpClient())
            .baseUrl(API_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

    private fun getHttpClient() = OkHttpClient().newBuilder()
        .addInterceptor(AccessKeyInterceptor(apiKey))
        .build()

    fun getDataAsync(
        requestFun: (Response<String>)
    ) : String? =
        try {
            requestFun.run {
                if (isSuccessful)
                    if (body().isNullOrBlank()) {
                        networkCallback.onError(IllegalStateException("Empty response\n${message()}"))
                    } else {
                        networkCallback.onSuccess()
                    }
                else {
                    when (code()) {
                        403 -> networkCallback.onError(LimitExceededException("Error ${code()}: Rate Limit Exceeded"))
                        else -> networkCallback.onError(NetworkErrorException("Error ${code()}: ${message()}\n"))
                    }
                }
                body()
            }
        } catch (e: Exception) {
            "${e.message}".logE(TAG)
            networkCallback.onError(e)
            null
        }
}