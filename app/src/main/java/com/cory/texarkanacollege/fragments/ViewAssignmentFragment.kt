package com.cory.texarkanacollege.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.database.AssignmentsDBHelper
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.*

class ViewAssignmentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_assignment, container, false)
    }

    @SuppressLint("Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val id = args?.getString("id", "")
        val type = args?.getString("type", "")

        var date = ""

        var type2 = type

        val formatter = SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
        val dateFormatted = formatter.format(Date())

        val dbHandler = AssignmentsDBHelper(requireActivity().applicationContext, null)

        val materialToolBar = requireActivity().findViewById<MaterialToolbar>(R.id.materialToolBarViewAssignments)

        materialToolBar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        materialToolBar.setOnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.markAsDone -> {

                    if (type2 == "upcoming") {
                        type2 = "done"
                        Toast.makeText(requireContext(), "Marked assignment as done", Toast.LENGTH_SHORT).show()
                        materialToolBar.menu.findItem(R.id.markAsDone).icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_remove_done_24)
                        dbHandler.upcomingMarkAsDone("done", id.toString())
                    }
                    else if (type2 == "pastDue") {
                        type2 = "done"
                        Toast.makeText(requireContext(), "Marked assignment as done", Toast.LENGTH_SHORT).show()
                        materialToolBar.menu.findItem(R.id.markAsDone).icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_remove_done_24)
                        dbHandler.pastDueMarkAsDone("done", id.toString())
                    }
                    else if (type2 == "done") {

                        if (dateFormatted >= date) {
                            type2 = "upcoming"
                        }
                        else if (dateFormatted < date) {
                            type2 = "pastDue"
                        }
                        Toast.makeText(requireContext(), "Marked assignment as not done", Toast.LENGTH_SHORT).show()
                        materialToolBar.menu.findItem(R.id.markAsDone).icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_done_24)
                        dbHandler.doneMarkAsUndone("", id.toString())
                    }
                    true
                }

                else -> false
            }
        }

        val textViewTitle = requireActivity().findViewById<TextView>(R.id.viewAssignmentTitle)
        val textViewClassName = requireActivity().findViewById<TextView>(R.id.viewAssignmentClass)
        val textViewNotes = requireActivity().findViewById<TextView>(R.id.viewAssignmentNotes)
        val textViewDueDate = requireActivity().findViewById<TextView>(R.id.viewAssignmentDueDate)

        val upcomingCursor = dbHandler.getUpcomingSingleRow(id.toString())
        upcomingCursor.moveToFirst()

        if (type == "upcoming") {
            while (!upcomingCursor.isAfterLast) {
                textViewClassName.text = "Class Name: " + upcomingCursor.getString(
                    upcomingCursor.getColumnIndex(
                        AssignmentsDBHelper.COLUMN_CLASS_NAME
                    )
                )
                textViewTitle.text =
                    "Assignment Name: " + upcomingCursor.getString(
                        upcomingCursor.getColumnIndex(
                            AssignmentsDBHelper.COLUMN_ASSIGNMENT_NAME
                        )
                    )
                textViewDueDate.text =
                    "Due Date: " + upcomingCursor.getString(
                        upcomingCursor.getColumnIndex(
                            AssignmentsDBHelper.COLUMN_ASSIGNMENT_DUE_DATE
                        )
                    )
                if (upcomingCursor.getString(upcomingCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_NOTES)) == "") {
                    textViewNotes.visibility = View.GONE
                } else {
                    textViewNotes.text = "Notes: " + upcomingCursor.getString(
                        upcomingCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_NOTES)
                    )
                }

                date = upcomingCursor.getString(
                    upcomingCursor.getColumnIndex(
                        AssignmentsDBHelper.COLUMN_ASSIGNMENT_DUE_DATE
                    ))
                upcomingCursor.moveToNext()

            }
        } else if (type == "pastDue") {
            val pastDueCursor = dbHandler.getPastDueSingleRow(id.toString())
            pastDueCursor.moveToFirst()

            while (!pastDueCursor.isAfterLast) {
                textViewClassName.text = "Class Name: " + pastDueCursor.getString(
                    pastDueCursor.getColumnIndex(
                        AssignmentsDBHelper.COLUMN_CLASS_NAME
                    )
                )
                textViewTitle.text =
                    "Assignment Name: " + pastDueCursor.getString(
                        pastDueCursor.getColumnIndex(
                            AssignmentsDBHelper.COLUMN_ASSIGNMENT_NAME
                        )
                    )
                textViewDueDate.text =
                    "Due Date: " + pastDueCursor.getString(
                        pastDueCursor.getColumnIndex(
                            AssignmentsDBHelper.COLUMN_ASSIGNMENT_DUE_DATE
                        )
                    )
                if (pastDueCursor.getString(pastDueCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_NOTES)) == "") {
                    textViewNotes.visibility = View.GONE
                } else {
                    textViewNotes.text = "Notes: " + pastDueCursor.getString(
                        pastDueCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_NOTES)
                    )
                }
                date = pastDueCursor.getString(
                    pastDueCursor.getColumnIndex(
                        AssignmentsDBHelper.COLUMN_ASSIGNMENT_DUE_DATE
                    )
                )
                pastDueCursor.moveToNext()
            }


        } else if (type == "done") {
            materialToolBar.menu.findItem(R.id.markAsDone).icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_remove_done_24)
            val doneCursor = dbHandler.getDoneSingleRow(id.toString())
            doneCursor.moveToFirst()

            while (!doneCursor.isAfterLast) {
                textViewClassName.text = "Class Name: " + doneCursor.getString(
                    doneCursor.getColumnIndex(
                        AssignmentsDBHelper.COLUMN_CLASS_NAME
                    )
                )
                textViewTitle.text =
                    "Assignment Name: " + doneCursor.getString(
                        doneCursor.getColumnIndex(
                            AssignmentsDBHelper.COLUMN_ASSIGNMENT_NAME
                        )
                    )
                textViewDueDate.text =
                    "Due Date: " + doneCursor.getString(
                        doneCursor.getColumnIndex(
                            AssignmentsDBHelper.COLUMN_ASSIGNMENT_DUE_DATE
                        )
                    )
                if (doneCursor.getString(doneCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_NOTES)) == "") {
                    textViewNotes.visibility = View.GONE
                } else {
                    textViewNotes.text = "Notes: " + doneCursor.getString(
                        doneCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_NOTES)
                    )
                }
                date = doneCursor.getString(
                    doneCursor.getColumnIndex(
                        AssignmentsDBHelper.COLUMN_ASSIGNMENT_DUE_DATE
                    )
                )
                doneCursor.moveToNext()
            }
        }
    }
}