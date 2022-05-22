package com.cory.texarkanacollege.fragments

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cory.texarkanacollege.adapters.ClassesAdapter
import com.cory.texarkanacollege.database.ClassesDBHelper
import com.cory.texarkanacollege.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.suke.widget.SwitchButton

class ClassesFragment: Fragment() {

    private lateinit var classesAdapter: ClassesAdapter
    private val dataList = ArrayList<HashMap<String, String>>()

    private lateinit var gridLayoutManager: GridLayoutManager

    var days = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_classes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (resources.getBoolean(R.bool.isTablet)) {
            gridLayoutManager = GridLayoutManager(requireContext(), 2)
        }
        else {
            gridLayoutManager = GridLayoutManager(requireContext(), 1)
        }

        classesAdapter = ClassesAdapter(requireContext(), dataList)

        loadIntoList()

        val topAppBar = activity?.findViewById<MaterialToolbar>(R.id.materialToolBarClasses)

        topAppBar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.addClass -> {
                   /* val input = TextInputEditText(requireContext())
                    input.setHint("Class Name")
                    input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
                    val frameLayout = FrameLayout(requireContext())
                    val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    params.topMargin = 30
                    params.bottomMargin = 30
                    params.leftMargin = 10
                    params.rightMargin = 10
                    input.layoutParams = params
                    frameLayout.addView(input)
                    val dialog = MaterialAlertDialogBuilder(requireContext())
                    dialog.setView(frameLayout)
                    dialog.setPositiveButton("Add") { _, _ ->
                        val text = input.text
                        val textString = text.toString()
                        if (input.text != null && textString != "") {
                            addClass(input.text.toString())
                            loadIntoList()
                        }
                        else {
                            Toast.makeText(requireContext(), "Class Name is required", Toast.LENGTH_SHORT).show()
                        }
                    }
                    dialog.setNegativeButton("Cancel", null)
                    dialog.show()*/

                    val daysArray :MutableList<Int> = ArrayList()

                    val dialog = BottomSheetDialog(requireContext())
                    val addGradeView = layoutInflater.inflate(R.layout.add_class_bottom_sheet, null)
                    dialog.setCancelable(false)
                    dialog.setContentView(addGradeView)
                    val nameEditText = dialog.findViewById<TextInputEditText>(R.id.name)
                    val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)
                    val addClassButton = dialog.findViewById<Button>(R.id.addClassButton)
                    dialog.show()
                    val netClassSwitch = dialog.findViewById<SwitchButton>(R.id.netClassSwitch)

                    val toggleGroup = dialog.findViewById<MaterialButtonToggleGroup>(R.id.toggleGroup)

                    /*toggleGroup?.clearChecked()
                    toggleGroup?.addOnButtonCheckedListener { group, checkedId, isChecked ->
                        daysArray.clear()
                        for (i in 0 until toggleGroup.childCount) {
                            val child = toggleGroup.getChildAt(i) as MaterialButton

                            if (child.isChecked) {
                                daysArray.add(child.id)
                                Toast.makeText(requireContext(), checkedId.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }*/

                    val mon = dialog.findViewById<MaterialButton>(R.id.mon)
                    val tue = dialog.findViewById<MaterialButton>(R.id.tue)
                    val wed = dialog.findViewById<MaterialButton>(R.id.wed)
                    val thur = dialog.findViewById<MaterialButton>(R.id.thur)
                    val fri = dialog.findViewById<MaterialButton>(R.id.fri)

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

                    netClassSwitch?.setOnCheckedChangeListener { compoundButton, b ->
                        if (b) {
                            toggleGroup?.visibility = View.GONE
                        }
                        else {
                            toggleGroup?.visibility = View.VISIBLE
                        }
                    }

                    addClassButton?.setOnClickListener {
                        daysArray.sort()
                        val text = nameEditText?.text
                        val textString = text.toString()
                        if (nameEditText?.text == null && textString == "") {
                            Toast.makeText(
                                requireContext(),
                                "Class Name is required",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (netClassSwitch!!.isChecked) {
                                days = "Web"
                            } else if (!netClassSwitch.isChecked && daysArray.count() == 0) {
                                Toast.makeText(
                                    requireContext(),
                                    "Must select a day of the week or web course",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (!netClassSwitch.isChecked && daysArray.count() != 0){
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

                            if (daysArray.isEmpty() && !netClassSwitch.isChecked) {
                                Toast.makeText(requireContext(), "Must enter days or if its a web course", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                addClass(nameEditText!!.text.toString().trim(), days.toString())
                                loadIntoList()
                                dialog.dismiss()
                                days = ""
                            }
                        }
                    }

                    cancelButton?.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()
                    true
                }
                else -> false
            }
        }

        val toggleGroup = activity?.findViewById<MaterialButtonToggleGroup>(R.id.toggleGroup)
        toggleGroup?.addOnButtonCheckedListener { group, checkedId, isChecked ->

        }
    }

    private fun addClass(
        className: String,
        classTime: String
    ) {

            val dbHandler = ClassesDBHelper(requireContext(), null)

            dbHandler.insertRow(className, classTime)
    }

    @SuppressLint("Range")
    private fun loadIntoList() {
        val dbHandler = ClassesDBHelper(requireActivity().applicationContext, null)

        if (dbHandler.getCount() > 0) {
            val noClassesStoredTextView = activity?.findViewById<TextView>(R.id.noClassesStoredTextView)
            noClassesStoredTextView?.visibility = View.GONE
        } else {
            val noClassesStoredTextView = activity?.findViewById<TextView>(R.id.noClassesStoredTextView)
            noClassesStoredTextView?.visibility = View.VISIBLE

        }

        dataList.clear()
        val cursor = dbHandler.getAllRow(requireContext())
        cursor!!.moveToFirst()

        if (dbHandler.getCount() > 0) {
            while (!cursor.isAfterLast) {
                val map = HashMap<String, String>()
                map["id"] = cursor.getString(cursor.getColumnIndex(ClassesDBHelper.COLUMN_ID))
                map["className"] =
                    cursor.getString(cursor.getColumnIndex(ClassesDBHelper.COLUMN_CLASS_NAME))
                map["classTime"] =
                    cursor.getString(cursor.getColumnIndex(ClassesDBHelper.COLUMN_CLASS_TIME))
                dataList.add(map)

                cursor.moveToNext()

            }
        }

        val recyclerView = activity?.findViewById<RecyclerView>(R.id.classesRecyclerView)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.adapter = classesAdapter
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val recyclerView = activity?.findViewById<RecyclerView>(R.id.classesRecyclerView)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = GridLayoutManager(requireContext(), 2)
            recyclerView?.layoutManager = gridLayoutManager
            recyclerView?.invalidate()
        }
        else {
            gridLayoutManager = GridLayoutManager(requireContext(), 1)
            recyclerView?.layoutManager = gridLayoutManager
            recyclerView?.invalidate()
        }
    }

    fun textViewVisibility() {
        val dbHandler = ClassesDBHelper(requireActivity().applicationContext, null)

        if (dbHandler.getCount() > 0) {
            val noClassesStoredTextView = activity?.findViewById<TextView>(R.id.noClassesStoredTextView)
            noClassesStoredTextView?.visibility = View.GONE
        } else {
            val noClassesStoredTextView = activity?.findViewById<TextView>(R.id.noClassesStoredTextView)
            noClassesStoredTextView?.visibility = View.VISIBLE

        }
    }
}