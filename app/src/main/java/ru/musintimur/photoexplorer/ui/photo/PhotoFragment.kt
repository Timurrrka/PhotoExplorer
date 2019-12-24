package ru.musintimur.photoexplorer.ui.photo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_photo.*

import ru.musintimur.photoexplorer.R
import ru.musintimur.photoexplorer.data.Photo
import ru.musintimur.photoexplorer.utils.logD

private const val TAG = "PhotoFragment"
private const val ARG_PARAM = "photo"

class PhotoFragment : Fragment() {
    private var photo : Photo? = null
    //private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        photo = arguments?.getParcelable(ARG_PARAM)
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
            detail_link_full.text = url_full
            Picasso.get()
                .load(url_small)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(detail_photo)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (context as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        listener = null
//    }
//
//    interface OnFragmentInteractionListener {
//        fun onBackPressed()
//    }

    companion object {
        @JvmStatic
        fun newInstance(photo: Photo?) =
            PhotoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM, photo)
                }
            }
    }
}
