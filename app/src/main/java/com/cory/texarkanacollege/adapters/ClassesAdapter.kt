package com.cory.texarkanacollege.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.ItemID
import com.cory.texarkanacollege.database.AssignmentsDBHelper
import com.cory.texarkanacollege.database.ClassesDBHelper
import com.cory.texarkanacollege.database.GradesDBHelper
import com.cory.texarkanacollege.fragments.GradeFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.suke.widget.SwitchButton
import de.hdodenhof.circleimageview.CircleImageView
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ClassesAdapter(
    val context: Context,
    private val dataList: ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title = itemView.findViewById<TextView>(R.id.row_class)!!
        var classTime = itemView.findViewById<TextView>(R.id.row_class_time)
        val classesIconImageView = itemView.findViewById<ImageView>(R.id.classesIconImageView)
        val classesIconTextView = itemView.findViewById<TextView>(R.id.classesTitleImageViewText)

        fun bind(position: Int) {

            val dataItem = dataList[position]

            val firstLetter = dataItem["className"].toString()[0].toString().uppercase()
            if (firstLetter == "A") {
                classesIconImageView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
            }
            else if (firstLetter == "B") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#228C22"))
            }
            else if (firstLetter == "C") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#80604D"))
            }
            else if (firstLetter == "D") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#77C3EC"))
            }
            else if (firstLetter == "E") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#808080"))
            }
            else if (firstLetter == "F") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#00FFFF"))
            }
            else if (firstLetter == "G") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#FFC0CB"))
            }
            else if (firstLetter == "H") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#FF0000"))
            }
            else if (firstLetter == "I") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#FF4500"))
            }
            else if (firstLetter == "J") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#FFD700"))
            }
            else if (firstLetter == "K") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#FF8C00"))
            }
            else if (firstLetter == "L") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#FF6347"))
            }
            else if (firstLetter == "M") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#FF00FF"))
            }
            else if (firstLetter == "N") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#800080"))
            }
            else if (firstLetter == "O") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#4B0082"))
            }
            else if (firstLetter == "P") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#8A2BE2"))
            }
            else if (firstLetter == "Q") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#8FBC8F"))
            }
            else if (firstLetter == "R") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#7CF00"))
            }
            else if (firstLetter == "S") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#2E8B57"))
            }
            else if (firstLetter == "T") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#6B8E23"))
            }
            else if (firstLetter == "U") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#20B2AA"))
            }
            else if (firstLetter == "V") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#2093C3"))
            }
            else if (firstLetter == "W") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#336296"))
            }
            else if (firstLetter == "X") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#7179BA"))
            }
            else if (firstLetter == "Y") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#65C9D4"))
            }
            else if (firstLetter == "Z") {
                classesIconImageView.setBackgroundColor(Color.parseColor("#000000"))
            }
            else {
                classesIconImageView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
            }

            title.text = "Class Name: " + dataItem["className"]
            classTime.text = "Class Time: " + dataItem["classTime"]

            classesIconTextView.text = firstLetter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.classes_list_item, parent, false)
        )
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val classAverage = holder.itemView.findViewById<TextView>(R.id.row_class_average)
        val gradesDBHandler = GradesDBHelper(context, null)

        var grades = 0.0
        var weights = 0.0
        val dataItem = dataList[holder.adapterPosition]

        var cursor = GradesDBHelper(context, null).getGrades(dataItem["id"].toString())
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
        if (grades > 0.0 && weights > 0.0) {
            val average =
                (grades / weights).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()
            val spannable = SpannableString("Class Average: " + average.toString() + "%")
            if (average >= 90) {
                spannable.setSpan(
                    ForegroundColorSpan(Color.parseColor("#46ad21")),
                    "Class Average: ".length,
                    "Class Average: ".length + average.toString().length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                classAverage.text = spannable
            } else if (average > 80 && average < 90) {
                spannable.setSpan(
                    ForegroundColorSpan(Color.YELLOW),
                    "Class Average: ".length,
                    "Class Average: ".length + average.toString().length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                classAverage.text = spannable
            } else if (average > 70 && average < 80) {
                spannable.setSpan(
                    ForegroundColorSpan(Color.parseColor("#FFA500")),
                    "Class Average: ".length,
                    "Class Average: ".length + average.toString().length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                classAverage.text = spannable
            } else {
                spannable.setSpan(
                    ForegroundColorSpan(Color.RED),
                    "Class Average: ".length,
                    "Class Average: ".length + average.toString().length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                classAverage.text = spannable
            }
        } else {
            classAverage.text = "Class Average: 0.0%"
        }

        holder.itemView.setOnClickListener {

            val itemPositionData = ItemID(context)
            itemPositionData.setPosition(dataItem["id"]!!.toInt())
            val gradeFragment = GradeFragment()
            (context as MainActivity).gradeFragment = gradeFragment
            val args = Bundle()
            args.putString("className", dataItem["className"])
            gradeFragment.arguments = args
            val manager =
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            manager.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            manager.replace(R.id.fragment_container, gradeFragment).addToBackStack(null)
            manager.commit()
        }

        holder.itemView.setOnLongClickListener {

            val dialog = BottomSheetDialog(context)
            val addGradeView =
                LayoutInflater.from(context).inflate(R.layout.options_bottom_sheet, null)
            dialog.setCancelable(false)
            dialog.setContentView(addGradeView)

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
                val textViewHeading = bottomSheetDialog.findViewById<TextView>(R.id.headingTextView)
                val nameEditTextAddClass =
                    bottomSheetDialog.findViewById<TextInputEditText>(R.id.name)
                val cancelButtonAddClass = bottomSheetDialog.findViewById<Button>(R.id.cancelButton)
                val addClassButtonAddClass =
                    bottomSheetDialog.findViewById<Button>(R.id.addClassButton)
                val netClassSwitchAddClass =
                    bottomSheetDialog.findViewById<SwitchButton>(R.id.netClassSwitch)
                val netClassSwitchConstraint = bottomSheetDialog.findViewById<ConstraintLayout>(R.id.switchConstraintLayout)
                val toggleGroupAddClass = bottomSheetDialog.findViewById<MaterialButtonToggleGroup>(
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
                    mon?.setBackgroundColor(ContextCompat.getColor(context, R.color.toggleButtonCheckedBackground))
                    daysArray.add(1)
                }
                if (dataItemAddClass["classTime"].toString().contains("Tue")) {
                    tue?.isChecked = true
                    tue?.setBackgroundColor(ContextCompat.getColor(context, R.color.toggleButtonCheckedBackground))
                    daysArray.add(2)
                }
                if (dataItemAddClass["classTime"].toString().contains("Wed")) {
                    wed?.isChecked = true
                    wed?.setBackgroundColor(ContextCompat.getColor(context, R.color.toggleButtonCheckedBackground))
                    daysArray.add(3)
                }
                if (dataItemAddClass["classTime"].toString().contains("Thur")) {
                    thur?.isChecked = true
                    thur?.setBackgroundColor(ContextCompat.getColor(context, R.color.toggleButtonCheckedBackground))
                    daysArray.add(4)
                }
                if (dataItemAddClass["classTime"].toString().contains("Fri")) {
                    fri?.isChecked = true
                    fri?.setBackgroundColor(ContextCompat.getColor(context, R.color.toggleButtonCheckedBackground))
                    daysArray.add(5)
                }

                mon?.setOnClickListener {
                    if (mon.isChecked) {
                        daysArray.add(1)
                        mon.setBackgroundColor(ContextCompat.getColor(context, R.color.toggleButtonCheckedBackground))
                    }
                    else {
                        daysArray.sort()
                        daysArray.remove(1)
                        mon.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
                    }
                }

                tue?.setOnClickListener {
                    if (tue.isChecked) {
                        daysArray.add(2)
                        tue.setBackgroundColor(ContextCompat.getColor(context, R.color.toggleButtonCheckedBackground))
                    }
                    else {
                        daysArray.sort()
                        daysArray.remove(2)
                        tue.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
                    }
                }

                wed?.setOnClickListener {
                    if (wed.isChecked) {
                        daysArray.add(3)
                        wed.setBackgroundColor(ContextCompat.getColor(context, R.color.toggleButtonCheckedBackground))
                    }
                    else {
                        daysArray.sort()
                        daysArray.remove(3)
                        wed.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
                    }
                }

                thur?.setOnClickListener {
                    if (thur.isChecked) {
                        daysArray.add(4)
                        thur.setBackgroundColor(ContextCompat.getColor(context, R.color.toggleButtonCheckedBackground))
                    }
                    else {
                        daysArray.sort()
                        daysArray.remove(4)
                        thur.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
                    }
                }

                fri?.setOnClickListener {
                    if (fri.isChecked) {
                        daysArray.add(5)
                        fri.setBackgroundColor(ContextCompat.getColor(context, R.color.toggleButtonCheckedBackground))
                    }
                    else {
                        daysArray.sort()
                        daysArray.remove(5)
                        fri.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
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
                    }
                    else {
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
                                    }
                                    else {
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
                            cursor = classDBHelper.getAllRow(context)!!
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
                val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context, R.style.AlertDialogStyle)
                materialAlertDialogBuilder.setTitle("Warning")
                materialAlertDialogBuilder.setMessage("You are fixing to delete a class, this will delete the class and all grades for this class, this can not be undone. Would you like to continue?")
                materialAlertDialogBuilder.setCancelable(false)
                materialAlertDialogBuilder.setPositiveButton("Yes") { _, _ ->
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
                                cursorGrades.getString(cursorGrades.getColumnIndex(GradesDBHelper.COLUMN_CLASS_ID))
                            mapGrades["grades"] =
                                cursorGrades.getString(cursorGrades.getColumnIndex(GradesDBHelper.COLUMN_GRADE))

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
                                cursorClasses.getString(cursorClasses.getColumnIndex(ClassesDBHelper.COLUMN_ID))
                            mapClasses["grades"] =
                                cursorClasses.getString(cursorClasses.getColumnIndex(ClassesDBHelper.COLUMN_CLASS_NAME))


                            classesDBHelper.deleteRow(mapClasses["id"].toString())
                            cursorClasses.moveToNext()
                        }
                    }

                    dataList.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                    dialog.dismiss()

                    val saveState = Runnable {
                        (context as MainActivity).textViewVisibilityClasses()

                    }

                    MainActivity().runOnUiThread(saveState)
                }
                materialAlertDialogBuilder.setNegativeButton("No", null)
                materialAlertDialogBuilder.show()
            }

            deleteAllButton?.setOnClickListener {
                val classesDBHelper = ClassesDBHelper(context, null)
                val gradesDBHelper = GradesDBHelper(context, null)
                val assignmentsDBHelper = AssignmentsDBHelper(context, null)

                if (classesDBHelper.getCount() > 0) {
                    val alertDialog = MaterialAlertDialogBuilder(context, R.style.AlertDialogStyle)
                    alertDialog.setTitle("Warning")
                    alertDialog.setMessage("Would you like to delete all classes, grades, and assignments?")

                    alertDialog.setPositiveButton("Yes") { _, _ ->
                        dialog.dismiss()
                        classesDBHelper.deleteAll()
                        gradesDBHelper.deleteAll()
                        assignmentsDBHelper.deleteAll()
                        dataList.clear()
                        val runnable = Runnable {
                            (context as MainActivity).deleteAll()
                        }
                        MainActivity().runOnUiThread(runnable)
                    }
                    alertDialog.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    alertDialog.show()
                } else {
                    Toast.makeText(context, "There are no classes stored", Toast.LENGTH_SHORT)
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
}