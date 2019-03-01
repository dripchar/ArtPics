package edu.rosehulman.dripchar.artpics

import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_pic_list.*

private const val ARG_UID = "UID"


class PicListFragment : Fragment() {
    lateinit private var uid: String
    var showMine = true
    private var listener : OnPicSelectedListener? = null
    private lateinit var adapter: PicAdapter
    //TODO: Maybe keep the list of pics here?


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uid = it.getString(ARG_UID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val recyclerView = inflater.inflate(R.layout.fragment_pic_list, container, false) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity) //a fragment doesn't have its own context, but does have access to the activity
        recyclerView.setHasFixedSize(true)

        adapter = PicAdapter(activity, listener, uid!!, showMine)
        recyclerView.adapter = adapter

        var fab = listener!!.getFab()

        fab.setOnClickListener{
            adapter.showAddEditDialog()
        }

        val callback = SimpleItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)



        // we are in a fragment so "this" is NOT the context
        //one is being used to hold the function, another is being used to ______________
        return recyclerView
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPicSelectedListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + getString(R.string.on_attach_error_msg_concat))
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        @JvmStatic
        fun newInstance(uid: String) =
            PicListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_UID, uid)
                }
            }
    }

    interface OnPicSelectedListener {
        // TODO: Update argument type and name
        fun onPicSelected(pic: Pic)
        fun getFab() : FloatingActionButton
    }



}