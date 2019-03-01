package edu.rosehulman.dripchar.artpics

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.add_dialog.view.*

class PicAdapter(
    val context: Context?,
    var listener: PicListFragment.OnPicSelectedListener?,
    val uid: String,
    var showMine: Boolean
) : RecyclerView.Adapter<PicViewHolder>(), ItemTouchHelperAdapter {

    private var pics = ArrayList<Pic>()
    private val picsRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.PICS_COLLECTION)


    fun addSnapshotListener() {
        picsRef.orderBy("timeStamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, fireStoreException ->
                if (fireStoreException != null) {
                    Log.d(Constants.TAG, "Firebase error: $fireStoreException")
                    return@addSnapshotListener
                }

//            populateLocalQuotes(snapshot!!)
                processSnapshotDiffs(snapshot!!)

            }
    }

    init {
        this.addSnapshotListener()
    }

    private fun processSnapshotDiffs(snapshot: QuerySnapshot) {
        for (docChange in snapshot.documentChanges) {
            val pic = Pic.fromSnapshot(docChange.document)
            if (pic.uid == uid || showMine)
                when (docChange.type) {
                    DocumentChange.Type.ADDED -> {
                        pics.add(0, pic)
                        notifyItemInserted(0)
                    }
                    DocumentChange.Type.REMOVED -> {
                        val position = pics.indexOf(pic)
                        pics.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        //TODO: Look up it in Kotlin
                        val position = pics.indexOfFirst { pic.id == it.id }
                        //This line makes sure local is in sync with the database. WHY?
                        pics[position] = pic
                        notifyItemChanged(position)
                    }
                }
        }
    }

//    private fun populateLocalQuotes(snapshot: QuerySnapshot) {
//        pics.clear()
//        for (document in snapshot.documents) {
//            pics.add(Pic.fromSnapshot(document))
//        }
//        notifyDataSetChanged()
//    }

    override fun getItemCount() = pics.size

    override fun onBindViewHolder(holder: PicViewHolder, position: Int) {
        holder.bind(pics[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_view_art_pic, parent, false)
        return PicViewHolder(view, this, context!!)
    }

    fun showAddEditDialog(position: Int = -1) {
        val builder = AlertDialog.Builder(context!!)
        //Configure the builder: title, icon, message/custom view OR list.  Buttons (pos, neg, neutral)
        builder.setTitle(R.string.add_dia_title)
        //TODO:
        val view = LayoutInflater.from(context).inflate(R.layout.add_dialog, null, false)
        builder.setView(view)

        // TODO: If editing, pre-populate the edit texts (like Jersey)
        if (position >= 0) {
            //Edit
            builder.setTitle(R.string.edit_dia_title)
            view.add_dia_caption_edit_text.setText(pics[position].caption) //Type of text is editable, not a string
            view.add_dia_url_edit_text.setText((pics[position].url))
        }

        builder.setPositiveButton(android.R.string.ok, { _, _ ->
            val caption = view.add_dia_caption_edit_text.text.toString()
            val url = view.add_dia_url_edit_text.text.toString()
            if (position >= 0) {
                edit(position, caption, url)
            } else {
                add(Pic(caption, url, uid))
            }

        })
        builder.setNegativeButton(android.R.string.cancel, null) // :)

        if (position >= 0) {
            builder.setNeutralButton("Remove") { _, _ ->
                remove(position)
            }
        }

        builder.create().show()
    }

    fun add(pic: Pic) {
        picsRef.add(pic)
    }

    fun edit(position: Int, caption: String, url: String) {
        if (pics[position].uid == uid) {

        val temp = pics[position].copy()
        temp.caption = caption
        temp.url = url

//        pics[position].quote = quote
//        pics[position].movie = movie
        //More?
//        notifyItemChanged(position)
        picsRef.document(pics[position].id).set(temp)

        } else {
            Toast.makeText(context, "This pic belongs to another user!", Toast.LENGTH_LONG).show()
            notifyItemChanged(position)
        }
    }

    fun setSelected(position: Int) {
        pics[position].isSelected = !pics[position].isSelected
        notifyItemChanged(position)
    }

    override fun onItemDismiss(index: Int) {
        if (pics[index].uid == uid) {
            remove(index)
        } else {
            Toast.makeText(context, "This pic belongs to another user!", Toast.LENGTH_LONG).show()
            notifyItemChanged(index)
        }
    }

    private fun remove(pos: Int) {
        picsRef.document(pics[pos].id).delete()
    }

    fun selectPic(position: Int) {
        listener?.onPicSelected(pics[position])
    }

    fun getFab() {
        listener?.getFab()
    }


}