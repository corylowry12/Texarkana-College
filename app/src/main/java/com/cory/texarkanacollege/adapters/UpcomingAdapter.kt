package com.cory.texarkanacollege.adapters

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.cory.texarkanacollege.*
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
import com.google.android.material.textfield.TextInputEditText
import com.suke.widget.SwitchButton
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UpcomingAdapter(val context: Context,
                     private val dataList:  ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var className = itemView.findViewById<TextView>(R.id.className)!!
        var title = itemView.findViewById<TextView>(R.id.assignmentName)!!
        var classTime = itemView.findViewById<TextView>(R.id.dueDate)
        var notes = itemView.findViewById<TextView>(R.id.notes)

        fun bind(position: Int) {

            val dataItem = dataList[position]

            className.text = "Class Name: " + dataItem["className"]
            title.text = "Assignment Name: " + dataItem["assignmentName"]
            classTime.text = "Due Date: " + dataItem["dueDate"]

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

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dataItem = dataList[position]
        holder.itemView.setOnClickListener {
            val viewAssignmentFragment = ViewAssignmentFragment()

            val manager =
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            val args = Bundle()
            args.putString("id", dataItem["id"])
            args.putString("type", "upcoming")
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
            val markAsDoneButton = assignmentOptionsView.findViewById<Button>(R.id.doneButton)
            val deleteButton = assignmentOptionsView.findViewById<Button>(R.id.deleteButton)
            val cancelButton = assignmentOptionsView.findViewById<Button>(R.id.cancelButton)

            /*editButton.setOnClickListener {
                dialog.dismiss()

                val dialog = BottomSheetDialog(requireContext())
                val addAssignmentView =
                    layoutInflater.inflate(R.layout.add_assignment_bottom_sheet, null)
                dialog.setCancelable(false)
                dialog.setContentView(addAssignmentView)
                val classesMenu =
                    addAssignmentView.findViewById<AutoCompleteTextView>(R.id.classesAutoComplete)
                val addAssignmentButton =
                    addAssignmentView.findViewById<Button>(R.id.addAssignmentButton)
                val assignmentName =
                    addAssignmentView.findViewById<TextInputEditText>(R.id.name)

                val assignmentNotes = addAssignmentView.findViewById<TextInputEditText>(R.id.notes)

                val cancelButtonEditAssignment = addAssignmentView.findViewById<Button>(R.id.cancelButton)
                val dueDateChip = addAssignmentView.findViewById<Chip>(R.id.dueDateChip)
                val formatter = SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
                val dateFormatted = formatter.format(Date())
                dueDateChip.text = dateFormatted
                val adapter =
                    ArrayAdapter(context, R.layout.list_item, classesArray)
                classesMenu.setAdapter(adapter)
                classesMenu.setText(classesArray.elementAt(0), false)

                var date = dateFormatted

                dueDateChip.setOnClickListener {
                    val datePicker = DatePickerDialog(
                        context
                    )
                    datePicker.setCancelable(false)
                    //datePicker.datePicker.minDate = System.currentTimeMillis()

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
                        date = dateFormattedSimple
                        datePicker.dismiss()
                    }
                }

                addAssignmentButton.setOnClickListener {
                    if (assignmentName.text.toString() == "") {
                        Toast.makeText(context, "An assignment name is required", Toast.LENGTH_SHORT).show()
                    }
                    else {

                        dialog.dismiss()
                    }
                }
                cancelButtonEditAssignment.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }*/
            markAsDoneButton.setOnClickListener {
                AssignmentsDBHelper(context, null).upcomingMarkAsDone("done", dataItem["id"].toString())
                val runnable = Runnable {
                    (context as MainActivity).assignmentLoadIntoList()
                }
                MainActivity().runOnUiThread(runnable)
                dialog.dismiss()

            }
            deleteButton.setOnClickListener {
                AssignmentsDBHelper(context, null).deleteRow(dataItem["id"].toString())
                dataList.removeAt(holder.adapterPosition)
                notifyItemRemoved(holder.adapterPosition)
                Toast.makeText(context, context.getString(R.string.assignment_deleted), Toast.LENGTH_SHORT).show()
                val runnable = Runnable {
                    (context as MainActivity).assignmentLoadIntoList()
                }
                MainActivity().runOnUiThread(runnable)
                dialog.dismiss()
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