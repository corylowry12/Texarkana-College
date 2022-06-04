package com.cory.texarkanacollege.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
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
import com.cory.texarkanacollege.fragments.ViewAssignmentFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.suke.widget.SwitchButton
import java.math.RoundingMode

class PastDueAdapter(val context: Context,
                      private val dataList:  ArrayList<HashMap<String, String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title = itemView.findViewById<TextView>(R.id.row_class)!!
        var classTime = itemView.findViewById<TextView>(R.id.row_class_time)
        var notes = itemView.findViewById<TextView>(R.id.row_class_notes)

        fun bind(position: Int) {

            val dataItem = dataList[position]

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
            args.putString("type", "pastDue")
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

            editButton.setOnClickListener {
                dialog.dismiss()
            }
            deleteButton.setOnClickListener {
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