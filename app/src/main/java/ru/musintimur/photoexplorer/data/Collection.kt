package ru.musintimur.photoexplorer.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import ru.musintimur.photoexplorer.utils.logD
import ru.musintimur.photoexplorer.utils.logE

@Parcelize
data class Collection (val id: Int,
                       val title: String,
                       val coverPhoto: Photo) : Parcelable

fun getCollectionFromJson(jsonCollection: JSONObject): Collection {
    val id = jsonCollection.getInt("id")
    val title = jsonCollection.getString("title")
    val jsonPhoto = jsonCollection.getJSONObject("cover_photo")
    val coverPhoto = getPhotoFromJson(jsonPhoto)

    return Collection(id, title, coverPhoto)
}

fun getCollectionsFromJson(data: String): List<Collection> {
    "Json data for parsing:\n$data".logD()
    val collections = mutableListOf<Collection>()

    try {
        val photosArray = JSONArray(data)
        for (i in 0 until photosArray.length()) {
            val jsonCollection = photosArray.getJSONObject(i)
            val newCollection = getCollectionFromJson(jsonCollection)
            collections.add(newCollection)
        }
    } catch (e: JSONException) {
        e.printStackTrace()
        "Error processing Json data: ${e.message}".logE()
    }

    "Photos from json:\n$collections".logD()
    return collections
}