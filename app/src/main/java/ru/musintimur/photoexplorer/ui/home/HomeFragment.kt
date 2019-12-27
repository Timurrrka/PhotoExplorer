package ru.musintimur.photoexplorer.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*
import ru.musintimur.photoexplorer.OnSearchClick
import ru.musintimur.photoexplorer.R
import ru.musintimur.photoexplorer.data.photo.Photo
import ru.musintimur.photoexplorer.data.preferences.Preferences
import ru.musintimur.photoexplorer.data.preferences.Properties
import ru.musintimur.photoexplorer.utils.logD

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    private val prefs: SharedPreferences? by lazy { context?.getSharedPreferences(Preferences.PREFERENCES.fileName, Context.MODE_PRIVATE) }
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java).apply {
                    apiKey = getString(R.string.api_key)
                    prefs?.let {
                        lastDownload = it.getLong(Properties.PREF_LAST_DOWNLOAD.alias, 0)
                        lastPhoto = Gson().fromJson(it.getString(Properties.PREF_LAST_PHOTO.alias, ""), Photo::class.java)
                    }
                    photo.observe(this@HomeFragment, Observer { photo ->
                        text_home.text = getString(R.string.photo_of_the_day, photo.author)
                        Picasso.get()
                            .load(photo.url_small)
                            .placeholder(R.drawable.image_placeholder)
                            .error(R.drawable.image_placeholder)
                            .into(photoOfTheDay)
                        if (photo != Gson().fromJson(prefs?.getString(Properties.PREF_LAST_PHOTO.alias, ""), Photo::class.java)) {
                            prefs?.edit {
                                putString(Properties.PREF_LAST_PHOTO.alias, Gson().toJson(photo, Photo::class.java))
                                putLong(Properties.PREF_LAST_DOWNLOAD.alias, System.currentTimeMillis())
                            }
                        }
                        photoOfTheDay.setOnClickListener {
                            val action = HomeFragmentDirections.actionHomeToPhoto(photo)
                            findNavController().navigate(action)
                        }
                    })
                }
        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is OnSearchClick) {
            throw RuntimeException("$context must implement OnPhotoClick")
        }
        "onAttach: setting onSearchClick".logD(TAG)
        context.setOnSearchClick {
            val query = prefs?.getString(Properties.PREF_SEARCH_QUERY.alias, "") ?: ""
            "in setOnSearchClick: query=$query".logD(TAG)
            val action = HomeFragmentDirections.actionHomeSearchPhotos(0, query)
            findNavController().navigate(action)
        }
    }
}