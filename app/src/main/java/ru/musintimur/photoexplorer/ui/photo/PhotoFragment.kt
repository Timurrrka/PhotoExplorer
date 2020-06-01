package ru.musintimur.photoexplorer.ui.photo

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_photo.*
import kotlinx.android.synthetic.main.item_link_full.*
import ru.musintimur.photoexplorer.NetworkCallback
import ru.musintimur.photoexplorer.OnSearchClick
import ru.musintimur.photoexplorer.R
import ru.musintimur.photoexplorer.data.photo.Photo
import ru.musintimur.photoexplorer.data.preferences.Preferences
import ru.musintimur.photoexplorer.data.preferences.Properties

class PhotoFragment : Fragment() {
    private val prefs: SharedPreferences? by lazy { context?.getSharedPreferences(Preferences.PREFERENCES.fileName, Context.MODE_PRIVATE) }
    private val args: PhotoFragmentArgs by navArgs()
    private var photo : Photo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photo = args.argPhoto
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (context as AppCompatActivity).supportActionBar?.title = ""
        photo?.run {
            if (description.isNullOrEmpty() || description=="null") {
                detail_description.visibility = View.GONE
            } else {
                detail_description.text = description
            }
            detail_author.text = author
            detail_dimensions.text = getString(R.string.detail_dimensions, width, height)
            detail_link_full.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url_full)
                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(activity, getString(R.string.about_url_error), Toast.LENGTH_LONG).show()
                }
            }
            Picasso.get()
                .load(url_small)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(detail_photo)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is OnSearchClick) {
            throw RuntimeException(getString(R.string.error_implement, context, "OnSearchClick"))
        }
        if (context !is NetworkCallback) {
            throw RuntimeException(getString(R.string.error_implement, context, "NetworkCallback"))
        }
    }

    override fun onResume() {
        super.onResume()
        (context as OnSearchClick).setOnSearchClick(getString(R.string.search_photos)) {
            val query = prefs?.getString(Properties.PREF_SEARCH_QUERY.alias, "") ?: ""
            val action = PhotoFragmentDirections.actionSearchPhotos(0, query)
            findNavController().navigate(action)
        }
    }
}
