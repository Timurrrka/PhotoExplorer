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
import kotlinx.coroutines.*
import ru.musintimur.photoexplorer.NetworkCallback
import ru.musintimur.photoexplorer.OnSearchClick
import ru.musintimur.photoexplorer.R
import ru.musintimur.photoexplorer.data.photo.Photo
import ru.musintimur.photoexplorer.data.preferences.Preferences
import ru.musintimur.photoexplorer.data.preferences.Properties
import ru.musintimur.photoexplorer.network.EmptyResultException
import ru.musintimur.photoexplorer.network.NetworkFactory

class HomeFragment : Fragment() {

    private val prefs: SharedPreferences? by lazy {
        context?.getSharedPreferences(
            Preferences.PREFERENCES.fileName,
            Context.MODE_PRIVATE
        )
    }
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var networkCallback: NetworkCallback
    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java).apply {
                apiKey = getString(R.string.api_key)
                networkFactory = NetworkFactory(getString(R.string.api_key), (context as NetworkCallback))
                prefs?.let {
                    lastDownload = it.getLong(Properties.PREF_LAST_DOWNLOAD.alias, 0)
                    lastPhoto = Gson().fromJson(it.getString(Properties.PREF_LAST_PHOTO.alias, ""), Photo::class.java)
                }
                photo?.observe(this@HomeFragment, Observer { photo ->
                    photo?.let {
                        networkCallback.onSuccess()
                        text_home.text = getString(R.string.photo_of_the_day, photo.author)
                        Picasso.get()
                            .load(photo.url_small)
                            .placeholder(R.drawable.image_placeholder)
                            .error(R.drawable.image_placeholder)
                            .into(photoOfTheDay)
                        if (photo != Gson().fromJson(
                                prefs?.getString(Properties.PREF_LAST_PHOTO.alias, ""),
                                Photo::class.java
                            )
                        ) {
                            prefs?.edit {
                                putString(Properties.PREF_LAST_PHOTO.alias, Gson().toJson(photo, Photo::class.java))
                                putLong(Properties.PREF_LAST_DOWNLOAD.alias, System.currentTimeMillis())
                            }
                        }
                        photoOfTheDay.setOnClickListener {
                            val action = HomeFragmentDirections.actionHomeToPhoto(photo)
                            findNavController().navigate(action)
                        }
                    } ?: networkCallback.onError(EmptyResultException(getString(R.string.empty_result)))
                })
            }
        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is OnSearchClick) {
            throw RuntimeException(getString(R.string.error_implement, context, "OnSearchClick"))
        }
        if (context !is NetworkCallback) {
            throw RuntimeException(getString(R.string.error_implement, context, "NetworkCallback"))
        } else {
            networkCallback = context
        }
    }

    override fun onResume() {
        super.onResume()
        (context as OnSearchClick).setOnSearchClick(getString(R.string.search_photos)) {
            val query = prefs?.getString(Properties.PREF_SEARCH_QUERY.alias, "") ?: ""
            val action = HomeFragmentDirections.actionHomeSearchPhotos(0, query)
            findNavController().navigate(action)
        }
        job = CoroutineScope(Dispatchers.Default).launch {
            homeViewModel.setPhoto()
        }
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
    }

}