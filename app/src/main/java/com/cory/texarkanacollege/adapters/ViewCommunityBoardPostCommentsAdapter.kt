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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class ViewCommunityBoardPostCommentsAdapter(val context: Context,
                                            private val dataList:  ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var firebaseAuth: FirebaseAuth

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name = itemView.findViewById<TextView>(R.id.row_display_name)
        var title = itemView.findViewById<TextView>(R.id.row_comment_title)
        var dateTextView = itemView.findViewById<TextView>(R.id.dateTextView)
        var profilePic = itemView.findViewById<CircleImageView>(R.id.profilePicComments)

        fun bind(position: Int) {

            val dataItem = dataList[position]

            name.text = dataItem["name"]
            title.text = dataItem["title"]
            dateTextView.text = dataItem["date"]

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

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataItem = dataList[position]

        holder.itemView.setOnLongClickListener {
            if (dataItem["uid"] == FirebaseAuth.getInstance().currentUser!!.uid.toString()) {
                val dialog = BottomSheetDialog(context)
                val postOptionsLayout = LayoutInflater.from(context).inflate(R.layout.edit_comment_bottom_sheet, null)
                dialog.setCancelable(false)
                dialog.setContentView(postOptionsLayout)
                val editPostEditButton = dialog.findViewById<Button>(R.id.editPostEditButton)
                val editPostDeleteButton = dialog.findViewById<Button>(R.id.editPostDeleteButton)
                val editPostCancelButton = dialog.findViewById<Button>(R.id.editPostCancelButton)

                editPostEditButton?.visibility = View.GONE

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

                        val loadIntoList = Runnable {
                            (context as MainActivity).setViewPostCommunityBoardLoadIntoList()

                        }

                        MainActivity().runOnUiThread(loadIntoList)
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
            false
        }

        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}