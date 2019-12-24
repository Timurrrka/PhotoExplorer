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
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*
import ru.musintimur.photoexplorer.OnPhotoClick
import ru.musintimur.photoexplorer.R
import ru.musintimur.photoexplorer.data.Photo
import java.lang.RuntimeException

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    private val pref_mode = 0
    private val pref_name = "STORED_PAGE"
    private val prefs: SharedPreferences? by lazy { context?.getSharedPreferences(pref_name, pref_mode) }
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java).apply {
                    api_key = getString(R.string.api_key)
                    prefs?.let {
                        lastDownload = it.getLong("lastDownload", 0)
                        lastPhoto = Gson().fromJson(it.getString("lastPhoto", ""), Photo::class.java)
                    }
                    photo.observe(this@HomeFragment, Observer { photo ->
                        text_home.text = getString(R.string.photo_of_the_day, photo.author)
                        Picasso.get()
                            .load(photo.url_small)
                            .placeholder(R.drawable.image_placeholder)
                            .error(R.drawable.image_placeholder)
                            .into(photoOfTheDay)
                        if (photo != Gson().fromJson(prefs?.getString("lastPhotoId", ""), Photo::class.java)) {
                            prefs?.edit {
                                putString("lastPhoto", Gson().toJson(photo, Photo::class.java))
                                putLong("lastDownload", System.currentTimeMillis())
                            }
                        }
                        photoOfTheDay.setOnClickListener {
                            (activity as OnPhotoClick?)?.onPhotoClick(photo)
                        }
                    })
                }


        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is OnPhotoClick) {
            throw RuntimeException("${context.toString()} must implement OnPhotoClick")
        }
    }
}