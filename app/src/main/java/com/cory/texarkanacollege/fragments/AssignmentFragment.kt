package com.cory.texarkanacollege.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.adapters.DoneAdapter
import com.cory.texarkanacollege.adapters.PastDueAdapter
import com.cory.texarkanacollege.adapters.UpcomingAdapter
import com.cory.texarkanacollege.classes.DarkThemeData
import com.cory.texarkanacollege.classes.DefaultCategoryData
import com.cory.texarkanacollege.classes.RecyclerViewVisibility
import com.cory.texarkanacollege.classes.RememberRecyclerViewVisibilityForAssignments
import com.cory.texarkanacollege.database.AssignmentsDBHelper
import com.cory.texarkanacollege.database.ClassesDBHelper
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
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
        return inflater.inflate(R.layout.fragment_assignment, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classesArray.clear()
        val dbHandler = ClassesDBHelper(requireActivity().applicationContext, null)

        val cursor = dbHandler.getAllRow()
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
                        if (resources.getBoolean(R.bool.isTablet)) {
                            val bottomSheet =
                                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                            bottomSheetBehavior.skipCollapsed = true
                            bottomSheetBehavior.isHideable = false
                            bottomSheetBehavior.isDraggable = false
                        }
                        val classesMenu =
                            addAssignmentView.findViewById<MaterialAutoCompleteTextView>(R.id.classesAutoComplete)

                        val addAssignmentButton =
                            addAssignmentView.findViewById<Button>(R.id.addAssignmentButton)
                        val assignmentName =
                            addAssignmentView.findViewById<TextInputEditText>(R.id.name)

                        val assignmentNotes = addAssignmentView.findViewById<TextInputEditText>(R.id.notes)

                        val categoryToggleGroup = addAssignmentView.findViewById<MaterialButtonToggleGroup>(R.id.categoryToggleGroup)
                        val examToggle = addAssignmentView.findViewById<MaterialButton>(R.id.examToggle)
                        val homeworkToggle = addAssignmentView.findViewById<MaterialButton>(R.id.homeworkToggle)
                        val otherToggle = addAssignmentView.findViewById<MaterialButton>(R.id.otherToggle)

                        if (DefaultCategoryData(requireContext()).loadDefaultCategory() == 0) {
                            examToggle.isChecked = true
                            examToggle.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.toggleButtonCheckedBackground
                                )
                            )
                        }
                        else if (DefaultCategoryData(requireContext()).loadDefaultCategory() == 1) {
                            homeworkToggle.isChecked = true
                            homeworkToggle.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.toggleButtonCheckedBackground
                                )
                            )
                        }
                        else if (DefaultCategoryData(requireContext()).loadDefaultCategory() == 2) {
                            otherToggle.isChecked = true
                            otherToggle.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.toggleButtonCheckedBackground
                                )
                            )
                        }

                        categoryToggleGroup.forEach {
                            examToggle.setOnClickListener {
                                if (examToggle.isChecked) {
                                    examToggle.setBackgroundColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.toggleButtonCheckedBackground
                                        )
                                    )
                                }
                                examToggle.isChecked = true

                                homeworkToggle.isChecked = false
                                homeworkToggle.setBackgroundColor(ContextCompat.getColor(
                                    requireContext(),
                                    R.color.transparent
                                ))
                                otherToggle.isChecked = false
                                otherToggle.setBackgroundColor(ContextCompat.getColor(
                                    requireContext(),
                                    R.color.transparent
                                ))
                            }
                            homeworkToggle.setOnClickListener {
                                if (homeworkToggle.isChecked) {
                                    homeworkToggle.setBackgroundColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.toggleButtonCheckedBackground
                                        )
                                    )
                                }
                                homeworkToggle.isChecked = true
                                examToggle.isChecked = false
                                examToggle.setBackgroundColor(ContextCompat.getColor(
                                    requireContext(),
                                    R.color.transparent
                                ))
                                otherToggle.isChecked = false
                                otherToggle.setBackgroundColor(ContextCompat.getColor(
                                    requireContext(),
                                    R.color.transparent
                                ))
                            }
                            otherToggle.setOnClickListener {
                                if (otherToggle.isChecked) {
                                    otherToggle.setBackgroundColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.toggleButtonCheckedBackground
                                        )
                                    )
                                }
                                otherToggle.isChecked = true
                                examToggle.isChecked = false
                                examToggle.setBackgroundColor(ContextCompat.getColor(
                                    requireContext(),
                                    R.color.transparent
                                ))
                                homeworkToggle.isChecked = false
                                homeworkToggle.setBackgroundColor(ContextCompat.getColor(
                                    requireContext(),
                                    R.color.transparent
                                ))
                            }
                        }

                        val cancelButton = addAssignmentView.findViewById<Button>(R.id.cancelButton)
                        val dueDateChip = addAssignmentView.findViewById<Button>(R.id.dueDateButton)
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                        val dateFormatted = formatter.format(Date())
                        val dateFormatted2 = formatter.parse(dateFormatted)
                        val formatter2 = SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
                        val dateFormatted3 = formatter2.format(dateFormatted2!!)
                        dueDateChip.text = dateFormatted3.toString()
                        val adapter =
                            ArrayAdapter(requireContext(), R.layout.list_item, classesArray)
                        classesMenu.setAdapter(adapter)
                        classesMenu.setText(classesArray.elementAt(0), false)

                        val darkThemeData = DarkThemeData(requireContext())
                        when {
                            darkThemeData.loadState() == 1 -> {
                                classesMenu.setDropDownBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.darkMenuBackground)))
                            }
                            darkThemeData.loadState() == 0 -> {
                                classesMenu.setDropDownBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.lightMenuBackground)))
                            }
                            darkThemeData.loadState() == 2 -> {
                                when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                                    Configuration.UI_MODE_NIGHT_NO -> {
                                        classesMenu.setDropDownBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.lightMenuBackground)))
                                    }
                                    Configuration.UI_MODE_NIGHT_YES -> {
                                        classesMenu.setDropDownBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.darkMenuBackground)))
                                    }
                                    Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                                        classesMenu.setDropDownBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.darkMenuBackground)))
                                    }
                                }
                            }
                        }

                        var date = dateFormatted

                        dueDateChip.setOnClickListener {
                            val datePicker = DatePickerDialog(
                                requireContext(), DarkThemeData(requireContext()).dateDialogTheme(requireContext())
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

                                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                                val dateFormattedSimple = simpleDateFormat.format(calendar.time)
                                val dateFormattedSimple2 = simpleDateFormat.parse(dateFormattedSimple)
                                val formatterSimple = SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
                                val dateFormattedSimple3 = formatterSimple.format(dateFormattedSimple2!!)
                                dueDateChip.text = dateFormattedSimple3
                                date = dateFormattedSimple
                                datePicker.dismiss()
                            }
                        }

                        addAssignmentButton.setOnClickListener {
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

                            if (assignmentName.text.toString() == "") {
                                Toast.makeText(requireContext(), "An assignment name is required", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                addAssignment(
                                    assignmentName.text.toString(),
                                    classesMenu.text.toString(),
                                    date.toString(),
                                    assignmentNotes.text.toString()
                                ,category)
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

        val upcomingRecyclerView =
            requireView().findViewById<RecyclerView>(R.id.upcomingRecyclerView)
        val upcomingChip =
            requireView().findViewById<Chip>(R.id.upcomingChip)

        val pastDueRecyclerView =
            requireView().findViewById<RecyclerView>(R.id.pastDueRecyclerView)
        val pastDueChip = requireView().findViewById<Chip>(R.id.pastDueChip)

        val doneRecyclerView =
            requireView().findViewById<RecyclerView>(R.id.doneRecyclerView)
        val doneChip = requireView().findViewById<Chip>(R.id.doneChip)

        if (RememberRecyclerViewVisibilityForAssignments(requireContext()).loadState()) {
            if (RecyclerViewVisibility(requireContext()).loadUpcoming()) {
                upcomingRecyclerView.visibility = View.VISIBLE
                upcomingChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24)
            }
            else {
                upcomingRecyclerView.visibility = View.GONE
                upcomingChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24)
            }

            if (RecyclerViewVisibility(requireContext()).loadPastDue()) {
                pastDueRecyclerView.visibility = View.VISIBLE
                pastDueChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24)
            }
            else {
                pastDueRecyclerView.visibility = View.GONE
                pastDueChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24)
            }

            if (RecyclerViewVisibility(requireContext()).loadDone()) {
                doneRecyclerView.visibility = View.VISIBLE
                doneChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24)
            }
            else {
                doneRecyclerView.visibility = View.GONE
                doneChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
        }

        val upcomingConstraint = activity?.findViewById<ConstraintLayout>(R.id.UpcomingConstraint)
        upcomingConstraint?.setOnClickListener {
            if (upcomingDataList.isNotEmpty()) {

                if (upcomingRecyclerView.visibility == View.GONE) {
                    RecyclerViewVisibility(requireContext()).setUpcoming(true)
                    upcomingRecyclerView.visibility = View.VISIBLE
                    upcomingChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24)
                } else {
                    RecyclerViewVisibility(requireContext()).setUpcoming(false)
                    upcomingRecyclerView.visibility = View.GONE
                    upcomingChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24)
                }
            }
            else {
                Toast.makeText(requireContext(), getString(R.string.there_are_no_upcoming_assignments), Toast.LENGTH_SHORT).show()
            }
        }

        val pastDueConstraint = activity?.findViewById<ConstraintLayout>(R.id.pastDueConstraint)
        pastDueConstraint?.setOnClickListener {
            if (pastDueDataList.isNotEmpty()) {

                if (pastDueRecyclerView.visibility == View.GONE) {
                    RecyclerViewVisibility(requireContext()).setPastDue(true)
                    pastDueRecyclerView.visibility = View.VISIBLE
                    pastDueChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24)
                } else {
                    RecyclerViewVisibility(requireContext()).setPastDue(false)
                    pastDueRecyclerView.visibility = View.GONE
                    pastDueChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24)
                }
            }
            else {
                Toast.makeText(requireContext(), getString(R.string.there_are_no_past_due_assignments), Toast.LENGTH_SHORT).show()
            }
        }

        val doneConstraint = activity?.findViewById<ConstraintLayout>(R.id.doneConstraint)
        doneConstraint?.setOnClickListener {
            if (doneDataList.isNotEmpty()) {

                if (doneRecyclerView.visibility == View.GONE) {
                    RecyclerViewVisibility(requireContext()).setDone(true)
                    doneRecyclerView.visibility = View.VISIBLE
                    doneChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24)
                } else {
                    RecyclerViewVisibility(requireContext()).setDone(false)
                    doneRecyclerView.visibility = View.GONE
                    doneChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24)
                }
            }
            else {
                Toast.makeText(requireContext(), getString(R.string.there_are_no_finished_assignments), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addAssignment(name: String, className: String, date: String, notes: String, category: String) {

        val dbHandler = AssignmentsDBHelper(requireContext(), null)

        dbHandler.insertRow(name, date, notes, "", className, category)
    }

    @SuppressLint("Range")
    fun loadIntoList() {
        val dbHandler = AssignmentsDBHelper(requireActivity().applicationContext, null)

        val upcomingChip =
            requireView().findViewById<Chip>(R.id.upcomingChip)
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
                try {
                    if (upcomingCursor.getString(upcomingCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_CATEGORY)) == "") {
                        map["category"] = "Other"
                    } else {
                        map["category"] =
                            upcomingCursor.getString(
                                upcomingCursor.getColumnIndex(
                                    AssignmentsDBHelper.COLUMN_CATEGORY
                                )
                            )
                    }
                } catch (e: Exception) {
                    map["category"] = "Other"
                }
                upcomingDataList.add(map)

                upcomingCursor.moveToNext()

            }

        if (upcomingDataList.isEmpty()) {
            upcomingChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24)
            upcomingRecyclerView.visibility = View.GONE
            RecyclerViewVisibility(requireContext()).setUpcoming(false)
        }

        val pastDueChip =
            requireView().findViewById<Chip>(R.id.pastDueChip)
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
                if (pastDueCursor.getString(pastDueCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_CATEGORY)) == "") {
                    map["category"] = "Other"
                }
                else {
                    map["category"] =
                        pastDueCursor.getString(pastDueCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_CATEGORY))
                }
                pastDueDataList.add(map)

                pastDueCursor.moveToNext()

            }

        if (pastDueDataList.isEmpty()) {
            pastDueChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24)
            pastDueRecyclerView.visibility = View.GONE
            RecyclerViewVisibility(requireContext()).setPastDue(false)
        }

        val doneChip =
            requireView().findViewById<Chip>(R.id.doneChip)
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
                if (doneCursor.getString(doneCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_CATEGORY)) == "") {
                    map["category"] = "Other"
                }
                else {
                    map["category"] = doneCursor.getString(doneCursor.getColumnIndex(AssignmentsDBHelper.COLUMN_CATEGORY))
                }
                doneDataList.add(map)

                doneCursor.moveToNext()

            }

        if (doneDataList.isEmpty()) {
            doneChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24)
            doneRecyclerView.visibility = View.GONE
            RecyclerViewVisibility(requireContext()).setDone(false)
        }

        upcomingRecyclerView?.layoutManager = upcomingGridLayoutManager
        upcomingRecyclerView?.adapter = upcomingAdapter

        pastDueRecyclerView?.layoutManager = pastDueGridLayoutManager
        pastDueRecyclerView?.adapter = pastDueAdapter

        doneRecyclerView?.layoutManager = doneGridLayoutManager
        doneRecyclerView?.adapter = doneAdapter

        activity?.findViewById<Chip>(R.id.upcomingChip)?.text = upcomingDataList.count().toString()
        activity?.findViewById<Chip>(R.id.pastDueChip)?.text = pastDueDataList.count().toString()
        activity?.findViewById<Chip>(R.id.doneChip)?.text = doneDataList.count().toString()
    }
}