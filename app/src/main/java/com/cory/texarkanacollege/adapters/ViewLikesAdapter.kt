package com.cory.texarkanacollege.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cory.texarkanacollege.R
import de.hdodenhof.circleimageview.CircleImageView

class ViewLikesAdapter(val context: Context,
                       private val dataList:  ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var contactName = itemView.findViewById<TextView>(R.id.likesName)!!
        var profilePicImageView = itemView.findViewById<CircleImageView>(R.id.profilePicLikes)!!


        fun bind(position: Int) {

            val dataItem = dataList[position]

            contactName.text = "Name: " + dataItem["name"]

            Glide.with(context)
                .load(dataItem["profilePicURL"])
                .into(profilePicImageView)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.likes_list_item, parent, false))
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }
}