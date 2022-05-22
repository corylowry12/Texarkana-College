package com.cory.texarkanacollege.adapters

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.alexvasilkov.gestures.GestureController
import com.alexvasilkov.gestures.animation.ViewPositionAnimator
import com.alexvasilkov.gestures.transition.GestureTransitions
import com.alexvasilkov.gestures.views.GestureImageView
import com.alexvasilkov.gestures.views.interfaces.GestureView
import com.bumptech.glide.Glide
import com.cory.texarkanacollege.database.GradesDBHelper
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.fragments.ImageViewFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GradesAdapter(val context: Context,
                    private val dataList:  ArrayList<HashMap<String, String>>, private val imageDataList: ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name = itemView.findViewById<TextView>(R.id.row_name)!!
        var grade = itemView.findViewById<TextView>(R.id.row_grade)!!
        var weight = itemView.findViewById<TextView>(R.id.row_weight)!!
        var date = itemView.findViewById<TextView>(R.id.row_date)!!
        var image = itemView.findViewById<ImageView>(R.id.image)

        fun bind(position: Int) {

            val dataItem = dataList[position]

            val imageDataItem = imageDataList[position]

            val formatter = SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
            val dateFormatted = formatter.format(formatter.parse(dataItem["date"]!!)!!)

            name.text = "Name: " + dataItem["name"]
            grade.text = "Grade: " + dataItem["grade"] + "%"
            weight.text = "Weight: " + dataItem["weight"] + "%"
            date.text = "Date: " + dateFormatted.toString()

            val imgFile = File(imageDataItem["image"]!!)

            if (imgFile.exists()) {
                val circularProgressDrawable = CircularProgressDrawable(context)
                circularProgressDrawable.strokeWidth = 5f
                circularProgressDrawable.centerRadius = 30f
                circularProgressDrawable.start()

                Glide.with(context)
                    .load(imageDataItem["image"])
                    .centerCrop()
                    .placeholder(circularProgressDrawable)
                    .into(image)
            }
            else {
                image.visibility = View.GONE
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.grades_list_item, parent, false))
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        holder.itemView.findViewById<ImageView>(R.id.image).setOnClickListener {
            val viewImageFragment = ImageViewFragment()
            val args = Bundle()
            val imageDataItem = imageDataList[holder.adapterPosition]
            var datalist = dataList[holder.adapterPosition]
            val id = datalist["primary"]
            val classID = datalist["id"]
            args.putInt("image", classID!!.toInt())
            args.putInt("id", id!!.toInt())
            viewImageFragment.arguments = args

            val manager = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            manager.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            manager.replace(R.id.fragment_container, viewImageFragment).addToBackStack(null)
            manager.commit()

        }

        holder.itemView.setOnLongClickListener {

            val dialog = BottomSheetDialog(context)
            val addGradeView = LayoutInflater.from(context).inflate(R.layout.options_bottom_sheet, null)
            dialog.setCancelable(false)
            dialog.setContentView(addGradeView)

            val editButton = dialog.findViewById<Button>(R.id.editButton)
            val deleteButton = dialog.findViewById<Button>(R.id.deleteButton)
            val deleteAllButton = dialog.findViewById<Button>(R.id.deleteAllButton)
            val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)

            deleteButton?.setOnClickListener {
                val datalist = dataList[holder.adapterPosition]
                val id = datalist["primary"]

                val cursor = GradesDBHelper(context, null).getSingleGrade(id.toString())
                cursor.moveToFirst()
                val map = HashMap<String, String>()

                while (!cursor.isAfterLast) {

                    map["primary"] =
                        cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_ID))
                    map["id"] =
                        cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_CLASS_ID))
                    map["grade"] =
                        cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_GRADE))

                    GradesDBHelper(context, null).deleteSingleRow(map["primary"].toString())

                    cursor.moveToNext()
                }

                dataList.removeAt(holder.adapterPosition)
                notifyItemRemoved(holder.adapterPosition)
                dialog.dismiss()

                val saveState = Runnable {
                    (context as MainActivity).textViewVisibilityGrades()

                }

                MainActivity().runOnUiThread(saveState)

            }

            deleteAllButton?.setOnClickListener {
                val datalist = dataList[holder.adapterPosition]

                val gradesDBHelper = GradesDBHelper(context, null)

                    val alertDialog = MaterialAlertDialogBuilder(context, R.style.AlertDialogStyle)
                    alertDialog.setTitle("Delete All")
                    alertDialog.setMessage("Would you like to delete all grades for this class?")

                    alertDialog.setPositiveButton("Yes") { _, _ ->

                        for (i in 0 until dataList.count()) {
                            gradesDBHelper.deleteAllGradesForOneClass(datalist["id"]!!)
                            notifyItemRemoved(0)
                        }
                        dataList.clear()

                        dialog.dismiss()

                        val saveState = Runnable {
                            (context as MainActivity).textViewVisibilityGrades()

                        }

                        MainActivity().runOnUiThread(saveState)

                    }
                    alertDialog.setNegativeButton("No") { _, _ ->
                        dialog.dismiss()
                    }
                    alertDialog.show()
            }

            cancelButton?.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
            return@setOnLongClickListener true
        }
        (holder as ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}