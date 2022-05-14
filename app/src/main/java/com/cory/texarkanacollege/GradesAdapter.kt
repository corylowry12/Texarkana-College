package com.cory.texarkanacollege

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import okio.Utf8

class GradesAdapter(val context: Context,
                    private val dataList:  ArrayList<HashMap<String, String>>, private val imageDataList: ArrayList<HashMap<String, ByteArray>>
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

            name.text = "Name: " + dataItem["name"]
            grade.text = "Grade: " + dataItem["grade"]
            weight.text = "Weight: " + dataItem["weight"]
            date.text = "Date: " + dataItem["date"]

            if (imageDataItem["image"] != null && imageDataItem["image"]!!.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeByteArray(
                    imageDataItem["image"],
                    0,
                    imageDataItem["image"]!!.size
                )
                image.setImageBitmap(bitmap)
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
            args.putByteArray("image", imageDataItem["image"])
            viewImageFragment.arguments = args

            val manager =
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            manager.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
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
                dialog.dismiss()
                val datalist = dataList[holder.adapterPosition]
                val id = datalist["primary"]
                var position = holder.adapterPosition

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

                dataList.removeAt(position)
                notifyItemRemoved(position)
            }

            cancelButton?.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
            return@setOnLongClickListener true
        }
        (holder as GradesAdapter.ViewHolder).bind(position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}