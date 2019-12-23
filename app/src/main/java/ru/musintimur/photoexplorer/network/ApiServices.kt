package ru.musintimur.photoexplorer.network


import retrofit2.Response
import retrofit2.http.*

private const val HEADER_CONTENT_TYPE = "Content-Type: application/json; charset=utf-8"
//private const val HEADER_CHARSET = ""
private const val HEADER_ACCEPT_VERSION = "Accept-Version: v1"

interface ApiServices {

    @Headers(HEADER_CONTENT_TYPE, HEADER_ACCEPT_VERSION)
    //@GET("photos/random?featured={featured}&count={count}")
    @GET("photos/random")
    suspend fun randomPhoto(
        @Query("featured") feautured: Boolean = true,
        @Query("count") count: Int = 1
    ): Response<String>

}