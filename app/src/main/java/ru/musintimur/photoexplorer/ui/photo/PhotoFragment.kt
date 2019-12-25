package ru.musintimur.photoexplorer.ui.photo

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_photo.*
import kotlinx.android.synthetic.main.item_link_full.*
import ru.musintimur.photoexplorer.MainActivity
import ru.musintimur.photoexplorer.R
import ru.musintimur.photoexplorer.data.Photo
import ru.musintimur.photoexplorer.utils.logD

private const val TAG = "PhotoFragment"
//private const val ARG_PARAM = "argPhoto"

class PhotoFragment : Fragment() {
    private val args: PhotoFragmentArgs by navArgs()
    private var photo : Photo? = null

    //private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        photo = args.argPhoto
        "Data from bundle:\n$photo".logD(TAG)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        (context as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
//    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
//        (context as AppCompatActivity).run {
//            val navView: BottomNavigationView = findViewById(R.id.nav_view)
//            val navController = findNavController(R.id.nav_host_fragment)
//            // Passing each menu ID as a set of Ids because each
//            // menu should be considered as top level destinations.
//            val appBarConfiguration = AppBarConfiguration(
//                setOf(
//                    R.id.navigation_home, R.id.navigation_collections
//                )
//            )
//            setupActionBarWithNavController(navController, appBarConfiguration)
//            navView.setupWithNavController(navController)
//        }
//    }

//    override fun onDetach() {
//        super.onDetach()
//        listener = null
//    }
//
//    interface OnFragmentInteractionListener {
//        fun onBackPressed()
//    }

//    companion object {
//        @JvmStatic
//        fun newInstance(photo: Photo?) =
//            PhotoFragment().apply {
//                arguments = Bundle().apply {
//                    putParcelable(ARG_PARAM, photo)
//                }
//            }
//    }
}
