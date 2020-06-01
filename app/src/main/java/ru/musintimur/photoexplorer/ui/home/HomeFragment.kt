package ru.musintimur.photoexplorer.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.*
import ru.musintimur.photoexplorer.NetworkCallback
import ru.musintimur.photoexplorer.OnSearchClick
import ru.musintimur.photoexplorer.R
import ru.musintimur.photoexplorer.data.preferences.Preferences
import ru.musintimur.photoexplorer.data.preferences.Properties
import ru.musintimur.photoexplorer.network.EmptyResultException
import ru.musintimur.photoexplorer.utils.logD
import ru.musintimur.photoexplorer.utils.logE

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {

    private val prefs: SharedPreferences? by lazy {
        context?.getSharedPreferences(
            Preferences.PREFERENCES.fileName,
            Context.MODE_PRIVATE
        )
    }
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mainActivity: NetworkCallback
    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        "onCreateView called".logD(TAG)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        homeViewModel.run {
            getPhoto().observe(viewLifecycleOwner, Observer { photo ->
                photo?.let {
                    mainActivity.onSuccess()
                    text_home.text = getString(R.string.photo_of_the_day, photo.author)
                    Picasso.get()
                        .load(photo.url_small)
                        .placeholder(R.drawable.image_placeholder)
                        .error(R.drawable.image_placeholder)
                        .into(photoOfTheDay)
                    photoOfTheDay.setOnClickListener {
                        val action = HomeFragmentDirections.actionHomeToPhoto(photo)
                        findNavController().navigate(action)
                    }
                } ?: mainActivity.onError(EmptyResultException(getString(R.string.empty_result)))
            })

            getError().observe(viewLifecycleOwner, Observer {
                it?.let {
                    "error.observe've got value: ${it.message}".logE(TAG)
                    mainActivity.onError(it)
                } ?: mainActivity.onSuccess()
            })
        }

        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        "onAttach called".logD(TAG)
        if (context !is OnSearchClick) {
            throw RuntimeException(getString(R.string.error_implement, context, "OnSearchClick"))
        }
        if (context !is NetworkCallback) {
            throw RuntimeException(getString(R.string.error_implement, context, "NetworkCallback"))
        } else {
            mainActivity = context
        }
    }

    override fun onResume() {
        super.onResume()
        "onResume called".logD(TAG)
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
        "onPause called".logD(TAG)
        job?.cancel()
    }

}