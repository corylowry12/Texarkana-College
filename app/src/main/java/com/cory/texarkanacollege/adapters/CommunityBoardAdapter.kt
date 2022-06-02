package com.cory.texarkanacollege.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.fragments.ViewCommunityBoardPostFragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth

class CommunityBoardAdapter(val context: Context,
                            private val dataList:  ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var firebaseAuth: FirebaseAuth

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var contactName = itemView.findViewById<TextView>(R.id.row_contact_name)!!
        var title = itemView.findViewById<TextView>(R.id.row_title)
        var date = itemView.findViewById<TextView>(R.id.row_date)
        val content = itemView.findViewById<TextView>(R.id.row_content)
        val pinnedChip = itemView.findViewById<Chip>(R.id.pinnedChip)
        val imageTextView = itemView.findViewById<TextView>(R.id.imageTextView)

        fun bind(position: Int) {

            val dataItem = dataList[position]

            contactName.text = "Name: " + dataItem["name"]
            title.text = "Title: " + dataItem["title"]
            date.text = "Date: " + dataItem["date"]
            content.text = "Content: " + dataItem["content"]

            if (dataItem["pinned"].toString() != "true") {
                pinnedChip.visibility = View.GONE
            }

            if (dataItem["imageURL"] == "") {
                imageTextView.visibility = View.GONE
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.community_board_list_row, parent, false))
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataItem = dataList[position]
        if (dataItem["urgent"] == "true") {
            holder.itemView.findViewById<MaterialCardView>(R.id.cardViewCampusBoard).setCardBackgroundColor(ContextCompat.getColor(context, R.color.urgentPostRed))
        }

        firebaseAuth = FirebaseAuth.getInstance()

        if (dataItem["uid"] == firebaseAuth.currentUser!!.uid.toString()) {
            holder.itemView.setOnLongClickListener {
                Toast.makeText(context, "This is your post", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }
        }

        holder.itemView.setOnClickListener {

            val fragment = ViewCommunityBoardPostFragment()

            val args = Bundle()
            args.putString("name", dataItem["name"])
            args.putString("title", dataItem["title"])
            args.putString("content", dataItem["content"])
            args.putString("imageURL", dataItem["imageURL"])
            args.putString("childPosition", dataItem["childPosition"])
            fragment.arguments = args

            val manager =
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            manager.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            manager.add(R.id.fragment_container, fragment)
                .addToBackStack(null)
            manager.commit()
        }
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}