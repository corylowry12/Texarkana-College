package com.cory.texarkanacollege.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cory.texarkanacollege.*
import com.cory.texarkanacollege.classes.*
import com.cory.texarkanacollege.database.GradesDBHelper
import com.cory.texarkanacollege.fragments.ImageViewFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class GradesAdapter(
    val context: Context,
    private val dataList: ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var addImageButton: Button

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) {

            val dataItem = dataList[position]

            val formatter = SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
            val dateFormatted = formatter.format(formatter.parse(dataItem["date"]!!)!!)

            itemView.findViewById<TextView>(R.id.row_name)!!.text = "Name: " + dataItem["name"]
            itemView.findViewById<TextView>(R.id.row_weight)!!.text =
                "Weight: " + dataItem["weight"] + "%"
            itemView.findViewById<TextView>(R.id.row_date)!!.text =
                "Date: " + dateFormatted.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.grades_list_item, parent, false)
        )
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataItem = dataList[holder.adapterPosition]

        GlobalScope.launch(Dispatchers.Main) {
            val imgFile = File(dataItem["image"]!!)

            if (imgFile.exists()) {
                holder.itemView.findViewById<ImageView>(R.id.image)!!.visibility = View.VISIBLE
                Glide.with(context)
                    .load(dataItem["image"])
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(holder.itemView.findViewById(R.id.image)!!)
            } else {
                holder.itemView.findViewById<ImageView>(R.id.image)!!.visibility = View.GONE
            }

            val spannable = SpannableString("Grade: " + dataItem["grade"]!!.toString() + "%")
            if (GradesColoredTextView(context).loadGradeColoredTextView()) {
                if (dataItem["grade"]!!.toDouble() >= 90) {
                    spannable.setSpan(
                        ForegroundColorSpan(Color.parseColor("#008631")),
                        "Grade: ".length,
                        "Grade: ".length + dataItem["grade"]!!.toString().length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else if (dataItem["grade"]!!.toDouble() > 80 && dataItem["grade"]!!.toDouble() < 90) {
                    spannable.setSpan(
                        ForegroundColorSpan(Color.parseColor("#FDE227")),
                        "Grade: ".length,
                        "Grade: ".length + dataItem["grade"]!!.toString().length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else if (dataItem["grade"]!!.toDouble() > 70 && dataItem["grade"]!!.toDouble() < 80) {
                    spannable.setSpan(
                        ForegroundColorSpan(Color.parseColor("#F4BC1C")),
                        "Grade: ".length,
                        "Grade: ".length + dataItem["grade"]!!.toString().length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else if (dataItem["grade"]!!.toDouble() > 60 && dataItem["grade"]!!.toDouble() < 70) {
                    spannable.setSpan(
                        ForegroundColorSpan(Color.parseColor("#EE7600")),
                        "Grade: ".length,
                        "Grade: ".length + dataItem["grade"]!!.toString().length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    spannable.setSpan(
                        ForegroundColorSpan(Color.RED),
                        "Grade: ".length,
                        "Grade: ".length + dataItem["grade"]!!.toString().length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            holder.itemView.findViewById<TextView>(R.id.row_grade)!!.text = spannable

            if (GradesIcons(context).loadGradeIcons()) {
                if (dataItem["grade"]!!.toDouble() >= 90) {
                    holder.itemView.findViewById<TextView>(R.id.gradesLetterGradeImageViewText).text =
                        "A"
                    holder.itemView.findViewById<ImageView>(R.id.gradesIconImageView)!!
                        .setBackgroundColor(Color.parseColor("#008631"))
                } else if (dataItem["grade"]!!.toDouble() >= 80 && dataItem["grade"]!!.toDouble() < 90) {
                    holder.itemView.findViewById<TextView>(R.id.gradesLetterGradeImageViewText).text =
                        "B"
                    holder.itemView.findViewById<ImageView>(R.id.gradesIconImageView)!!
                        .setBackgroundColor(Color.parseColor("#FDE227"))
                } else if (dataItem["grade"]!!.toDouble() >= 70 && dataItem["grade"]!!.toDouble() < 80) {
                    holder.itemView.findViewById<TextView>(R.id.gradesLetterGradeImageViewText).text =
                        "C"
                    holder.itemView.findViewById<ImageView>(R.id.gradesIconImageView)!!
                        .setBackgroundColor(Color.parseColor("#F4BC1C"))
                } else if (dataItem["grade"]!!.toDouble() >= 60 && dataItem["grade"]!!.toDouble() < 70) {
                    holder.itemView.findViewById<TextView>(R.id.gradesLetterGradeImageViewText).text =
                        "D"
                    holder.itemView.findViewById<ImageView>(R.id.gradesIconImageView)!!
                        .setBackgroundColor(Color.parseColor("#EE7600"))
                } else {
                    holder.itemView.findViewById<TextView>(R.id.gradesLetterGradeImageViewText).text =
                        "F"
                    holder.itemView.findViewById<ImageView>(R.id.gradesIconImageView)!!
                        .setBackgroundColor(Color.parseColor("#C30010"))
                }
            } else {
                holder.itemView.findViewById<ImageView>(R.id.gradesIconImageView)!!.visibility =
                    View.GONE
                holder.itemView.findViewById<TextView>(R.id.gradesLetterGradeImageViewText).visibility =
                    View.GONE
            }
        }

        holder.itemView.findViewById<ImageView>(R.id.image).setOnClickListener {
            if (!ImageViewIntentData(context).loadImageView()) {
                val viewImageFragment = ImageViewFragment()
                val args = Bundle()
                val id = dataItem["primary"]
                val classID = dataItem["id"]
                args.putInt("image", classID!!.toInt())
                args.putInt("id", id!!.toInt())
                viewImageFragment.arguments = args

                val manager =
                    (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                manager.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                manager.replace(R.id.fragment_container, viewImageFragment).addToBackStack(null)
                manager.commit()
            } else {
                val viewImageActivity = Intent(context, ViewImageActivity::class.java)
                val id = dataItem["primary"]
                val classID = dataItem["id"]
                viewImageActivity.putExtra("image", classID!!.toInt())
                viewImageActivity.putExtra("id", id!!.toInt())
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (context as AppCompatActivity),
                    holder.itemView.findViewById<ImageView>(R.id.image),
                    "transition_image"
                )
                context.startActivity(viewImageActivity, options.toBundle())
            }

        }

        holder.itemView.findViewById<ConstraintLayout>(R.id.gradesItemConstraintLayout)
            .setOnLongClickListener {

                val dialog = BottomSheetDialog(context)
                val addGradeView =
                    LayoutInflater.from(context).inflate(R.layout.options_bottom_sheet, null)
                dialog.setCancelable(false)
                dialog.setContentView(addGradeView)

                if (context.resources.getBoolean(R.bool.isTablet)) {
                    val bottomSheet =
                        dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                    val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    bottomSheetBehavior.skipCollapsed = true
                    bottomSheetBehavior.isHideable = false
                    bottomSheetBehavior.isDraggable = false
                }

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
                    addImageButton =
                        editGradeBottomSheetView.findViewById<Button>(R.id.addImage)
                    val addGradeButton =
                        editGradeBottomSheetView.findViewById<Button>(R.id.addGradeButton)
                    val addGradeCancelButton =
                        editGradeBottomSheetView.findViewById<Button>(R.id.cancelButton)

                    ImagePathData(context).setPath(dataItem["image"].toString())

                    addGradeButton.text = "Update Grade"
                    nameEditText.setText(dataItem["name"])
                    gradeEditText.setText(dataItem["grade"])
                    weightEditText.setText(dataItem["weight"])

                    if (dataItem["image"] != "") {
                        addImageButton.text = "View Image"
                    }

                    addImageButton.setOnLongClickListener LongClickListener@{
                        if (ImagePathData(context).loadPath() != "") {
                            addImageButton.text = "Add Image"
                            ImagePathData(context).setPath("")
                            Toast.makeText(context, "Image Removed", Toast.LENGTH_SHORT).show()
                            return@LongClickListener true
                        }
                        false
                    }

                    addImageButton.setOnClickListener {

                        if (!(context as MainActivity).checkPermissions()) {

                            val requestPermissions = Runnable {
                                context.requestPermissions()

                            }

                            MainActivity().runOnUiThread(requestPermissions)
                        } else {
                            if (dataItem["image"] != null && addImageButton.text == "View Image" && ImagePathData(
                                    context
                                ).loadPath() == ""
                            ) {

                                val viewImageDialog =
                                    MaterialAlertDialogBuilder(context, R.style.AlertDialogStyle).create()
                                val layout = LayoutInflater.from(context)
                                    .inflate(R.layout.view_image_layout, null)
                                val imageView =
                                    layout.findViewById<ImageView>(R.id.viewImageImageView)

                                val okButton = layout.findViewById<Button>(R.id.okButton)
                                val bitmap = File(dataItem["image"]!!)
                                Glide.with(context)
                                    .load(bitmap)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(imageView)

                                okButton.setOnClickListener {
                                    viewImageDialog.dismiss()
                                }

                                viewImageDialog.setView(layout)
                                viewImageDialog.show()
                            } else if (ImagePathData(context).loadPath() == "" && addImageButton.text == "Add Image") {
                                val chooseImageDialog =
                                    MaterialAlertDialogBuilder(
                                        context,
                                        R.style.AlertDialogStyle
                                    ).create()
                                val layout =
                                    LayoutInflater.from(context)
                                        .inflate(R.layout.choose_image_dialog, null)
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
                                        context.showImagePicker.launch(
                                            pickerIntent
                                        )

                                    }
                                    MainActivity().runOnUiThread(saveState)

                                    chooseImageDialog.dismiss()
                                }

                                takePhotoButton.setOnClickListener {

                                    val saveState = Runnable {
                                        context.camera()

                                    }
                                    MainActivity().runOnUiThread(saveState)

                                    chooseImageDialog.dismiss()
                                }

                                cancelButtonImageDialog.setOnClickListener {
                                    chooseImageDialog.dismiss()
                                }

                                chooseImageDialog.show()
                            } else {
                                val viewImageDialog =
                                    MaterialAlertDialogBuilder(context, R.style.AlertDialogStyle).create()
                                val layout = LayoutInflater.from(context)
                                    .inflate(R.layout.view_image_layout, null)
                                val imageView =
                                    layout.findViewById<ImageView>(R.id.viewImageImageView)
                                val bitmap = File(ImagePathData(context).loadPath())

                                val okButton = layout.findViewById<Button>(R.id.okButton)

                                Glide.with(context)
                                    .load(bitmap)
                                    .centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(imageView)

                                okButton.setOnClickListener {
                                    viewImageDialog.dismiss()
                                }

                                viewImageDialog.setView(layout)
                                viewImageDialog.show()
                            }
                        }
                    }

                    addGradeButton.setOnClickListener {
                        val gradesDBHelper = GradesDBHelper(context, null)
                        var image = ""

                        if (dataItem["image"] != null && addImageButton.text == "View Image" && ImagePathData(
                                context
                            ).loadPath() == ""
                        ) {

                            image = dataItem["image"]!!
                        } else {
                            image = ImagePathData(context).loadPath()
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

                        val cursor = GradesDBHelper(context, null).getGrades(
                            ItemID(context).loadPosition().toString()
                        )
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

                            } else {
                                map["image"] = ""

                            }
                            dataList.add(map)

                            cursor.moveToNext()
                        }

                        notifyItemChanged(holder.adapterPosition)
                        bottomSheetDialog.dismiss()
                    }

                    addGradeCancelButton.setOnClickListener {
                        ImagePathData(context).setPath("")
                        bottomSheetDialog.dismiss()
                    }

                    bottomSheetDialog.show()
                }

                deleteButton?.setOnClickListener {

                    val materialAlertDialogBuilder =
                        MaterialAlertDialogBuilder(
                            context,
                            R.style.AlertDialogStyle
                        ).create()

                    val layout =
                        LayoutInflater.from(context)
                            .inflate(R.layout.delete_grades_dialog_layout, null)
                    materialAlertDialogBuilder.setView(layout)

                    val deleteGradeDialogButton =
                        layout.findViewById<Button>(R.id.deleteGradeDialogDeleteButton)
                    val deleteGradeCancelButton =
                        layout.findViewById<Button>(R.id.cancelDeleteGradeDialog)
                   deleteGradeDialogButton.setOnClickListener {
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
                       materialAlertDialogBuilder.dismiss()

                        val saveState = Runnable {
                            (context as MainActivity).textViewVisibilityGrades()

                        }

                        MainActivity().runOnUiThread(saveState)
                    }
                    deleteGradeCancelButton.setOnClickListener {
                        materialAlertDialogBuilder.dismiss()
                    }
                    materialAlertDialogBuilder.show()
                }

                deleteAllButton?.setOnClickListener {
                    val datalist = dataList[holder.adapterPosition]

                    val gradesDBHelper = GradesDBHelper(context, null)

                    val alertDialog =
                        MaterialAlertDialogBuilder(
                            context,
                            R.style.AlertDialogStyle
                        ).create()

                    val layout =
                        LayoutInflater.from(context)
                            .inflate(R.layout.delete_all_grades_dialog_layout, null)
                    alertDialog.setView(layout)

                    val deleteAllDialogButton =
                        layout.findViewById<Button>(R.id.deleteAllDialogDeleteButton)
                    val deleteAllCancelButton =
                        layout.findViewById<Button>(R.id.cancelDeleteAllDialog)

                    deleteAllDialogButton.setOnClickListener {

                        for (i in 0 until dataList.count()) {
                            gradesDBHelper.deleteAllGradesForOneClass(datalist["id"]!!)
                        }
                        dataList.clear()

                        dialog.dismiss()

                        val runnable = Runnable {
                            (context as MainActivity).deleteAllGrades()
                        }
                        MainActivity().runOnUiThread(runnable)

                        alertDialog.dismiss()
                    }

                    deleteAllCancelButton.setOnClickListener {
                        alertDialog.dismiss()
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

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}