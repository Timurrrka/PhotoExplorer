package ru.musintimur.photoexplorer.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import ru.musintimur.photoexplorer.utils.logD
import ru.musintimur.photoexplorer.utils.logE

@Parcelize
data class Photo(
    val id: String,
    val description: String?,
    val width: Int,
    val height: Int,
    val url_small: String,
    val url_full: String,
    val author: String
) : Parcelable

fun getPhotoFromJson(jsonPhoto: JSONObject): Photo {
    val id = jsonPhoto.getString("id")
    val description = jsonPhoto.getString("description")
    val width = jsonPhoto.getInt("width")
    val height = jsonPhoto.getInt("height")
    val urls = jsonPhoto.getJSONObject("urls")
    val url_small = urls.getString("small")
    val url_full = urls.getString("full")
    val author = jsonPhoto.getJSONObject("user").getString("name")

    return Photo(id, description, width, height, url_small, url_full, author)
}

fun getPhotosFromJson(data: String): List<Photo> {
    "Json data for parsing:\n$data".logD()
    val photos = mutableListOf<Photo>()

    try {
        val photosArray = JSONArray(data)
        for (i in 0 until photosArray.length()) {
            val jsonPhoto = photosArray.getJSONObject(i)
            val newPhoto = getPhotoFromJson(jsonPhoto)
            photos.add(newPhoto)
        }
    } catch (e: JSONException) {
        e.printStackTrace()
        "Error processing Json data: ${e.message}".logE()
    }

    "Photos from json:\n$photos".logD()
    return photos
}