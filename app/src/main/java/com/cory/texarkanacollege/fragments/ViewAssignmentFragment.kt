package com.cory.texarkanacollege.fragments

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.ColoredBackgroundsData
import com.cory.texarkanacollege.classes.DarkThemeData
import com.cory.texarkanacollege.database.AssignmentsDBHelper
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.*

class ViewAssignmentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val darkThemeData = DarkThemeData(requireContext())
        when {
            darkThemeData.loadState() == 1 -> {
                activity?.setTheme(R.style.Dark)
            }
            darkThemeData.loadState() == 0 -> {
                activity?.setTheme(R.style.Theme_MyApplication)
            }
            darkThemeData.loadState() == 2 -> {
                when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_NO -> {
                        activity?.setTheme(R.style.Theme_MyApplication)
                    }
                    Configuration.UI_MODE_NIGHT_YES -> {
                        activity?.setTheme(R.style.Dark)
                    }
                    Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                        activity?.setTheme(R.style.Dark)
                    }
                }
            }
        }
        return inflater.inflate(R.layout.fragment_view_assignment, container, false)
    }

    @SuppressLint("Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        val id = args?.getString("id", "")
        val type = args?.getString("type", "")
        val category = args?.getString("category", "")

        val categoryTextView = activity?.findViewById<TextView>(R.id.viewAssignmentCategory)
        categoryTextView?.text = "Category: " + category

        if (ColoredBackgroundsData(requireContext()).loadColoredBackgrounds()) {
            if (category == "Homework") {
                activity?.findViewById<CardView>(R.id.viewAssignmentCardView)?.setCardBackgroundColor(ContextCompat.getColor(requireContext(),
                            R.color.homeworkCardBackgroundColor))
            } else if (category == "Exam") {
                activity?.findViewById<CardView>(R.id.viewAssignmentCardView)?.setCardBackgroundColor(ContextCompat.getColor(requireContext(),
                            R.color.examCardBackgroundColor
                        )
                    )
            }
        }

        var date = ""

        var type2 = type

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val dateFormatted = formatter.format(Date())

        val dbHandler = AssignmentsDBHelper(requireActivity().applicationContext, null)

        val materialToolBar = requireActivity().findViewById<MaterialToolbar>(R.id.materialToolBarViewAssignments)

        materialToolBar.setNavigationOnClickListener {
            val loadIntoList = Runnable {
                (context as MainActivity).assignmentLoadIntoList()
            }
            activity?.runOnUiThread(loadIntoList)
            activity?.supportFragmentManager?.popBackStack()
        }
        materialToolBar.setOnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.markAsDone -> {

                    if (type2 == "upcoming") {
                       val update = dbHandler.upcomingMarkAsDone("done", id.toString())
                        if (update.count != 0) {
                            materialToolBar.menu.findItem(R.id.markAsDone).icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_remove_done_24)
                            type2 = "done"
                            Toast.makeText(requireContext(), "Marked assignment as done", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toast.makeText(requireContext(), "There was an error marking assignment as done", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else if (type2 == "pastDue") {
                        val update = dbHandler.pastDueMarkAsDone("done", id.toString())
                        if (update.count != 0) {
                            materialToolBar.menu.findItem(R.id.markAsDone).icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_remove_done_24)
                            type2 = "done"
                            Toast.makeText(requireContext(), "Marked assignment as done", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toast.makeText(requireContext(), "There was an error marking assignment as done", Toast.LENGTH_SHORT).show()
                        }
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
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val dateFormat = dateFormatter.parse(upcomingCursor.getString(
                    upcomingCursor.getColumnIndex(
                        AssignmentsDBHelper.COLUMN_ASSIGNMENT_DUE_DATE
                    )
                )) as Date
                val formatter2 = SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
                val dateFormatted2 = formatter2.format(dateFormat)
                textViewDueDate.text =
                    "Due Date: " + dateFormatted2
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

                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val dateFormat = dateFormatter.parse(pastDueCursor.getString(
                    pastDueCursor.getColumnIndex(
                        AssignmentsDBHelper.COLUMN_ASSIGNMENT_DUE_DATE
                    )
                )) as Date
                val formatter2 = SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
                val dateFormatted2 = formatter2.format(dateFormat)
                textViewDueDate.text =
                    "Due Date: " + dateFormatted2
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
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val dateFormat = dateFormatter.parse(doneCursor.getString(
                    doneCursor.getColumnIndex(
                        AssignmentsDBHelper.COLUMN_ASSIGNMENT_DUE_DATE
                    )
                )) as Date
                val formatter2 = SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
                val dateFormatted2 = formatter2.format(dateFormat)
                textViewDueDate.text =
                    "Due Date: " + dateFormatted2
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

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val loadIntoList = Runnable {
                        (context as MainActivity).assignmentLoadIntoList()
                    }
                    activity?.runOnUiThread(loadIntoList)
                    activity?.supportFragmentManager?.popBackStack()
                }
            })
    }
}