package com.cory.texarkanacollege.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cory.texarkanacollege.R
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class ViewCommunityBoardPostCommentsAdapter(val context: Context,
                                            private val dataList:  ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var firebaseAuth: FirebaseAuth

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name = itemView.findViewById<TextView>(R.id.row_display_name)
        var title = itemView.findViewById<TextView>(R.id.row_comment_title)
        var dateChip = itemView.findViewById<Chip>(R.id.dateChip)
        var profilePic = itemView.findViewById<CircleImageView>(R.id.profilePicComments)

        fun bind(position: Int) {

            val dataItem = dataList[position]

            name.text = "Name: " + dataItem["name"]
            title.text = dataItem["title"]
            dateChip.text = dataItem["date"]

            Glide.with(context)
                .load(dataItem["profilePicURL"])
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(profilePic)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.view_community_board_list_item_row, parent, false))
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}