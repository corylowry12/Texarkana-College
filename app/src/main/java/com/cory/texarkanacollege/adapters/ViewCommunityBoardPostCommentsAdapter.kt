package com.cory.texarkanacollege.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cory.texarkanacollege.R
import com.google.firebase.auth.FirebaseAuth

class ViewCommunityBoardPostCommentsAdapter(val context: Context,
                                            private val dataList:  ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var firebaseAuth: FirebaseAuth

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name = itemView.findViewById<TextView>(R.id.row_display_name)
        var title = itemView.findViewById<TextView>(R.id.row_comment_title)

        fun bind(position: Int) {

            val dataItem = dataList[position]

            name.text = "Name: " + dataItem["name"]
            title.text = "Title: " + dataItem["title"]

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