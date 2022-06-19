package com.cory.texarkanacollege.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cory.hourcalculator.adapters.PatchNotesBugFixesAdapter
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.adapters.ClassesAdapter
import com.cory.texarkanacollege.adapters.DoneAdapter
import com.cory.texarkanacollege.adapters.PastDueAdapter
import com.cory.texarkanacollege.adapters.UpcomingAdapter
import com.cory.texarkanacollege.database.AssignmentsDBHelper
import com.cory.texarkanacollege.database.ClassesDBHelper
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AssignmentFragment : Fragment() {

    private lateinit var upcomingAdapter: UpcomingAdapter
    private lateinit var pastDueAdapter: PastDueAdapter
    private lateinit var doneAdapter: DoneAdapter
    private val upcomingDataList = ArrayList<HashMap<String, String>>()
    private val pastDueDataList = ArrayList<HashMap<String, String>>()
    private val doneDataList = ArrayList<HashMap<String, String>>()

    private lateinit var upcomingGridLayoutManager: GridLayoutManager
    private lateinit var pastDueGridLayoutManager: GridLayoutManager
    private lateinit var doneGridLayoutManager: GridLayoutManager

    var classesArray = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assignment, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classesArray.clear()
        val dbHandler = ClassesDBHelper(requireActivity().applicationContext, null)

        val cursor = dbHandler.getAllRow(requireContext())
        cursor!!.moveToFirst()

        if (dbHandler.getCount() > 0) {
            while (!cursor.isAfterLast) {
                classesArray.add(cursor.getString(cursor.getColumnIndex(ClassesDBHelper.COLUMN_CLASS_NAME)))

                cursor.moveToNext()

            }
        }

        if (resources.getBoolean(R.bool.isTablet)) {
            upcomingGridLayoutManager = GridLayoutManager(requireContext(), 2)
            pastDueGridLayoutManager = GridLayoutManager(requireContext(), 2)
            doneGridLayoutManager = GridLayoutManager(requireContext(), 2)
        }
        else {
            upcomingGridLayoutManager = GridLayoutManager(requireContext(), 1)
            pastDueGridLayoutManager = GridLayoutManager(requireContext(), 1)
            doneGridLayoutManager = GridLayoutManager(requireContext(), 1)
        }

        upcomingAdapter = UpcomingAdapter(requireContext(), upcomingDataList)
        pastDueAdapter = PastDueAdapter(requireContext(), pastDueDataList)
        doneAdapter = DoneAdapter(requireContext(), doneDataList)

        loadIntoList()

        val topAppBar = activity?.findViewById<MaterialToolbar>(R.id.materialToolBarAssignements)
        topAppBar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.addClass -> {
                    if (classesArray.isNotEmpty()) {
                        val dialog = BottomSheetDialog(requireContext())
                        val addAssignmentView =
                            layoutInflater.inflate(R.layout.add_assignment_bottom_sheet, null)
                        dialog.setCancelable(false)
                        dialog.setContentView(addAssignmentView)
                        val classesMenu =
                            addAssignmentView.findViewById<MaterialAutoCompleteTextView>(R.id.classesAutoComplete)

                        val addAssignmentButton =
                            addAssignmentView.findViewById<Button>(R.id.addAssignmentButton)
                        val assignmentName =
                            addAssignmentView.findViewById<TextInputEditText>(R.id.name)

                        val assignmentNotes = addAssignmentView.findViewById<TextInputEditText>(R.id.notes)

                        val cancelButton = addAssignmentView.findViewById<Button>(R.id.cancelButton)
                        val dueDateChip = addAssignmentView.findViewById<Chip>(R.id.dueDateChip)
                        val formatter = SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
                        val dateFormatted = formatter.format(Date())
                        dueDateChip.text = dateFormatted
                        val adapter =
                            ArrayAdapter(requireContext(), R.layout.list_item, classesArray)
                        classesMenu.setAdapter(adapter)
                        classesMenu.setText(classesArray.elementAt(0), false)

                        var date = dateFormatted

                        dueDateChip.setOnClickListener {
                            val datePicker = DatePickerDialog(
                                requireContext(), R.style.datePickerLight
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
                                date = dateFormattedSimple
                                datePicker.dismiss()
                            }
                        }

                        addAssignmentButton.setOnClickListener {
                            if (assignmentName.text.toString() == "") {
                                Toast.makeText(requireContext(), "An assignment name is required", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                addAssignment(
                                    assignmentName.text.toString(),
                                    classesMenu.text.toString(),
                                    date,
                                    assignmentNotes.text.toString()
                                )
                                loadIntoList()
                                dialog.dismiss()
                            }
                        }
                        cancelButton.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }
                    else {
                        Toast.makeText(requireContext(), "There are no classes stored", Toast.LENGTH_SHORT).show()
                    }
                        true
                }
                else -> false
            }
        }

        val upcomingConstraint = activity?.findViewById<ConstraintLayout>(R.id.UpcomingConstraint)
        upcomingConstraint?.setOnClickListener {
            if (upcomingDataList.isNotEmpty()) {
                val upcomingRecyclerView =
                    requireView().findViewById<RecyclerView>(R.id.upcomingRecyclerView)
                val upcomingChevron =
                    requireView().findViewById<ImageView>(R.id.upcomingChevronImage)

                if (upcomingRecyclerView.visibility == View.GONE) {
                    upcomingRecyclerView.visibility = View.VISIBLE
                    upcomingChevron.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                } else {
                    upcomingRecyclerView.visibility = View.GONE
                    upcomingChevron.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                }
            }
            else {
                Toast.makeText(requireContext(), "There is no upcoming assignments", Toast.LENGTH_SHORT).show()
            }
        }

        val pastDueConstraint = activity?.findViewById<ConstraintLayout>(R.id.pastDueConstraint)
        pastDueConstraint?.setOnClickListener {
            if (pastDueDataList.isNotEmpty()) {
                val pastDueRecyclerView =
                    requireView().findViewById<RecyclerView>(R.id.pastDueRecyclerView)
                val pastDueChevron = requireView().findViewById<ImageView>(R.id.pastDueChevronImage)

                if (pastDueRecyclerView.visibility == View.GONE) {
                    pastDueRecyclerView.visibility = View.VISIBLE
                    pastDueChevron.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                } else {
                    pastDueRecyclerView.visibility = View.GONE
                    pastDueChevron.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                }
            }
            else {
                Toast.makeText(requireContext(), "There is no past due assignments", Toast.LENGTH_SHORT).show()
            }
        }

        val doneConstraint = activity?.findViewById<ConstraintLayout>(R.id.doneConstraint)
        doneConstraint?.setOnClickListener {
            if (doneDataList.isNotEmpty()) {
                val doneRecyclerView =
                    requireView().findViewById<RecyclerView>(R.id.doneRecyclerView)
                val doneChevron = requireView().findViewById<ImageView>(R.id.doneChevronImage)

                if (doneRecyclerView.visibility == View.GONE) {
                    doneRecyclerView.visibility = View.VISIBLE
                    doneChevron.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                } else {
                    doneRecyclerView.visibility = View.GONE
                    doneChevron.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                }
            }
            else {
                Toast.makeText(requireContext(), "There are no finished assignments", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addAssignment(name: String, className: String, date: String, notes: String) {

        val dbHandler = AssignmentsDBHelper(requireContext(), null)

        dbHandler.insertRow(name, date, notes, "", className)
    }

    @SuppressLint("Range")
    fun loadIntoList() {
        val dbHandler = AssignmentsDBHelper(requireActivity().applicationContext, null)

        val upcomingChevron =
            requireView().findViewById<ImageView>(R.id.upcomingChevronImage)
        val upcomingRecyclerView = requireView().findViewById<RecyclerView>(R.id.upcomingRecyclerView)

        upcomingDataList.clear()
        val upcomingCursor = dbHandler.getUpcoming()
        upcomingCursor.moveToFirst()

            while (!upcomingCursor.isAfterLast) {
                val map = HashMap<String, String>()
                map["id"] = upcomingCursor.getString(upcomingCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_ID))
                map["className"] = upcomingCursor.getString(upcomingCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_CLASS_NAME))
                map["assignmentName"] =
                    upcomingCursor.getString(upcomingCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_ASSIGNMENT_NAME))
                map["dueDate"] =
                    upcomingCursor.getString(upcomingCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_ASSIGNMENT_DUE_DATE))
                map["notes"] = upcomingCursor.getString(upcomingCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_NOTES))
                upcomingDataList.add(map)

                upcomingCursor.moveToNext()

            }

        if (upcomingDataList.isEmpty()) {
            upcomingChevron.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24))
            upcomingRecyclerView.visibility = View.GONE
        }

        val pastDueChevron =
            requireView().findViewById<ImageView>(R.id.pastDueChevronImage)
        val pastDueRecyclerView = requireView().findViewById<RecyclerView>(R.id.pastDueRecyclerView)

        pastDueDataList.clear()
        val pastDueCursor = dbHandler.getPastDue()
        pastDueCursor.moveToFirst()

            while (!pastDueCursor.isAfterLast) {
                val map = HashMap<String, String>()
                map["id"] = pastDueCursor.getString(pastDueCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_ID))
                map["className"] = pastDueCursor.getString(pastDueCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_CLASS_NAME))
                map["assignmentName"] =
                    pastDueCursor.getString(pastDueCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_ASSIGNMENT_NAME))
                map["dueDate"] =
                    pastDueCursor.getString(pastDueCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_ASSIGNMENT_DUE_DATE))
                map["notes"] = pastDueCursor.getString(pastDueCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_NOTES))
                pastDueDataList.add(map)

                pastDueCursor.moveToNext()

            }

        if (pastDueDataList.isEmpty()) {
            pastDueChevron.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24))
            pastDueRecyclerView.visibility = View.GONE
        }

        val doneChevron =
            requireView().findViewById<ImageView>(R.id.doneChevronImage)
        val doneRecyclerView = requireView().findViewById<RecyclerView>(R.id.doneRecyclerView)

        doneDataList.clear()
        val doneCursor = dbHandler.getDone()
        doneCursor.moveToFirst()

            while (!doneCursor.isAfterLast) {
                val map = HashMap<String, String>()
                map["id"] = doneCursor.getString(doneCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_ID))
                map["className"] = doneCursor.getString(doneCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_CLASS_NAME))
                map["assignmentName"] =
                    doneCursor.getString(doneCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_ASSIGNMENT_NAME))
                map["dueDate"] =
                    doneCursor.getString(doneCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_ASSIGNMENT_DUE_DATE))
                map["notes"] = doneCursor.getString(doneCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_NOTES))
                doneDataList.add(map)

                doneCursor.moveToNext()

            }

        if (doneDataList.isEmpty()) {
            doneChevron.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24))
            doneRecyclerView.visibility = View.GONE
        }

        upcomingRecyclerView?.layoutManager = upcomingGridLayoutManager
        upcomingRecyclerView?.adapter = upcomingAdapter

        pastDueRecyclerView?.layoutManager = pastDueGridLayoutManager
        pastDueRecyclerView?.adapter = pastDueAdapter

        doneRecyclerView?.layoutManager = doneGridLayoutManager
        doneRecyclerView?.adapter = doneAdapter

        activity?.findViewById<TextView>(R.id.upcomingCounterTextView)?.text = upcomingDataList.count().toString()
        activity?.findViewById<TextView>(R.id.pastDueCounterTextView)?.text = pastDueDataList.count().toString()
        activity?.findViewById<TextView>(R.id.doneCounterTextView)?.text = doneDataList.count().toString()
    }
}