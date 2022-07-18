package com.cory.texarkanacollege.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.CategoryTextViewVisible
import com.cory.texarkanacollege.classes.ColoredBackgroundsData
import com.cory.texarkanacollege.database.AssignmentsDBHelper
import com.cory.texarkanacollege.fragments.ViewAssignmentFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class DoneAdapter(val context: Context,
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
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val dateFormatted = formatter.parse(dataItem["dueDate"].toString()) as Date
            val formatter2 = SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
            val dateFormatted2 = formatter2.format(dateFormatted)
            classTime.text = "Due Date: " + dateFormatted2.toString()

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

    @SuppressLint("Range")
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
            else {
                holder.itemView.findViewById<CardView>(R.id.cardViewAssignmentItem)
                    .setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.cardViewLightBackgroundColor
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
            args.putString("type", "done")
            args.putString("category", dataItem["category"])
            viewAssignmentFragment.arguments = args
            manager.setCustomAnimations(
                R.anim.slide_in_fragment,
                R.anim.fade_out_fragment,
                R.anim.fade_in_fragment,
                R.anim.slide_out_fragment
            )
            manager.add(R.id.fragment_container, viewAssignmentFragment).addToBackStack(null)
            manager.commit()
        }

        holder.itemView.setOnLongClickListener {
            val dialog = BottomSheetDialog(context)
            val assignmentOptionsView =
                LayoutInflater.from(context).inflate(R.layout.assignment_options_bottom_sheet, null)
            dialog.setCancelable(false)
            dialog.setContentView(assignmentOptionsView)

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
            headingTextView!!.text = "Options/" + dataItem["assignmentName"]

            val editButton = assignmentOptionsView.findViewById<Button>(R.id.editButton)
            val deleteButton = assignmentOptionsView.findViewById<Button>(R.id.deleteButton)
            val cancelButton = assignmentOptionsView.findViewById<Button>(R.id.cancelButton)
            val markAsDoneButton = assignmentOptionsView.findViewById<Button>(R.id.doneButton)
            markAsDoneButton.text = context.getString(R.string.mark_as_not_done)

            editButton.visibility = View.GONE

            markAsDoneButton.setOnClickListener {
                AssignmentsDBHelper(context, null).doneMarkAsUndone("", dataItem["id"].toString())
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