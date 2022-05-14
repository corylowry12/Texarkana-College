package com.cory.texarkanacollege

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.math.RoundingMode

class ClassesFragment: Fragment() {

    private lateinit var classesAdapter: ClassesAdapter
    private val dataList = ArrayList<HashMap<String, String>>()

    private lateinit var linearLayoutManager: LinearLayoutManager

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

        linearLayoutManager = LinearLayoutManager(requireContext())

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
                    val netClassSwitch = dialog.findViewById<SwitchCompat>(R.id.netClassSwitch)
                    val toggleGroup = dialog.findViewById<MaterialButtonToggleGroup>(R.id.toggleGroup)

                    toggleGroup?.addOnButtonCheckedListener { group, checkedId, isChecked ->
                        daysArray.clear()
                        for (i in 0 until toggleGroup.childCount) {
                            val child = toggleGroup.getChildAt(i) as MaterialButton
                            if (child.isChecked) {
                                Toast.makeText(requireContext(), child.id.toString(), Toast.LENGTH_SHORT).show()
                                daysArray.add(child.id)
                            }
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
                        dialog.dismiss()
                        val text = nameEditText?.text
                        val textString = text.toString()
                        if (nameEditText?.text != null && textString != "") {
                            addClass(nameEditText.text.toString())
                            loadIntoList()
                        }
                        else {
                            Toast.makeText(requireContext(), "Class Name is required", Toast.LENGTH_SHORT).show()
                        }

                        /*if (netClassSwitch!!.isChecked) {
                            days = "Web"
                        }
                        else if (!netClassSwitch.isChecked && daysArray.count() == 0) {
                            Toast.makeText(requireContext(), "Must select a day of the week or web course", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            for (i in 0 until daysArray.count()) {
                                if (daysArray[i] == 1) {
                                    days += "Mon"
                                }
                                if (daysArray[i] == 2) {
                                    if (i == daysArray.count() - 1) {
                                        days += " and Tue"
                                    } else {
                                        days += ", Tue"
                                    }
                                }
                                if (daysArray[i] == 3) {
                                    if (i == daysArray.count() - 1) {
                                        days += " and Wed"
                                    } else {
                                        days += ", Wed"
                                    }
                                }
                                if (daysArray[i] == 4) {
                                    if (i == daysArray.count() - 1) {
                                        days += " and Thur"
                                    } else {
                                        days += ", Thur"
                                    }
                                }
                                if (daysArray[i] == 5) {
                                    if (i == daysArray.count() - 1) {
                                        days += " and Fri"
                                    } else {
                                        days += ", Fri"
                                    }
                                }
                            }
                        }*/
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
    ) {

            val dbHandler = ClassesDBHelper(requireContext(), null)

            dbHandler.insertRow(className)
    }

    @SuppressLint("Range")
    private fun loadIntoList() {
        val dbHandler = ClassesDBHelper(requireActivity().applicationContext, null)

        //var y = 0.0

        dataList.clear()
        val cursor = dbHandler.getAllRow(requireContext())
        cursor!!.moveToFirst()

        while (!cursor.isAfterLast) {
            val map = HashMap<String, String>()
            map["id"] = cursor.getString(cursor.getColumnIndex(ClassesDBHelper.COLUMN_ID))
            map["className"] = cursor.getString(cursor.getColumnIndex(ClassesDBHelper.COLUMN_CLASS_NAME))
            dataList.add(map)

            //val array = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TOTAL)).toString()

            cursor.moveToNext()

        }

        val recyclerView = activity?.findViewById<RecyclerView>(R.id.classesRecyclerView)
        recyclerView?.layoutManager = linearLayoutManager
        recyclerView?.adapter = classesAdapter
    }
}