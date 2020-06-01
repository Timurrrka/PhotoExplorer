package ru.musintimur.photoexplorer.network

import retrofit2.Response
import retrofit2.http.*

private const val HEADER_CONTENT_TYPE = "Content-Type: application/json; charset=utf-8"
private const val HEADER_ACCEPT_VERSION = "Accept-Version: v1"

interface ApiServices {

    @Headers(HEADER_CONTENT_TYPE, HEADER_ACCEPT_VERSION)
    @GET("photos/random")
    suspend fun randomPhoto(
        @Query("featured") featured: Boolean = true,
        @Query("count") count: Int = 1
    ): Response<String>

    @Headers(HEADER_CONTENT_TYPE, HEADER_ACCEPT_VERSION)
    @GET("photos/{id}")
    suspend fun getPhoto(
        @Path("id") id: String
    ): Response<String>

    @Headers(HEADER_CONTENT_TYPE, HEADER_ACCEPT_VERSION)
    @GET("collections")
    suspend fun collectionsPage(
        @Query("page") page: Int = 1,
        @Query("per_page") per_page: Int = 10
    ): Response<String>

    @Headers(HEADER_CONTENT_TYPE, HEADER_ACCEPT_VERSION)
    @GET("/collections/{id}/photos")
    suspend fun photoCollection(
        @Path("id") id: Int,
        @Query("page") page: Int = 1,
        @Query("per_page") per_page: Int = 10
    ): Response<String>

    @Headers(HEADER_CONTENT_TYPE, HEADER_ACCEPT_VERSION)
    @GET("/search/photos")
    suspend fun queryPhotos(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("collections") collections: String = "",
        @Query("per_page") per_page: Int = 10

    ): Response<String>

    @Headers(HEADER_CONTENT_TYPE, HEADER_ACCEPT_VERSION)
    @GET("/search/collections")
    suspend fun queryCollections(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") per_page: Int = 10

    ): Response<String>

}