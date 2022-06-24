package com.cory.texarkanacollege.adapters

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.cory.texarkanacollege.*
import com.cory.texarkanacollege.classes.CategoryTextViewVisible
import com.cory.texarkanacollege.classes.ColoredBackgroundsData
import com.cory.texarkanacollege.database.AssignmentsDBHelper
import com.cory.texarkanacollege.database.ClassesDBHelper
import com.cory.texarkanacollege.database.GradesDBHelper
import com.cory.texarkanacollege.fragments.GradeFragment
import com.cory.texarkanacollege.fragments.ViewAssignmentFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.suke.widget.SwitchButton
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PastDueAdapter(val context: Context,
                      private val dataList:  ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val className = itemView.findViewById<TextView>(R.id.className)!!
        val title = itemView.findViewById<TextView>(R.id.assignmentName)!!
        val classTime = itemView.findViewById<TextView>(R.id.dueDate)!!
        val notes = itemView.findViewById<TextView>(R.id.notes)!!
        val category = itemView.findViewById<TextView>(R.id.category)!!

        fun bind(position: Int) {

            val dataItem = dataList[position]

            className.text = "Class Name: " + dataItem["className"]
            title.text = "Assignment Name: " + dataItem["assignmentName"]
            classTime.text = "Due Date: " + dataItem["dueDate"]

            if (CategoryTextViewVisible(context).loadCategoryTextView()) {
                category.visibility = View.VISIBLE
                category.text = "Category: " + dataItem["category"]
            }
            else {
                category.visibility = View.GONE
            }

            if (dataItem["notes"] == "" || dataItem["notes"] == null) {
                notes.visibility = View.GONE
            }
            else {
                if (dataItem["notes"]!!.length < 15) {
                    notes.text = "Notes: " + dataItem["notes"]
                }
                else {
                    val notesSubstring = dataItem["notes"]!!.substring(0, 15)
                    notes.text = "Notes: " + notesSubstring + "..."
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.assignment_list_row, parent, false))
    }

    @SuppressLint("Range", "NewApi")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataItem = dataList[position]

        if (ColoredBackgroundsData(context).loadColoredBackgrounds()) {
            if (dataItem["category"] == "Homework") {
                holder.itemView.findViewById<CardView>(R.id.cardViewAssignmentItem)
                    .setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.homeworkCardBackgroundColor
                        )
                    )
            } else if (dataItem["category"] == "Exam") {
                holder.itemView.findViewById<CardView>(R.id.cardViewAssignmentItem)
                    .setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.examCardBackgroundColor
                        )
                    )
            }
        }

        holder.itemView.setOnClickListener {
            val viewAssignmentFragment = ViewAssignmentFragment()

            val manager =
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            val args = Bundle()
            args.putString("id", dataItem["id"])
            args.putString("type", "pastDue")
            args.putString("category", dataItem["category"])
            viewAssignmentFragment.arguments = args
            manager.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            manager.add(R.id.fragment_container, viewAssignmentFragment).addToBackStack(null)
            manager.commit()
        }

        holder.itemView.setOnLongClickListener {
            val dialog = BottomSheetDialog(context)
            val assignmentOptionsView =
                LayoutInflater.from(context).inflate(R.layout.assignment_options_bottom_sheet, null)
            dialog.setCancelable(false)
            dialog.setContentView(assignmentOptionsView)
            val editButton = assignmentOptionsView.findViewById<Button>(R.id.editButton)
            val deleteButton = assignmentOptionsView.findViewById<Button>(R.id.deleteButton)
            val cancelButton = assignmentOptionsView.findViewById<Button>(R.id.cancelButton)
            val markAsDoneButton = assignmentOptionsView.findViewById<Button>(R.id.doneButton)

            editButton.setOnClickListener {
                dialog.dismiss()

                var classesArray = ArrayList<String>()

                classesArray.clear()
                val dbHandler = ClassesDBHelper(context.applicationContext, null)

                val cursor = dbHandler.getAllRow(context)
                cursor!!.moveToFirst()

                if (dbHandler.getCount() > 0) {
                    while (!cursor.isAfterLast) {
                        classesArray.add(cursor.getString(cursor.getColumnIndex(ClassesDBHelper.COLUMN_CLASS_NAME)))

                        cursor.moveToNext()

                    }
                }

                val editAssignmentDialog = BottomSheetDialog(context)
                val addAssignmentView =
                    LayoutInflater.from(context).inflate(R.layout.add_assignment_bottom_sheet, null)
                editAssignmentDialog.setCancelable(false)
                editAssignmentDialog.setContentView(addAssignmentView)
                val header = editAssignmentDialog.findViewById<TextView>(R.id.headingTextView)
                header?.text = "Update Assignment"
                val classesMenu =
                    addAssignmentView.findViewById<MaterialAutoCompleteTextView>(R.id.classesAutoComplete)

                val addAssignmentButton =
                    addAssignmentView.findViewById<Button>(R.id.addAssignmentButton)
                addAssignmentButton.text = "Update Assignment"
                val assignmentName =
                    addAssignmentView.findViewById<TextInputEditText>(R.id.name)

                val assignmentNotes = addAssignmentView.findViewById<TextInputEditText>(R.id.notes)

                val categoryToggleGroup = addAssignmentView.findViewById<MaterialButtonToggleGroup>(R.id.categoryToggleGroup)
                val examToggle = addAssignmentView.findViewById<MaterialButton>(R.id.examToggle)
                val homeworkToggle = addAssignmentView.findViewById<MaterialButton>(R.id.homeworkToggle)
                val otherToggle = addAssignmentView.findViewById<MaterialButton>(R.id.otherToggle)

                if (dataItem["category"] == "Exam") {
                    examToggle.isChecked = true
                    examToggle.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.toggleButtonCheckedBackground
                        )
                    )
                }
                else if (dataItem["category"] == "Homework") {
                    homeworkToggle.isChecked = true
                    homeworkToggle.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.toggleButtonCheckedBackground
                        )
                    )
                }
                else {
                    otherToggle.isChecked = true
                    otherToggle.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.toggleButtonCheckedBackground
                        )
                    )
                }

                categoryToggleGroup.forEach {
                    examToggle.setOnClickListener {

                        examToggle.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.toggleButtonCheckedBackground
                            )
                        )

                        examToggle.isChecked = true

                        homeworkToggle.isChecked = false
                        homeworkToggle.setBackgroundColor(ContextCompat.getColor(
                            context,
                            R.color.transparent
                        ))
                        otherToggle.isChecked = false
                        otherToggle.setBackgroundColor(ContextCompat.getColor(
                            context,
                            R.color.transparent
                        ))
                    }
                    homeworkToggle.setOnClickListener {
                        homeworkToggle.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.toggleButtonCheckedBackground
                            )
                        )

                        homeworkToggle.isChecked = true
                        examToggle.isChecked = false
                        examToggle.setBackgroundColor(ContextCompat.getColor(
                            context,
                            R.color.transparent
                        ))
                        otherToggle.isChecked = false
                        otherToggle.setBackgroundColor(ContextCompat.getColor(
                            context,
                            R.color.transparent
                        ))
                    }
                    otherToggle.setOnClickListener {
                        otherToggle.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.toggleButtonCheckedBackground
                            )
                        )

                        otherToggle.isChecked = true
                        examToggle.isChecked = false
                        examToggle.setBackgroundColor(ContextCompat.getColor(
                            context,
                            R.color.transparent
                        ))
                        homeworkToggle.isChecked = false
                        homeworkToggle.setBackgroundColor(ContextCompat.getColor(
                            context,
                            R.color.transparent
                        ))
                    }
                }

                assignmentName.setText(dataItem["assignmentName"].toString())
                assignmentNotes.setText(dataItem["notes"].toString())

                val cancelButtonEditAssignment = addAssignmentView.findViewById<Button>(R.id.cancelButton)
                val dueDateChip = addAssignmentView.findViewById<Chip>(R.id.dueDateChip)

                dueDateChip.text = dataItem["dueDate"]
                val adapter =
                    ArrayAdapter(context, R.layout.list_item, classesArray)
                classesMenu.setAdapter(adapter)
                classesMenu.setText(dataItem["className"], false)

                dueDateChip.setOnClickListener {
                    val datePicker = DatePickerDialog(
                        context, R.style.datePickerLight
                    )
                    datePicker.setCancelable(false)
                    datePicker.datePicker.minDate = System.currentTimeMillis()

                    datePicker.show()

                    val positiveButton =
                        datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE)
                    val negativeButton =
                        datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE)

                    positiveButton.setOnClickListener {
                        val year = datePicker.datePicker.year
                        val month = datePicker.datePicker.month
                        val day = datePicker.datePicker.dayOfMonth

                        val calendar = Calendar.getInstance()
                        calendar.set(year, month, day)
                        val simpleDateFormat =
                            SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
                        val dateFormattedSimple = simpleDateFormat.format(calendar.time)
                        dueDateChip.text = dateFormattedSimple
                        datePicker.dismiss()
                    }
                }

                addAssignmentButton.setOnClickListener {
                    if (assignmentName.text.toString() == "") {
                        Toast.makeText(
                            context,
                            "An assignment name is required",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        var category = ""
                        if (homeworkToggle.isChecked) {
                            category = "Homework"
                        }
                        else if (examToggle.isChecked) {
                            category = "Exam"
                        }
                        else if (otherToggle.isChecked) {
                            category = "Other"
                        }
                        AssignmentsDBHelper(context, null).update(
                            dataItem["id"].toString(),
                            assignmentName.text.toString(),
                            classesMenu.text.toString(),
                            dueDateChip.text.toString(),
                            assignmentNotes.text.toString(),
                            category
                        )
                        editAssignmentDialog.dismiss()

                        dataList.clear()
                        val upcomingCursor = AssignmentsDBHelper(context, null).getPastDue()
                        upcomingCursor.moveToFirst()

                        while (!upcomingCursor.isAfterLast) {
                            val map = HashMap<String, String>()
                            map["id"] = upcomingCursor.getString(
                                upcomingCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_ID)
                            )
                            map["className"] = upcomingCursor.getString(
                                upcomingCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_CLASS_NAME)
                            )
                            map["assignmentName"] =
                                upcomingCursor.getString(
                                    upcomingCursor.getColumnIndex(
                                        AssignmentsDBHelper.COLUMN_ASSIGNMENT_NAME
                                    )
                                )
                            map["dueDate"] =
                                upcomingCursor.getString(
                                    upcomingCursor.getColumnIndex(
                                        AssignmentsDBHelper.COLUMN_ASSIGNMENT_DUE_DATE
                                    )
                                )
                            map["notes"] = upcomingCursor.getString(
                                upcomingCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_NOTES)
                            )
                            map["category"] = upcomingCursor.getString(
                                upcomingCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_CATEGORY))
                            dataList.add(map)

                            upcomingCursor.moveToNext()

                        }
                        notifyItemRangeChanged(0, dataList.count())
                    }
                }
                cancelButtonEditAssignment.setOnClickListener {
                    editAssignmentDialog.dismiss()
                }
                editAssignmentDialog.show()
            }
            markAsDoneButton.setOnClickListener {
                AssignmentsDBHelper(context, null).pastDueMarkAsDone("done", dataItem["id"].toString())
                val runnable = Runnable {
                    (context as MainActivity).assignmentLoadIntoList()
                }
                MainActivity().runOnUiThread(runnable)
                dialog.dismiss()

            }
            deleteButton.setOnClickListener {
                val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context, R.style.AlertDialogStyle)
                materialAlertDialogBuilder.setTitle("Warning")
                materialAlertDialogBuilder.setMessage("You are fixing to delete an assignment, this can not be undone, would you like to continue?")
                materialAlertDialogBuilder.setPositiveButton("Yes") { materialDialog, _ ->
                    AssignmentsDBHelper(context, null).deleteRow(dataItem["id"].toString())
                    dataList.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                    Toast.makeText(
                        context,
                        context.getString(R.string.assignment_deleted),
                        Toast.LENGTH_SHORT
                    ).show()
                    val runnable = Runnable {
                        (context as MainActivity).assignmentLoadIntoList()
                    }
                    MainActivity().runOnUiThread(runnable)
                    dialog.dismiss()
                    materialDialog.dismiss()
                }
                materialAlertDialogBuilder.setNegativeButton("No") { materialDialog, _ ->
                    materialDialog.dismiss()
                    dialog.dismiss()
                }
                materialAlertDialogBuilder.show()
            }
            cancelButton.setOnClickListener {
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