package com.cory.texarkanacollege.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.CommentLikeCounter
import com.cory.texarkanacollege.fragments.ViewCommunityBoardPostFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CommunityBoardAdapter(val context: Context,
                            private val dataList:  ArrayList<HashMap<String, String>>, private val likesDataList:  ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val dateType =  ArrayList<Boolean>()

    private lateinit var firebaseAuth: FirebaseAuth

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var contactName = itemView.findViewById<TextView>(R.id.row_contact_name)!!
        var title = itemView.findViewById<TextView>(R.id.row_title)!!
        var date = itemView.findViewById<TextView>(R.id.row_date)!!
        val content = itemView.findViewById<TextView>(R.id.row_content)!!
        val pinnedConstraint = itemView.findViewById<ConstraintLayout>(R.id.pinnedConstraintLayout)!!
        val imageTextView = itemView.findViewById<TextView>(R.id.imageTextView)!!
        val profileImage = itemView.findViewById<CircleImageView>(R.id.profilePic)!!
        val likedCountTextView = itemView.findViewById<TextView>(R.id.likesCountTextView)!!
        val commentCountTextView = itemView.findViewById<TextView>(R.id.commentCountTextView)!!
        val likesCounterCardView = itemView.findViewById<CardView>(R.id.likesCardView)!!
        val commentCounterCardView = itemView.findViewById<CardView>(R.id.commentsCardView)!!


        fun bind(position: Int) {

            val dataItem = dataList[position]

            val dateFormatter = SimpleDateFormat("MMM/dd/yyyy hh:mm a", Locale.ENGLISH)
            val dateFormatted = dateFormatter.parse(dataItem["date"]!!)!!
            val timeInMil = dateFormatted.time
            val currentTime = System.currentTimeMillis()
            val difference = currentTime - timeInMil
            var diff = ""

            dateType.add(position, false)

            if (difference > 86400000) {
                val dateFormatter2 = SimpleDateFormat("MMM/dd/yyyy hh:mm a", Locale.ENGLISH)
                val daysFormatter = SimpleDateFormat("MMM dd", Locale.ENGLISH)
                val daysDate = dateFormatter2.parse(dataItem["date"]!!)
                val days = daysFormatter.format(daysDate!!)
                diff = days.toString()
            }
            else if (difference in 3600000..86399999) {
                diff = TimeUnit.MILLISECONDS.toHours(difference).toString() + "h"
            }
            else if (difference < 86400000) {
                diff = TimeUnit.MILLISECONDS.toMinutes(difference).toString() + " Min"
            }
            else {
                diff = dataItem["date"].toString()
            }

            contactName.text = dataItem["name"]
            title.text = dataItem["title"]
            date.text = diff
            likedCountTextView.text = dataItem["likedCount"]
            commentCountTextView.text = dataItem["commentCount"]

            if (!CommentLikeCounter(context).loadCounterVisibility()) {
                likesCounterCardView.visibility = View.GONE
                commentCounterCardView.visibility = View.GONE
            }

            if (dataItem["content"]!!.length < 80) {
                content.text = dataItem["content"]!!.trim()
            }
            else {
                val contentSubstring = dataItem["content"]!!.substring(0, 80)
                content.text = contentSubstring.trim() + "..."
            }

            if (dataItem["pinned"].toString() != "true") {
                pinnedConstraint.visibility = View.GONE
            }

            if (dataItem["imageURL"] == "" || dataItem["imageURL"] == null) {
                imageTextView.visibility = View.GONE
            }

            val circularProgressDrawableProfilePic = CircularProgressDrawable(context)
            circularProgressDrawableProfilePic.strokeWidth = 2f
            circularProgressDrawableProfilePic.centerRadius = 20f
            circularProgressDrawableProfilePic.setColorSchemeColors(ContextCompat.getColor(context, R.color.blue))
            circularProgressDrawableProfilePic.start()

            Glide.with(context)
                .load(dataItem["profilePicURL"])
                .centerCrop()
                .placeholder(circularProgressDrawableProfilePic)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(profileImage)

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
            holder.itemView.findViewById<CardView>(R.id.contentCardView).setCardBackgroundColor(ContextCompat.getColor(context, R.color.communityBoardAccentRed))
            holder.itemView.findViewById<CardView>(R.id.likesCardView).setCardBackgroundColor(ContextCompat.getColor(context, R.color.communityBoardAccentRed))
            holder.itemView.findViewById<CardView>(R.id.commentsCardView).setCardBackgroundColor(ContextCompat.getColor(context, R.color.communityBoardAccentRed))
        }
        else {
            holder.itemView.findViewById<CardView>(R.id.contentCardView).setCardBackgroundColor(ContextCompat.getColor(context, R.color.communityBoardAccentBlue))
            holder.itemView.findViewById<CardView>(R.id.likesCardView).setCardBackgroundColor(ContextCompat.getColor(context, R.color.communityBoardAccentBlue))
            holder.itemView.findViewById<CardView>(R.id.commentsCardView).setCardBackgroundColor(ContextCompat.getColor(context, R.color.communityBoardAccentBlue))
        }

        holder.itemView.findViewById<TextView>(R.id.row_contact_name).setOnClickListener {
            if (holder.itemView.findViewById<TextView>(R.id.row_contact_name).text.toString().isEmailValid()) {
                holder.itemView.findViewById<TextView>(R.id.row_contact_name).text = dataItem["name"]
            }
            else {
                holder.itemView.findViewById<TextView>(R.id.row_contact_name).text = dataItem["email"]
            }
        }

        holder.itemView.findViewById<TextView>(R.id.row_date).setOnClickListener {
            if (!dateType.elementAt(holder.adapterPosition)) {
                holder.itemView.findViewById<TextView>(R.id.row_date).text = dataItem["date"]
                dateType[holder.adapterPosition] = true
            }
            else {
                val dateFormatter = SimpleDateFormat("MMM/dd/yyyy hh:mm a", Locale.ENGLISH)
                val dateFormatted = dateFormatter.parse(dataItem["date"]!!)!!
                val timeInMil = dateFormatted.time
                val currentTime = System.currentTimeMillis()
                val difference = currentTime - timeInMil
                var diff = ""

                if (difference > 86400000) {
                    val dateFormatter2 = SimpleDateFormat("MMM/dd/yyyy hh:mm a", Locale.ENGLISH)
                    val daysFormatter = SimpleDateFormat("MMM dd", Locale.ENGLISH)
                    val daysDate = dateFormatter2.parse(dataItem["date"]!!)
                    val days = daysFormatter.format(daysDate!!)
                    diff = days.toString()
                }
                else if (difference in 3600000..86399999) {
                    diff = TimeUnit.MILLISECONDS.toHours(difference).toString() + "h"
                }
                else if (difference < 86400000) {
                    diff = TimeUnit.MILLISECONDS.toMinutes(difference).toString() + " Min"
                }
                else {
                    diff = dataItem["date"].toString()
                }

                holder.itemView.findViewById<TextView>(R.id.row_date).text = diff
                dateType[holder.adapterPosition] = false
            }
        }

        firebaseAuth = FirebaseAuth.getInstance()

        holder.itemView.findViewById<ConstraintLayout>(R.id.communityBoardItemConstraintLayout).setOnLongClickListener {
                firebaseAuth.currentUser?.reload()

                if (dataItem["uid"] == firebaseAuth.currentUser?.uid.toString()) {
                val dialog = BottomSheetDialog(context)
                val postOptionsLayout = LayoutInflater.from(context).inflate(R.layout.edit_post_bottom_sheet, null)
                dialog.setCancelable(false)
                dialog.setContentView(postOptionsLayout)
                val editPostEditButton = dialog.findViewById<Button>(R.id.editPostEditButton)
                val editPostDeleteButton = dialog.findViewById<Button>(R.id.editPostDeleteButton)
                val editPostCancelButton = dialog.findViewById<Button>(R.id.editPostCancelButton)
                val editPostViewLikesButton = dialog.findViewById<Button>(R.id.editPostViewLikesButton)

                editPostViewLikesButton?.setOnClickListener {
                    val likesDataListPost = ArrayList<HashMap<String, String>>()
                    for (i in 0 until likesDataList.count()) {
                        if (likesDataList[i]["post_number"] == dataItem["childPosition"]) {
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

                editPostEditButton?.visibility = View.GONE

                editPostDeleteButton?.setOnClickListener {
                    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context, R.style.AlertDialogStyle)
                    materialAlertDialogBuilder.setTitle("Delete Post?")
                    materialAlertDialogBuilder.setMessage("Would you like to delete this post? It can not be undone.")
                    materialAlertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                        if (dataItem["imageURL"] != "" && dataItem["imageURL"] != null) {
                            if (isOnline(context)) {
                                FirebaseStorage.getInstance()
                                    .getReference(dataItem["images"].toString()).delete()
                                    .addOnSuccessListener {
                                        FirebaseDatabase.getInstance().getReference("posts")
                                            .child(dataItem["childPosition"].toString())
                                            .removeValue()
                                        dataList.removeAt(holder.adapterPosition)
                                        notifyItemRemoved(holder.adapterPosition)
                                        Toast.makeText(context, "Post Deleted", Toast.LENGTH_SHORT)
                                            .show()
                                        dialog.dismiss()
                                    }
                                    .addOnFailureListener {
                                        FirebaseDatabase.getInstance().getReference("posts")
                                            .child(dataItem["childPosition"].toString())
                                            .removeValue()
                                        dataList.removeAt(holder.adapterPosition)
                                        notifyItemRemoved(holder.adapterPosition)
                                        Toast.makeText(context, "Post Deleted", Toast.LENGTH_SHORT)
                                            .show()
                                        dialog.dismiss()
                                    }
                            }
                            else {
                                Toast.makeText(context, "Check your data connection", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else {
                            FirebaseDatabase.getInstance().getReference("posts")
                                .child(dataItem["childPosition"].toString()).removeValue()
                            dataList.removeAt(holder.adapterPosition)
                            notifyItemRemoved(holder.adapterPosition)
                            Toast.makeText(context, "Post Deleted", Toast.LENGTH_SHORT)
                                .show()
                            dialog.dismiss()
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
                    val postOptionsLayout = LayoutInflater.from(context).inflate(R.layout.edit_post_bottom_sheet, null)
                    dialog.setCancelable(false)
                    dialog.setContentView(postOptionsLayout)
                    val editPostEditButton = dialog.findViewById<Button>(R.id.editPostEditButton)
                    val editPostDeleteButton = dialog.findViewById<Button>(R.id.editPostDeleteButton)
                    val editPostCancelButton = dialog.findViewById<Button>(R.id.editPostCancelButton)
                    val editPostViewLikesButton = dialog.findViewById<Button>(R.id.editPostViewLikesButton)

                    editPostViewLikesButton?.setOnClickListener {
                        if (likesDataList.isNotEmpty()) {
                            val bottomSheetDialog = BottomSheetDialog(context)
                            val editGradeBottomSheetView =
                                LayoutInflater.from(context)
                                    .inflate(R.layout.view_likes_bottom_sheet, null)
                            val recyclerView =
                                editGradeBottomSheetView.findViewById<RecyclerView>(R.id.viewLikesRecyclerView)
                            val viewLikesAdapter = ViewLikesAdapter(context, likesDataList)
                            recyclerView.adapter = viewLikesAdapter
                            recyclerView.layoutManager = LinearLayoutManager(context)
                            bottomSheetDialog.setContentView(editGradeBottomSheetView)
                            bottomSheetDialog.show()
                            dialog.dismiss()
                        }
                        else {
                            Toast.makeText(context, "There are no likes for this post", Toast.LENGTH_SHORT).show()
                        }
                    }

                    editPostEditButton?.visibility = View.GONE
                    editPostDeleteButton?.visibility = View.GONE

                    editPostCancelButton?.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                    return@setOnLongClickListener true
                }
                false
        }

        holder.itemView.findViewById<ConstraintLayout>(R.id.communityBoardItemConstraintLayout).setOnClickListener {

            val fragment = ViewCommunityBoardPostFragment()
            (context as MainActivity).viewPostCommunityBoardFragment = fragment

            val args = Bundle()
            args.putString("name", dataItem["name"])
            args.putString("title", dataItem["title"])
            args.putString("content", dataItem["content"])
            args.putString("imageURL", dataItem["imageURL"])
            args.putString("childPosition", dataItem["childPosition"])
            args.putString("date", dataItem["date"])
            args.putString("profilePicURL", dataItem["profilePicURL"])
            args.putString("email", dataItem["email"])
            args.putString("pinned", dataItem["pinned"].toString())
            args.putString("urgent", dataItem["urgent"].toString())
            fragment.arguments = args

            val manager =
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            manager.setCustomAnimations(R.anim.slide_in_fragment, R.anim.fade_out_fragment, R.anim.fade_in_fragment, R.anim.slide_out_fragment)
            manager.add(R.id.fragment_container, fragment)
                .addToBackStack(null)
            manager.commit()
        }
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

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}