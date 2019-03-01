package edu.rosehulman.dripchar.artpics

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.row_view_art_pic.view.*

class PicViewHolder(itemView: View, val adapter: PicAdapter, val context: Context) : RecyclerView.ViewHolder(itemView)  {
    //Two ways to capture views from xml
    //1: Java way
    private val captionTextView = itemView.findViewById<TextView>(R.id.caption_text_view)
    private val urlTextView = itemView.findViewById<TextView>(R.id.url_text_view)
    //2: Kotlin way
    private val cardView = itemView.card_view

    init {
        itemView.setOnClickListener {
            //TODO: Launch the fragment
            adapter.selectPic(adapterPosition)
        }
        //Have to return a boolean
        itemView.setOnLongClickListener {
            adapter.showAddEditDialog(adapterPosition)
            true
        }
    }

    fun bind(pic: Pic){
        captionTextView.text = pic.caption
        urlTextView.text = pic.url

//        if (pic.isSelected){
//            val color = ContextCompat.getColor(context, R.color.colorAccent)
//            cardView.setCardBackgroundColor(color)
//        } else {
//            cardView.setCardBackgroundColor(Color.WHITE)
//        }
    }


}