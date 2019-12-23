package ru.musintimur.photoexplorer.network

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Interceptor
import okhttp3.Response
import ru.musintimur.photoexplorer.R
import ru.musintimur.photoexplorer.utils.logE

private const val TAG = "AccessKeyInterceptor"

class AccessKeyInterceptor(private val api_key: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if (request.header("No-Authentication") == null) {

            if (!api_key.isEmpty()) {
                val finalToken = "Client-ID $api_key"
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