package com.cory.texarkanacollege.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.cory.texarkanacollege.*
import com.cory.texarkanacollege.database.GradesDBHelper
import com.cory.texarkanacollege.fragments.ImageViewFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class GradesAdapter(
    val context: Context,
    private val dataList: ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name = itemView.findViewById<TextView>(R.id.row_name)!!
        var grade = itemView.findViewById<TextView>(R.id.row_grade)!!
        var weight = itemView.findViewById<TextView>(R.id.row_weight)!!
        var date = itemView.findViewById<TextView>(R.id.row_date)!!
        var image = itemView.findViewById<ImageView>(R.id.image)

        fun bind(position: Int) {

            val dataItem = dataList[position]

            val formatter = SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
            val dateFormatted = formatter.format(formatter.parse(dataItem["date"]!!)!!)

            name.text = "Name: " + dataItem["name"]
            grade.text = "Grade: " + dataItem["grade"] + "%"
            weight.text = "Weight: " + dataItem["weight"] + "%"
            date.text = "Date: " + dateFormatted.toString()

            val imgFile = File(dataItem["image"]!!)

            if (imgFile.exists()) {
                val circularProgressDrawable = CircularProgressDrawable(context)
                circularProgressDrawable.strokeWidth = 5f
                circularProgressDrawable.centerRadius = 30f
                circularProgressDrawable.start()

                Glide.with(context)
                    .load(dataItem["image"])
                    .centerCrop()
                    .placeholder(circularProgressDrawable)
                    .into(image)
            } else {
                image.visibility = View.GONE
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.grades_list_item, parent, false)
        )
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataItem = dataList[holder.adapterPosition]

        holder.itemView.findViewById<ImageView>(R.id.image).setOnClickListener {
            val viewImageFragment = ImageViewFragment()
            val args = Bundle()
            val id = dataItem["primary"]
            val classID = dataItem["id"]
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
            val addGradeView =
                LayoutInflater.from(context).inflate(R.layout.options_bottom_sheet, null)
            dialog.setCancelable(false)
            dialog.setContentView(addGradeView)

            val headingTextView = dialog.findViewById<TextView>(R.id.headingTextView)
            headingTextView!!.text = "Options/" + dataItem["name"]
            val editButton = dialog.findViewById<Button>(R.id.editButton)
            val deleteButton = dialog.findViewById<Button>(R.id.deleteButton)
            val deleteAllButton = dialog.findViewById<Button>(R.id.deleteAllButton)
            val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)

            editButton?.setOnClickListener {
                dialog.dismiss()

                val bottomSheetDialog = BottomSheetDialog(context)
                val editGradeBottomSheetView =
                    LayoutInflater.from(context).inflate(R.layout.add_grade_bottom_sheet, null)
                bottomSheetDialog.setCancelable(false)
                bottomSheetDialog.setContentView(editGradeBottomSheetView)

                val editGradeHeadingTextView =
                    editGradeBottomSheetView.findViewById<TextView>(R.id.headingTextView)
                editGradeHeadingTextView.text = "Edit Grade"

                val nameEditText =
                    editGradeBottomSheetView.findViewById<TextInputEditText>(R.id.name)
                val gradeEditText =
                    editGradeBottomSheetView.findViewById<TextInputEditText>(R.id.grade)
                val weightEditText =
                    editGradeBottomSheetView.findViewById<TextInputEditText>(R.id.weight)
                val addImageButton = editGradeBottomSheetView.findViewById<Button>(R.id.addImage)
                val addGradeButton =
                    editGradeBottomSheetView.findViewById<Button>(R.id.addGradeButton)
                val addGradeCancelButton =
                    editGradeBottomSheetView.findViewById<Button>(R.id.cancelButton)

                addGradeButton.text = "Update Grade"
                nameEditText.setText(dataItem["name"])
                gradeEditText.setText(dataItem["grade"])
                weightEditText.setText(dataItem["weight"])

                if (dataItem["image"] != null) {
                    addImageButton.setText("View Image")
                }

                addImageButton.setOnLongClickListener {
                    addImageButton.setText("Add Image")
                    ImagePathData(context).setPath("")
                    Toast.makeText(context, "Image Removed", Toast.LENGTH_SHORT).show()
                    return@setOnLongClickListener true
                }

                addImageButton.setOnClickListener {
                    if (dataItem["image"] != null && addImageButton.text == "View Image" && ImagePathData(context).loadPath() == "") {

                        val viewImageDialog = MaterialAlertDialogBuilder(context)
                        val layout = LayoutInflater.from(context).inflate(R.layout.view_image_layout, null)
                        val imageView = layout.findViewById<ImageView>(R.id.viewImageImageView)
                        val bitmap = BitmapFactory.decodeFile(dataItem["image"])
                        imageView.setImageBitmap(bitmap)
                        viewImageDialog.setPositiveButton("OK", null)

                        viewImageDialog.setView(layout)
                        viewImageDialog.show()
                    }
                    else if (ImagePathData(context).loadPath() == "" && addImageButton.text == "Add Image") {
                        val chooseImageDialog =
                            MaterialAlertDialogBuilder(context).create()
                        val layout =
                            LayoutInflater.from(context).inflate(R.layout.choose_image_dialog, null)
                        chooseImageDialog.setView(layout)

                        val chooseImageButton =
                            layout.findViewById<Button>(R.id.chooseImage)
                        val takePhotoButton =
                            layout.findViewById<Button>(R.id.takePhoto)
                        val cancelButtonImageDialog =
                            layout.findViewById<Button>(R.id.cancelImageDialog)

                        chooseImageButton.setOnClickListener {

                            val pickerIntent = Intent(Intent.ACTION_PICK)
                            pickerIntent.type = "image/*"

                            val saveState = Runnable {
                                (context as MainActivity).showImagePicker.launch(pickerIntent)

                            }

                            MainActivity().runOnUiThread(saveState)

                            addImageButton.text = "View Image"
                            chooseImageDialog.dismiss()
                        }

                        cancelButtonImageDialog.setOnClickListener {
                            chooseImageDialog.dismiss()
                        }

                        chooseImageDialog.show()
                    }
                    else {
                        val viewImageDialog = MaterialAlertDialogBuilder(context)
                        val layout = LayoutInflater.from(context).inflate(R.layout.view_image_layout, null)
                        val imageView = layout.findViewById<ImageView>(R.id.viewImageImageView)
                        val bitmap = BitmapFactory.decodeFile(ImagePathData(context).loadPath())
                        imageView.setImageBitmap(bitmap)
                        viewImageDialog.setPositiveButton("OK", null)

                        viewImageDialog.setView(layout)
                        viewImageDialog.show()
                    }
                }

                addGradeButton.setOnClickListener {
                    val gradesDBHelper = GradesDBHelper(context, null)
                    var image = ""
                    if (ImagePathData(context).loadPath() != "") {
                        image = ImagePathData(context).loadPath()
                    }
                    else {
                        image = dataItem["image"].toString()
                    }
                    gradesDBHelper.update(
                        dataItem["primary"].toString(),
                        nameEditText.text.toString(),
                        gradeEditText.text.toString(),
                        weightEditText.text.toString(),
                        dataItem["date"].toString(),
                        image
                    )
                    dataList.clear()

                    val cursor = GradesDBHelper(context,null).getGrades(ItemID(context).loadPosition().toString())
                    cursor.moveToFirst()

                    while (!cursor.isAfterLast) {
                        val map = HashMap<String, String>()
                        map["primary"] =
                            cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_ID))
                        map["id"] =
                            cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_CLASS_ID))
                        map["name"] =
                            cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_NAME))
                        map["grade"] =
                            cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_GRADE))
                        map["weight"] =
                            cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_WEIGHT))
                        map["date"] =
                            cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_DATE))

                        if (cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_IMAGE)) != null) {
                            map["image"] =
                                cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_IMAGE))

                        }
                        else {
                            map["image"] = ""

                        }

                        dataList.add(map)
                        cursor.moveToNext()
                    }

                   /* val map = HashMap<String, String>()
                    map["primary"] = dataItem["primary"].toString()
                    map["name"] = nameEditText.text.toString()
                    map["grade"] = gradeEditText.text.toString()
                    map["weight"] = weightEditText.text.toString()
                    map["date"] = dataItem["date"].toString()
                    map["image"] = image*/

                    notifyItemRangeChanged(0, dataList.count())
                    bottomSheetDialog.dismiss()
                }

                addGradeCancelButton.setOnClickListener {
                    ImagePathData(context).setPath("")
                    bottomSheetDialog.dismiss()
                }

                bottomSheetDialog.show()
            }

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

                val alertDialog =
                    MaterialAlertDialogBuilder(context, R.style.AlertDialogStyle)
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
        (holder as GradesAdapter.ViewHolder).bind(position)
    }


    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
