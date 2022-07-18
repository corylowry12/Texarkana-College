package com.cory.texarkanacollege.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.ClassIcons
import com.cory.texarkanacollege.classes.ColoredClassGradeTextView
import com.cory.texarkanacollege.classes.ItemID
import com.cory.texarkanacollege.database.AssignmentsDBHelper
import com.cory.texarkanacollege.database.ClassesDBHelper
import com.cory.texarkanacollege.database.GradesDBHelper
import com.cory.texarkanacollege.fragments.GradeFragment
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.suke.widget.SwitchButton
import java.math.RoundingMode

class ClassesAdapter(
    val context: Context,
    private val dataList: ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title = itemView.findViewById<TextView>(R.id.row_class)!!
        var classTime = itemView.findViewById<TextView>(R.id.row_class_time)!!

        @SuppressLint("Range")
        fun bind(position: Int) {

            val dataItem = dataList[position]
            title.text = "Class Name: " + dataItem["className"]
            classTime.text = "Class Time: " + dataItem["classTime"]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.classes_list_item, parent, false)
        )
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        val dataItem = dataList[holder.adapterPosition]

        val classesIconImageView = holder.itemView.findViewById<ImageView>(R.id.classesIconImageView)!!
        val classesIconTextView = holder.itemView.findViewById<TextView>(R.id.classesTitleImageViewText)!!

        if (ClassIcons(context).loadClassIcons()) {
            val firstLetter = dataItem["className"].toString()[0].toString().uppercase()
            when (firstLetter) {
                "A" -> {
                    classesIconImageView.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.blue
                        )
                    )
                }
                "B" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#228C22"))
                }
                "C" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#80604D"))
                }
                "D" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#77C3EC"))
                }
                "E" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#808080"))
                }
                "F" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#00FFFF"))
                }
                "G" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#FFC0CB"))
                }
                "H" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#FF0000"))
                }
                "I" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#FF4500"))
                }
                "J" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#FFD700"))
                }
                "K" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#FF8C00"))
                }
                "L" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#FF6347"))
                }
                "M" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#FF00FF"))
                }
                "N" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#800080"))
                }
                "O" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#4B0082"))
                }
                "P" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#8A2BE2"))
                }
                "Q" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#8FBC8F"))
                }
                "R" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#62b512"))
                }
                "S" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#2E8B57"))
                }
                "T" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#6B8E23"))
                }
                "U" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#20B2AA"))
                }
                "V" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#2093C3"))
                }
                "W" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#336296"))
                }
                "X" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#7179BA"))
                }
                "Y" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#65C9D4"))
                }
                "Z" -> {
                    classesIconImageView.setBackgroundColor(Color.parseColor("#000000"))
                }
                else -> {
                    classesIconImageView.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.blue
                        )
                    )
                }
            }
            classesIconTextView.text = firstLetter
        } else {
            classesIconTextView.visibility = View.GONE
            classesIconImageView.visibility = View.GONE
        }

        val gradesDBHandler = GradesDBHelper(context, null)

        var grades = 0.0
        var weights = 0.0

        val classAverage = holder.itemView.findViewById<TextView>(R.id.row_class_average)

        var cursor = GradesDBHelper(context, null).getGrades(dataItem["id"].toString())

        holder.itemView.findViewById<TextView>(R.id.gradeCountTextView).text = cursor.count.toString()
        cursor.moveToFirst()

        while (!cursor.isAfterLast) {
            val map = HashMap<String, String>()
            map["id"] = cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_ID))
            map["grade"] = cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_GRADE))
            map["weight"] = cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_WEIGHT))
            grades += map["grade"]!!.toDouble() * map["weight"]!!.toDouble()
            weights += map["weight"]!!.toDouble()

            cursor.moveToNext()

        }

        if ((grades > 0.0 && weights > 0.0) || cursor.count > 0) {
            val average =
                (grades / weights).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()
            val spannable = SpannableString("Class Average: " + average.toString() + "%")
            if (ColoredClassGradeTextView(context).loadColoredClassTextView() == 0) {
                if (average >= 90) {

                    spannable.setSpan(
                        ForegroundColorSpan(Color.parseColor("#008631")),
                        "Class Average: ".length,
                        "Class Average: ".length + average.toString().length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else if (average > 80 && average < 90) {
                    spannable.setSpan(
                        ForegroundColorSpan(Color.parseColor("#FDE227")),
                        "Class Average: ".length,
                        "Class Average: ".length + average.toString().length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else if (average > 70 && average < 80) {
                    spannable.setSpan(
                        ForegroundColorSpan(Color.parseColor("#F4BC1C")),
                        "Class Average: ".length,
                        "Class Average: ".length + average.toString().length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else if (average > 60 && average < 70) {
                    spannable.setSpan(
                        ForegroundColorSpan(Color.parseColor("#EE7600")),
                        "Class Average: ".length,
                        "Class Average: ".length + average.toString().length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    spannable.setSpan(
                        ForegroundColorSpan(Color.RED),
                        "Class Average: ".length,
                        "Class Average: ".length + average.toString().length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            } else if (ColoredClassGradeTextView(context).loadColoredClassTextView() == 1) {
                if (average >= 90) {
                    holder.itemView.findViewById<CardView>(R.id.cardViewClassItem)
                        .setCardBackgroundColor(Color.parseColor("#008631"))
                } else if (average >= 80 && average < 90) {
                    holder.itemView.findViewById<CardView>(R.id.cardViewClassItem)
                        .setCardBackgroundColor(Color.parseColor("#FDE227"))
                } else if (average >= 70 && average < 80) {
                    holder.itemView.findViewById<CardView>(R.id.cardViewClassItem)
                        .setCardBackgroundColor(Color.parseColor("#F4BC1C"))
                } else if (average >= 60 && average < 70) {
                    holder.itemView.findViewById<CardView>(R.id.cardViewClassItem)
                        .setCardBackgroundColor(Color.parseColor("#EE7600"))
                } else if (average < 60) {
                    holder.itemView.findViewById<CardView>(R.id.cardViewClassItem)
                        .setCardBackgroundColor(Color.RED)
                }
                else {
                    holder.itemView.findViewById<CardView>(R.id.cardViewClassItem)
                        .setCardBackgroundColor(ContextCompat.getColor(context, R.color.cardViewLightBackgroundColor))
                }
            }
            classAverage.text = spannable
        } else {
            classAverage.text = "Class Average: 0.0%"
            holder.itemView.findViewById<CardView>(R.id.cardViewClassItem)
                .setCardBackgroundColor(ContextCompat.getColor(context, R.color.cardViewLightBackgroundColor))
        }

        holder.itemView.findViewById<ConstraintLayout>(R.id.classesItemConstraintLayout)
            .setOnClickListener {

                val saveState = Runnable {
                    (context as MainActivity).hideKeyboardClasses()

                }

                MainActivity().runOnUiThread(saveState)

                val itemPositionData = ItemID(context)
                itemPositionData.setPosition(dataItem["id"]!!.toInt())
                val gradeFragment = GradeFragment()
                (context as MainActivity).gradeFragment = gradeFragment
                val args = Bundle()
                args.putString("className", dataItem["className"])
                gradeFragment.arguments = args
                val manager =
                    (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                manager.setCustomAnimations(
                    R.anim.slide_in_fragment,
                    R.anim.fade_out_fragment,
                    R.anim.fade_in_fragment,
                    R.anim.slide_out_fragment
                )
                manager.replace(R.id.fragment_container, gradeFragment).addToBackStack(null)
                manager.commit()
            }

        holder.itemView.findViewById<ConstraintLayout>(R.id.classesItemConstraintLayout)
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
                headingTextView!!.text = "Options/" + dataItem["className"]
                val editButton = dialog.findViewById<Button>(R.id.editButton)
                val deleteButton = dialog.findViewById<Button>(R.id.deleteButton)
                val deleteAllButton = dialog.findViewById<Button>(R.id.deleteAllButton)
                val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)

                editButton?.setOnClickListener {
                    dialog.dismiss()
                    val daysArray: MutableList<Int> = ArrayList()

                    val bottomSheetDialog = BottomSheetDialog(context)
                    val addClassBottomSheetView =
                        LayoutInflater.from(context).inflate(R.layout.add_class_bottom_sheet, null)
                    bottomSheetDialog.setCancelable(false)
                    bottomSheetDialog.setContentView(addClassBottomSheetView)
                    val textViewHeading =
                        bottomSheetDialog.findViewById<TextView>(R.id.headingTextView)
                    val nameEditTextAddClass =
                        bottomSheetDialog.findViewById<TextInputEditText>(R.id.name)
                    val cancelButtonAddClass =
                        bottomSheetDialog.findViewById<Button>(R.id.cancelButton)
                    val addClassButtonAddClass =
                        bottomSheetDialog.findViewById<Button>(R.id.addClassButton)
                    val netClassSwitchAddClass =
                        bottomSheetDialog.findViewById<SwitchButton>(R.id.netClassSwitch)
                    val netClassSwitchConstraint =
                        bottomSheetDialog.findViewById<ConstraintLayout>(R.id.switchConstraintLayout)
                    val toggleGroupAddClass =
                        bottomSheetDialog.findViewById<MaterialButtonToggleGroup>(
                            R.id.toggleGroup
                        )

                    val mon = bottomSheetDialog.findViewById<MaterialButton>(R.id.mon)
                    val tue = bottomSheetDialog.findViewById<MaterialButton>(R.id.tue)
                    val wed = bottomSheetDialog.findViewById<MaterialButton>(R.id.wed)
                    val thur = bottomSheetDialog.findViewById<MaterialButton>(R.id.thur)
                    val fri = bottomSheetDialog.findViewById<MaterialButton>(R.id.fri)

                    addClassButtonAddClass?.text = "Update Class"
                    textViewHeading?.text = "Update Class"
                    val dataItemAddClass = dataList[holder.adapterPosition]
                    nameEditTextAddClass?.setText(dataItemAddClass["className"])
                    if (dataItemAddClass["classTime"] == "Web") {
                        netClassSwitchAddClass?.isChecked = true
                        toggleGroupAddClass?.visibility = View.GONE
                    }
                    if (dataItemAddClass["classTime"].toString().contains("Mon")) {
                        mon?.isChecked = true
                        mon?.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.toggleButtonCheckedBackground
                            )
                        )
                        daysArray.add(1)
                    }
                    if (dataItemAddClass["classTime"].toString().contains("Tue")) {
                        tue?.isChecked = true
                        tue?.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.toggleButtonCheckedBackground
                            )
                        )
                        daysArray.add(2)
                    }
                    if (dataItemAddClass["classTime"].toString().contains("Wed")) {
                        wed?.isChecked = true
                        wed?.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.toggleButtonCheckedBackground
                            )
                        )
                        daysArray.add(3)
                    }
                    if (dataItemAddClass["classTime"].toString().contains("Thur")) {
                        thur?.isChecked = true
                        thur?.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.toggleButtonCheckedBackground
                            )
                        )
                        daysArray.add(4)
                    }
                    if (dataItemAddClass["classTime"].toString().contains("Fri")) {
                        fri?.isChecked = true
                        fri?.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.toggleButtonCheckedBackground
                            )
                        )
                        daysArray.add(5)
                    }

                    mon?.setOnClickListener {
                        if (mon.isChecked) {
                            daysArray.add(1)
                            mon.setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.toggleButtonCheckedBackground
                                )
                            )
                        } else {
                            daysArray.sort()
                            daysArray.remove(1)
                            mon.setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.transparent
                                )
                            )
                        }
                    }

                    tue?.setOnClickListener {
                        if (tue.isChecked) {
                            daysArray.add(2)
                            tue.setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.toggleButtonCheckedBackground
                                )
                            )
                        } else {
                            daysArray.sort()
                            daysArray.remove(2)
                            tue.setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.transparent
                                )
                            )
                        }
                    }

                    wed?.setOnClickListener {
                        if (wed.isChecked) {
                            daysArray.add(3)
                            wed.setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.toggleButtonCheckedBackground
                                )
                            )
                        } else {
                            daysArray.sort()
                            daysArray.remove(3)
                            wed.setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.transparent
                                )
                            )
                        }
                    }

                    thur?.setOnClickListener {
                        if (thur.isChecked) {
                            daysArray.add(4)
                            thur.setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.toggleButtonCheckedBackground
                                )
                            )
                        } else {
                            daysArray.sort()
                            daysArray.remove(4)
                            thur.setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.transparent
                                )
                            )
                        }
                    }

                    fri?.setOnClickListener {
                        if (fri.isChecked) {
                            daysArray.add(5)
                            fri.setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.toggleButtonCheckedBackground
                                )
                            )
                        } else {
                            daysArray.sort()
                            daysArray.remove(5)
                            fri.setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.transparent
                                )
                            )
                        }
                    }

                    netClassSwitchAddClass?.setOnCheckedChangeListener { compoundButton, b ->
                        if (b) {
                            toggleGroupAddClass?.visibility = View.GONE
                        } else {
                            toggleGroupAddClass?.visibility = View.VISIBLE
                        }
                    }

                    netClassSwitchConstraint?.setOnClickListener {
                        netClassSwitchAddClass?.isChecked = !netClassSwitchAddClass!!.isChecked
                        if (netClassSwitchAddClass.isChecked) {
                            toggleGroupAddClass?.visibility = View.GONE
                        } else {
                            toggleGroupAddClass?.visibility = View.VISIBLE
                        }
                    }

                    var days = ""
                    addClassButtonAddClass?.setOnClickListener {
                        daysArray.sort()
                        val text = nameEditTextAddClass?.text
                        val textString = text.toString()
                        if (nameEditTextAddClass?.text == null || textString == "") {
                            Toast.makeText(
                                context,
                                "Class Name is required",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (netClassSwitchAddClass!!.isChecked) {
                                days = "Web"
                            } else if (!netClassSwitchAddClass.isChecked && daysArray.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Must select a day of the week or web course",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (!netClassSwitchAddClass.isChecked && daysArray.isNotEmpty()) {
                                for (i in 0 until daysArray.count()) {
                                    if (daysArray[i] == 1) {
                                        days += "Mon"
                                    }
                                    if (daysArray[i] == 2) {
                                        days += if (daysArray.count() == 1) {
                                            "Tue"
                                        } else {
                                            when (i) {
                                                daysArray.count() - 1 -> {
                                                    " and Tue"
                                                }
                                                0 -> {
                                                    "Tue"
                                                }
                                                else -> {
                                                    ", Tue"
                                                }
                                            }
                                        }
                                    }
                                    if (daysArray[i] == 3) {
                                        if (daysArray.count() == 1) {
                                            days += "Wed"
                                        } else {
                                            days += when (i) {
                                                daysArray.count() - 1 -> {
                                                    " and Wed"
                                                }
                                                0 -> {
                                                    "Wed"
                                                }
                                                else -> {
                                                    ", Wed"
                                                }
                                            }
                                        }
                                    }
                                    if (daysArray[i] == 4) {
                                        days += if (daysArray.count() == 1) {
                                            "Thur"
                                        } else {
                                            when (i) {
                                                daysArray.count() - 1 -> {
                                                    " and Thur"
                                                }
                                                0 -> {
                                                    "Thur"
                                                }
                                                else -> {
                                                    ", Thur"
                                                }
                                            }
                                        }
                                    }
                                    if (daysArray[i] == 5) {
                                        days += if (daysArray.count() == 1) {
                                            "Fri"
                                        } else {
                                            if (daysArray.count() == 2) {
                                                " and Fri"
                                            } else {
                                                if (i == 0) {
                                                    "Fri"
                                                } else {
                                                    ", and Fri"
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (textString != "" && (daysArray.isNotEmpty() || netClassSwitchAddClass.isChecked)) {
                                val classDBHelper = ClassesDBHelper(context, null)
                                classDBHelper.update(
                                    dataItemAddClass["id"].toString(),
                                    nameEditTextAddClass.text.toString(),
                                    days
                                )
                                AssignmentsDBHelper(
                                    context,
                                    null
                                ).updateOnClassChange(
                                    dataItem["className"].toString(),
                                    nameEditTextAddClass.text.toString()
                                )
                                dataList.clear()
                                cursor = classDBHelper.getAllRow()!!
                                cursor.moveToFirst()

                                if (classDBHelper.getCount() > 0) {
                                    while (!cursor.isAfterLast) {
                                        val map = HashMap<String, String>()
                                        map["id"] =
                                            cursor.getString(cursor.getColumnIndex(ClassesDBHelper.COLUMN_ID))
                                        map["className"] =
                                            cursor.getString(cursor.getColumnIndex(ClassesDBHelper.COLUMN_CLASS_NAME))
                                        map["classTime"] =
                                            cursor.getString(cursor.getColumnIndex(ClassesDBHelper.COLUMN_CLASS_TIME))
                                        dataList.add(map)

                                        cursor.moveToNext()

                                    }
                                }
                                notifyItemRangeChanged(0, dataList.count())

                                bottomSheetDialog.dismiss()
                                days = ""
                            }
                        }
                    }
                    cancelButtonAddClass?.setOnClickListener {
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
                            .inflate(R.layout.delete_class_dialog_layout, null)
                    materialAlertDialogBuilder.setView(layout)

                    val deleteClassDeleteButton = layout.findViewById<Button>(R.id.deleteClassDialogDeleteButton)
                    val deleteClassCancelButton = layout.findViewById<Button>(R.id.cancelDeleteClassDialog)

                    deleteClassDeleteButton.setOnClickListener {
                        val datalist = dataList[holder.adapterPosition]
                        val id = datalist["id"]

                        val classesDBHelper = ClassesDBHelper(context, null)
                        val gradesDBHelper = GradesDBHelper(context, null)
                        AssignmentsDBHelper(
                            context,
                            null
                        ).deleteOnClassDeletion(dataItem["className"].toString())

                        val mapGrades = HashMap<String, String>()
                        val cursorGrades = gradesDBHandler.getGrades(id.toString())
                        cursorGrades.moveToFirst()
                        if (cursorGrades.count > 0) {

                            while (!cursorGrades.isAfterLast) {

                                mapGrades["id"] =
                                    cursorGrades.getString(
                                        cursorGrades.getColumnIndex(
                                            GradesDBHelper.COLUMN_CLASS_ID
                                        )
                                    )
                                mapGrades["grades"] =
                                    cursorGrades.getString(
                                        cursorGrades.getColumnIndex(
                                            GradesDBHelper.COLUMN_GRADE
                                        )
                                    )

                                gradesDBHelper.deleteRow(mapGrades["id"].toString())
                                cursorGrades.moveToNext()
                            }
                        }

                        val mapClasses = HashMap<String, String>()
                        val cursorClasses = classesDBHelper.getRow(id.toString())
                        cursorClasses.moveToFirst()
                        if (cursorClasses.count > 0) {

                            while (!cursorClasses.isAfterLast) {

                                mapClasses["id"] =
                                    cursorClasses.getString(
                                        cursorClasses.getColumnIndex(
                                            ClassesDBHelper.COLUMN_ID
                                        )
                                    )
                                mapClasses["grades"] =
                                    cursorClasses.getString(
                                        cursorClasses.getColumnIndex(
                                            ClassesDBHelper.COLUMN_CLASS_NAME
                                        )
                                    )


                                classesDBHelper.deleteRow(mapClasses["id"].toString())
                                cursorClasses.moveToNext()
                            }
                        }

                        dataList.removeAt(holder.adapterPosition)
                        notifyItemRemoved(holder.adapterPosition)
                        dialog.dismiss()
                        materialAlertDialogBuilder.dismiss()

                        val saveState = Runnable {
                            (context as MainActivity).textViewVisibilityClasses()

                        }

                        MainActivity().runOnUiThread(saveState)
                    }
                    deleteClassCancelButton.setOnClickListener {
                        materialAlertDialogBuilder.dismiss()
                    }
                    materialAlertDialogBuilder.show()
                }

                deleteAllButton?.setOnClickListener {
                    val classesDBHelper = ClassesDBHelper(context, null)
                    val gradesDBHelper = GradesDBHelper(context, null)
                    val assignmentsDBHelper = AssignmentsDBHelper(context, null)

                    if (classesDBHelper.getCount() > 0) {
                        val alertDialog =
                            MaterialAlertDialogBuilder(
                                context,
                                R.style.AlertDialogStyle
                            ).create()

                        val layout =
                            LayoutInflater.from(context)
                                .inflate(R.layout.delete_all_classes_dialog_layout, null)
                        alertDialog.setView(layout)

                        val deleteAllClassesDeleteButton = layout.findViewById<Button>(R.id.deleteAllClassesDialogDeleteButton)
                        val deleteAllClassesCancelButton = layout.findViewById<Button>(R.id.cancelDeleteAllClassesDialog)

                        deleteAllClassesDeleteButton.setOnClickListener {
                            dialog.dismiss()
                            classesDBHelper.deleteAll()
                            gradesDBHelper.deleteAll()
                            assignmentsDBHelper.deleteAll()
                            dataList.clear()
                            alertDialog.dismiss()
                            val runnable = Runnable {
                                (context as MainActivity).deleteAll()
                            }
                            MainActivity().runOnUiThread(runnable)
                        }
                        deleteAllClassesCancelButton.setOnClickListener {
                            alertDialog.dismiss()
                        }
                        alertDialog.show()
                    } else {
                        Toast.makeText(context, context.getString(R.string.there_are_no_classes_stored), Toast.LENGTH_SHORT)
                            .show()
                    }
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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }
}