package com.cory.texarkanacollege.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.cory.texarkanacollege.*
import com.cory.texarkanacollege.database.ClassesDBHelper
import com.cory.texarkanacollege.database.GradesDBHelper
import com.cory.texarkanacollege.fragments.GradeFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.suke.widget.SwitchButton
import java.math.RoundingMode

class ClassesAdapter(val context: Context,
                     private val dataList:  ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title = itemView.findViewById<TextView>(R.id.row_class)!!
        var classTime = itemView.findViewById<TextView>(R.id.row_class_time)

        fun bind(position: Int) {

            val dataItem = dataList[position]

            title.text = "Class Name: " + dataItem["className"]
            classTime.text = "Class Time: " + dataItem["classTime"]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.classes_list_item, parent, false))
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

      val classAverage = holder.itemView.findViewById<TextView>(R.id.row_class_average)
        val gradesDBHandler = GradesDBHelper(context, null)

        var grades = 0.0
        var weights = 0.0
        val dataItem = dataList[holder.adapterPosition]

        val cursor = GradesDBHelper(context, null).getGrades(dataItem["id"].toString())
        cursor!!.moveToFirst()

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
            classAverage.text = "Class Average: " + average.toString() + "%"
        }
        else {
            classAverage.text = "Class Average: 0.0%"
        }

        holder.itemView.setOnClickListener {

            val itemPositionData = ItemID(context)
            itemPositionData.setPosition(dataItem["id"]!!.toInt())
            val gradeFragment = GradeFragment()
            (context as MainActivity).gradeFragment = gradeFragment

            val manager =
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            manager.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            manager.replace(R.id.fragment_container, gradeFragment).addToBackStack(null)
            manager.commit()
        }

        holder.itemView.setOnLongClickListener {

            val dialog = BottomSheetDialog(context)
            val addGradeView = LayoutInflater.from(context).inflate(R.layout.options_bottom_sheet, null)
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
                val daysArray :MutableList<Int> = ArrayList()

                val bottomSheetDialog = BottomSheetDialog(context)
                val addClassBottomSheetView = LayoutInflater.from(context).inflate(R.layout.add_class_bottom_sheet, null)
                bottomSheetDialog.setCancelable(false)
                bottomSheetDialog.setContentView(addClassBottomSheetView)
                val textViewHeading = bottomSheetDialog.findViewById<TextView>(R.id.headingTextView)
                val nameEditTextAddClass = bottomSheetDialog.findViewById<TextInputEditText>(R.id.name)
                val cancelButtonAddClass = bottomSheetDialog.findViewById<Button>(R.id.cancelButton)
                val addClassButtonAddClass = bottomSheetDialog.findViewById<Button>(R.id.addClassButton)
                val netClassSwitchAddClass = bottomSheetDialog.findViewById<SwitchButton>(R.id.netClassSwitch)
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
                    daysArray.add(1)
                }
                if (dataItemAddClass["classTime"].toString().contains("Tue")) {
                    tue?.isChecked = true
                    daysArray.add(2)
                }
                if (dataItemAddClass["classTime"].toString().contains("Wed")) {
                    wed?.isChecked = true
                    daysArray.add(3)
                }
                if (dataItemAddClass["classTime"].toString().contains("Thur")) {
                    thur?.isChecked = true
                    daysArray.add(4)
                }
                if (dataItemAddClass["classTime"].toString().contains("Fri")) {
                    fri?.isChecked = true
                    daysArray.add(5)
                }

                mon?.setOnClickListener {
                    if (mon.isChecked) {
                        daysArray.add(1)
                    }
                    else {
                        daysArray.sort()
                        daysArray.removeAt(0)

                    }
                }

                tue?.setOnClickListener {
                    if (tue.isChecked) {
                        daysArray.add(2)
                    }
                    else {
                        daysArray.sort()
                        daysArray.removeAt(1)

                    }
                }

                wed?.setOnClickListener {
                    if (wed.isChecked) {
                        daysArray.add(3)
                    }
                    else {
                        daysArray.sort()
                        daysArray.removeAt(2)

                    }
                }

                thur?.setOnClickListener {
                    if (thur.isChecked) {
                        daysArray.add(4)
                    }
                    else {
                        daysArray.sort()
                        daysArray.removeAt(3)

                    }
                }

                fri?.setOnClickListener {
                    if (fri.isChecked) {
                        daysArray.add(5)
                    }
                    else {
                        daysArray.sort()
                        daysArray.removeAt(4)

                    }
                }

                netClassSwitchAddClass?.setOnCheckedChangeListener { compoundButton, b ->
                    if (b) {
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
                    if (nameEditTextAddClass?.text == null && textString == "") {
                        Toast.makeText(
                            context,
                            "Class Name is required",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (netClassSwitchAddClass!!.isChecked) {
                            days = "Web"
                        } else if (!netClassSwitchAddClass.isChecked && daysArray.count() == 0) {
                            Toast.makeText(
                                context,
                                "Must select a day of the week or web course",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (!netClassSwitchAddClass.isChecked && daysArray.count() != 0){
                            for (i in 0 until daysArray.count()) {
                                if (daysArray[i] == 1) {
                                    days += "Mon"
                                }
                                if (daysArray[i] == 2) {
                                    if (i == daysArray.count() - 1) {
                                        days += " and Tue"
                                    }
                                    else if (i == 0) {
                                        days += "Tue"
                                    }
                                    else {
                                        days += ", Tue"
                                    }
                                }
                                if (daysArray[i] == 3) {
                                    if (i == daysArray.count() - 1) {
                                        days += " and Wed"
                                    }
                                    else if (i == 0) {
                                        days += "Wed"
                                    }
                                    else {
                                        days += ", Wed"
                                    }
                                }
                                if (daysArray[i] == 4) {
                                    if (i == daysArray.count() - 1) {
                                        days += " and Thur"
                                    }
                                    else if (i == 0) {
                                        days += "Thur"
                                    }
                                    else {
                                        days += ", Thur"
                                    }
                                }
                                if (daysArray[i] == 5) {
                                    if (i == daysArray.count() - 1) {
                                        days += " and Fri"
                                    }
                                    else if (i == 0) {
                                        days += "Fri"
                                    }
                                    else {
                                        days += ", Fri"
                                    }
                                }
                            }
                        }

                        if (daysArray.count() == 0 && !netClassSwitchAddClass.isChecked) {
                            Toast.makeText(context, "Must enter days or if its a web course", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            val classDBHelper = ClassesDBHelper(context, null)
                            classDBHelper.update(dataItemAddClass["id"].toString(), nameEditTextAddClass!!.text.toString(), days)
                            dataList.removeAt(holder.adapterPosition)

                            val map = HashMap<String, String>()
                            map["id"] = dataItemAddClass["id"].toString()
                            map["className"] = nameEditTextAddClass.text.toString()
                            map["classTime"] = days

                            dataList.add(holder.adapterPosition, map)
                            notifyItemChanged(holder.adapterPosition)
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
                val datalist = dataList[holder.adapterPosition]
                val id = datalist["id"]

                val classesDBHelper = ClassesDBHelper(context, null)
                val gradesDBHelper = GradesDBHelper(context, null)

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

            deleteAllButton?.setOnClickListener {
                val classesDBHelper = ClassesDBHelper(context, null)
                val gradesDBHelper = GradesDBHelper(context, null)

                if (classesDBHelper.getCount() > 0) {
                    val alertDialog = MaterialAlertDialogBuilder(context, R.style.AlertDialogStyle)
                    alertDialog.setTitle("Delete All")
                    alertDialog.setMessage("Would you like to delete all grades and classes?")

                    alertDialog.setPositiveButton("Yes") { _, _ ->
                        dialog.dismiss()
                        classesDBHelper.deleteAll()
                        gradesDBHelper.deleteAll()
                        notifyItemRangeRemoved(0, 0)
                    }
                    alertDialog.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    alertDialog.show()
                }
                else {
                    Toast.makeText(context, "There are no classes stored", Toast.LENGTH_SHORT).show()
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