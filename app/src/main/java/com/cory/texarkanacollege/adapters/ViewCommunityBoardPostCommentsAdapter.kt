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
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class ViewCommunityBoardPostCommentsAdapter(val context: Context,
                                            private val dataList:  ArrayList<HashMap<String, String>>, private val likesDataList: ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var firebaseAuth: FirebaseAuth

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name = itemView.findViewById<TextView>(R.id.row_display_name)
        var title = itemView.findViewById<TextView>(R.id.row_comment_title)
        var dateTextView = itemView.findViewById<TextView>(R.id.dateTextView)
        var profilePic = itemView.findViewById<CircleImageView>(R.id.profilePicComments)
        val likesCountTextView = itemView.findViewById<TextView>(R.id.likesCountTextView)

        fun bind(position: Int) {

            val dataItem = dataList[position]

            name.text = dataItem["name"]
            title.text = dataItem["title"]
            dateTextView.text = dataItem["date"]
            likesCountTextView.text = "Likes: " + dataItem["likedCount"]

            Glide.with(context)
                .load(dataItem["profilePicURL"])
                .error(R.drawable.ic_baseline_broken_image_24)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(profilePic)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.view_community_board_list_item_row, parent, false))
    }

    fun likeComment(position: Int) {
        val dataItem = dataList[position]
        val database = Firebase.database.reference

        FirebaseAuth.getInstance().currentUser?.reload()

        var set = false

        if (FirebaseAuth.getInstance().currentUser != null) {
        database.child("posts").child(dataItem["childPosition"].toString()).child("comments").child(dataItem["commentPosition"].toString())
            .orderByKey()
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        if (i.child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("liked").value == true) {
                            val uid = FirebaseAuth.getInstance().currentUser!!.uid
                            val name = FirebaseAuth.getInstance().currentUser!!.displayName
                            val profilePicURL = FirebaseAuth.getInstance().currentUser!!.photoUrl
                            FirebaseDatabase.getInstance().getReference("posts")
                                .child(dataItem["childPosition"].toString()).child("comments")
                                .child(dataItem["commentPosition"].toString()).child("likes")
                                .child(uid)
                                .child("liked").setValue(false)
                            FirebaseDatabase.getInstance().getReference("posts")
                                .child(dataItem["childPosition"].toString()).child("comments")
                                .child(dataItem["commentPosition"].toString()).child("likes")
                                .child(uid)
                                .child("profilePicURL")
                                .setValue(profilePicURL.toString())
                            FirebaseDatabase.getInstance().getReference("posts")
                                .child(dataItem["childPosition"].toString()).child("comments")
                                .child(dataItem["commentPosition"].toString()).child("likes")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child("name")
                                .setValue(name)

                            set = false

                            try {
                                for (z in 0 until likesDataList.count()) {
                                    if (likesDataList[z]["profilePicURL"].toString() == FirebaseAuth.getInstance().currentUser!!.photoUrl.toString()) {
                                        likesDataList.removeAt(z)
                                    }
                                }
                            }
                            catch (e: Exception) {
                                e.printStackTrace()
                            }
                            break
                        }
                        else {
                                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                                val name = FirebaseAuth.getInstance().currentUser!!.displayName
                                val profilePicURL = FirebaseAuth.getInstance().currentUser!!.photoUrl
                                FirebaseDatabase.getInstance().getReference("posts")
                                    .child(dataItem["childPosition"].toString()).child("comments")
                                    .child(dataItem["commentPosition"].toString()).child("likes")
                                    .child(uid)
                                    .child("liked").setValue(true)
                                FirebaseDatabase.getInstance().getReference("posts")
                                    .child(dataItem["childPosition"].toString()).child("comments")
                                    .child(dataItem["commentPosition"].toString()).child("likes")
                                    .child(uid)
                                    .child("profilePicURL")
                                    .setValue(profilePicURL.toString())
                                FirebaseDatabase.getInstance().getReference("posts")
                                    .child(dataItem["childPosition"].toString()).child("comments")
                                    .child(dataItem["commentPosition"].toString()).child("likes")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .child("name")
                                    .setValue(name)
                                set = true
                            }
                    }
                    if (set) {
                        val uid = FirebaseAuth.getInstance().currentUser!!.uid
                        val name = FirebaseAuth.getInstance().currentUser!!.displayName
                        val profilePicURL = FirebaseAuth.getInstance().currentUser!!.photoUrl
                        val map = HashMap<String, String>()
                        map["profilePicURL"] = profilePicURL.toString()
                        map["name"] = name.toString()

                        map["post_number"] = dataItem["commentPosition"].toString()
                        dataItem["likedCount"] = (dataItem["likedCount"]!!.toInt() + 1).toString()
                        likesDataList.add(map)
                    }
                    else {
                        dataItem["likedCount"] = (dataItem["likedCount"]!!.toInt() - 1).toString()
                    }
                    notifyItemChanged(position)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
            }
        else {
            Toast.makeText(
                context,
                "You must be signed in to like comments",
                Toast.LENGTH_SHORT
            ).show()
            notifyItemChanged(position)
        }
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataItem = dataList[position]

        holder.itemView.setOnLongClickListener {
            FirebaseAuth.getInstance().currentUser?.reload()
            if (dataItem["uid"] == FirebaseAuth.getInstance().currentUser?.uid.toString()) {
                val dialog = BottomSheetDialog(context)
                val postOptionsLayout = LayoutInflater.from(context).inflate(R.layout.edit_comment_bottom_sheet, null)
                dialog.setCancelable(false)
                dialog.setContentView(postOptionsLayout)
                val editPostViewLikesButton = dialog.findViewById<Button>(R.id.editCommentViewLikesButton)
                val editPostEditButton = dialog.findViewById<Button>(R.id.editPostEditButton)
                val editPostDeleteButton = dialog.findViewById<Button>(R.id.editPostDeleteButton)
                val editPostCancelButton = dialog.findViewById<Button>(R.id.editPostCancelButton)

                editPostEditButton?.visibility = View.GONE

                editPostViewLikesButton?.setOnClickListener {
                    val likesDataListPost = ArrayList<HashMap<String, String>>()
                    for (i in 0 until likesDataList.count()) {
                        if (likesDataList[i]["post_number"] == dataItem["commentPosition"]) {
                            val map = java.util.HashMap<String, String>()
                            map["name"] = likesDataList[i]["name"].toString()
                            map["profilePicURL"] = likesDataList[i]["profilePicURL"].toString()
                            likesDataListPost.add(map)
                        }
                    }

                    if (likesDataListPost.isNotEmpty()) {
                        val bottomSheetDialog = BottomSheetDialog(context)
                        val editGradeBottomSheetView =
                            LayoutInflater.from(context)
                                .inflate(R.layout.view_likes_bottom_sheet, null)
                        val recyclerView =
                            editGradeBottomSheetView.findViewById<RecyclerView>(R.id.viewLikesRecyclerView)
                        val viewLikesAdapter = ViewLikesAdapter(context, likesDataListPost)
                        recyclerView.adapter = viewLikesAdapter
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        bottomSheetDialog.setContentView(editGradeBottomSheetView)
                        bottomSheetDialog.show()
                    }
                    else {
                        Toast.makeText(context, "There are no likes for this post", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }

                editPostDeleteButton?.setOnClickListener {
                    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context, R.style.AlertDialogStyle)
                    materialAlertDialogBuilder.setTitle("Delete Comment?")
                    materialAlertDialogBuilder.setMessage("Would you like to delete this comment? It can not be undone.")
                    materialAlertDialogBuilder.setPositiveButton("Yes") { _, _ ->

                            FirebaseDatabase.getInstance().getReference("posts")
                                .child(dataItem["childPosition"].toString()).child("comments").child(dataItem["commentPosition"].toString()).removeValue()
                            dataList.removeAt(holder.adapterPosition)
                            notifyItemRemoved(holder.adapterPosition)
                            Toast.makeText(context, "Comment Deleted", Toast.LENGTH_SHORT)
                                .show()
                            dialog.dismiss()

                        if (dataList.isEmpty()) {
                            val loadIntoList = Runnable {
                                (context as MainActivity).setViewPostCommunityBoardLoadIntoList()

                            }

                            MainActivity().runOnUiThread(loadIntoList)
                        }
                    }
                    materialAlertDialogBuilder.setNegativeButton("No") {_, _ ->
                        dialog.dismiss()
                    }
                    materialAlertDialogBuilder.show()
                }
                editPostCancelButton?.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                return@setOnLongClickListener true
            }
            else {
                val dialog = BottomSheetDialog(context)
                val postOptionsLayout = LayoutInflater.from(context).inflate(R.layout.edit_comment_bottom_sheet, null)
                dialog.setCancelable(false)
                dialog.setContentView(postOptionsLayout)
                val editPostViewLikesButton = dialog.findViewById<Button>(R.id.editCommentViewLikesButton)
                val editPostEditButton = dialog.findViewById<Button>(R.id.editPostEditButton)
                val editPostDeleteButton = dialog.findViewById<Button>(R.id.editPostDeleteButton)
                val editPostCancelButton = dialog.findViewById<Button>(R.id.editPostCancelButton)

                editPostDeleteButton?.visibility = View.GONE

                editPostEditButton?.visibility = View.GONE

                editPostViewLikesButton?.setOnClickListener {
                    val likesDataListPost = ArrayList<HashMap<String, String>>()

                    for (i in 0 until likesDataList.count()) {
                        if (likesDataList[i]["post_number"] == dataItem["commentPosition"]) {
                            val map = java.util.HashMap<String, String>()
                            map["name"] = likesDataList[i]["name"].toString()
                            map["profilePicURL"] = likesDataList[i]["profilePicURL"].toString()
                            likesDataListPost.add(map)
                        }
                    }

                    if (likesDataListPost.isNotEmpty()) {
                        val bottomSheetDialog = BottomSheetDialog(context)
                        val editGradeBottomSheetView =
                            LayoutInflater.from(context)
                                .inflate(R.layout.view_likes_bottom_sheet, null)
                        val recyclerView =
                            editGradeBottomSheetView.findViewById<RecyclerView>(R.id.viewLikesRecyclerView)
                        val viewLikesAdapter = ViewLikesAdapter(context, likesDataListPost)
                        recyclerView.adapter = viewLikesAdapter
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        bottomSheetDialog.setContentView(editGradeBottomSheetView)
                        bottomSheetDialog.show()
                    }
                    else {
                        Toast.makeText(context, "There are no likes for this post", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                editPostCancelButton?.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                return@setOnLongClickListener true
            }
            false
        }

        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}