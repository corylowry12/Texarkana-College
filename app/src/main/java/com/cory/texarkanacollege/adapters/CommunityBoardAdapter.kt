package com.cory.texarkanacollege.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.CommentLikeCounter
import com.cory.texarkanacollege.fragments.ViewCommunityBoardPostFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class CommunityBoardAdapter(val context: Context,
                            private val dataList:  ArrayList<HashMap<String, String>>, private val likesDataList:  ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var firebaseAuth: FirebaseAuth

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var contactName = itemView.findViewById<TextView>(R.id.row_contact_name)!!
        var title = itemView.findViewById<TextView>(R.id.row_title)!!
        var date = itemView.findViewById<TextView>(R.id.row_date)!!
        val content = itemView.findViewById<TextView>(R.id.row_content)!!
        val email = itemView.findViewById<TextView>(R.id.row_contact_email)!!
        val pinnedChip = itemView.findViewById<Chip>(R.id.pinnedChip)!!
        val imageTextView = itemView.findViewById<TextView>(R.id.imageTextView)!!
        val profileImage = itemView.findViewById<CircleImageView>(R.id.profilePic)!!
        val likedCountTextView = itemView.findViewById<TextView>(R.id.likesCountTextView)!!
        val commentCountTextView = itemView.findViewById<TextView>(R.id.commentCountTextView)!!
        val counterLinearLayout = itemView.findViewById<LinearLayout>(R.id.counterLinearLayout)


        fun bind(position: Int) {

            val dataItem = dataList[position]

            contactName.text = "Name: " + dataItem["name"]
            title.text = "Title: " + dataItem["title"]
            date.text = "Date: " + dataItem["date"]
            likedCountTextView.text = dataItem["likedCount"]
            commentCountTextView.text = dataItem["commentCount"]

            if (!CommentLikeCounter(context).loadCounterVisibility()) {
                counterLinearLayout.visibility = View.GONE
            }

            if (dataItem["content"]!!.length < 30) {
                content.text = "Content: " + dataItem["content"]!!.trim()
            }
            else {
                val contentSubstring = dataItem["content"]!!.substring(0, 30)
                content.text = "Content: " + contentSubstring.trim() + "..."
            }

            if (dataItem["email"] == "") {
                email.visibility = View.GONE
            }
            else {
                email.text = "Email: " + dataItem["email"]
            }

            if (dataItem["pinned"].toString() != "true") {
                pinnedChip.visibility = View.GONE
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
        }

        firebaseAuth = FirebaseAuth.getInstance()

            holder.itemView.setOnLongClickListener {
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
                    val bottomSheetDialog = BottomSheetDialog(context)
                    val editGradeBottomSheetView =
                        LayoutInflater.from(context).inflate(R.layout.view_likes_bottom_sheet, null)
                    val recyclerView = editGradeBottomSheetView.findViewById<RecyclerView>(R.id.viewLikesRecyclerView)
                    val viewLikesAdapter = ViewLikesAdapter(context, likesDataList)
                    recyclerView.adapter = viewLikesAdapter
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    bottomSheetDialog.setContentView(editGradeBottomSheetView)
                    bottomSheetDialog.show()
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
                        val bottomSheetDialog = BottomSheetDialog(context)
                        val editGradeBottomSheetView =
                            LayoutInflater.from(context).inflate(R.layout.view_likes_bottom_sheet, null)
                        val recyclerView = editGradeBottomSheetView.findViewById<RecyclerView>(R.id.viewLikesRecyclerView)
                        val viewLikesAdapter = ViewLikesAdapter(context, likesDataList)
                        recyclerView.adapter = viewLikesAdapter
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        bottomSheetDialog.setContentView(editGradeBottomSheetView)
                        bottomSheetDialog.show()
                        dialog.dismiss()
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

        holder.itemView.setOnClickListener {

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

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    @RequiresApi(Build.VERSION_CODES.M)
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
}