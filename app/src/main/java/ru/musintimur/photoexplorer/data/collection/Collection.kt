package ru.musintimur.photoexplorer.data.collection

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import ru.musintimur.photoexplorer.data.photo.Photo
import ru.musintimur.photoexplorer.data.photo.getPhotoFromJson
import ru.musintimur.photoexplorer.utils.logD
import ru.musintimur.photoexplorer.utils.logE

private const val TAG = "Collection"

@Parcelize
data class Collection (val id: Int,
                       val title: String,
                       val coverPhoto: Photo?
) : Parcelable

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
        "${photosArray.length()} photos founded in array".logD(TAG)
        for (i in 0 until photosArray.length()) {
            val jsonCollection = photosArray.getJSONObject(i)
            val newCollection =
                getCollectionFromJson(jsonCollection)
            "$i) $newCollection".logD(TAG)
            collections.add(newCollection)
        }
    } catch (e: JSONException) {
        e.printStackTrace()
        "Error processing Json data: ${e.message}".logE(TAG)
    }

    "Collections from json:\n$collections".logD(TAG)
    return collections
}

fun getCollectionsFromSearchResult(data: String): List<Collection> {
    var collections: List<Collection> = listOf()

    try {
        val jsonObject = JSONObject(data)
        val jsonCollections = jsonObject.getString("results")
        collections = getCollectionsFromJson(jsonCollections)
    } catch (e: JSONException) {
        e.printStackTrace()
        "Error processing Json data: ${e.message}".logE()
    }

    return collections
}