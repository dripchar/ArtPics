package edu.rosehulman.dripchar.artpics

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.detail_fragment.view.*

private const val ARG_PIC = "param1"

class PicDetailFragment : Fragment() {
    private var pic: Pic? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pic = it.getParcelable(ARG_PIC)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //NOTE: Set Text or .text?
        val view = inflater.inflate(R.layout.detail_fragment, container, false)
        Picasso.get().load(pic?.url).resize(800,1200).centerCrop().into(view.detail_fragment_image_view)
        view.detail_fragment_text_view.text = pic?.caption
        return view
    }




    companion object {

        //TODO: Needs to return a PicDetailFragment???
        @JvmStatic
        fun newInstance(pic: Pic) =
            PicDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PIC, pic)
                }
            }
    }

}