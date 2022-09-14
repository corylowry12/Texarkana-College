package com.cory.texarkanacollege.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.adapters.CampusNewsAdapter
import com.cory.texarkanacollege.adapters.ClassesAdapter
import com.cory.texarkanacollege.classes.DarkThemeData
import com.cory.texarkanacollege.database.ClassesDBHelper
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText

class ClassesFragment: Fragment() {

    private lateinit var classesAdapter: ClassesAdapter
    private val dataList = ArrayList<HashMap<String, String>>()
    private val selectedItems = ArrayList<HashMap<String, String>>()

    private lateinit var recyclerViewState: Parcelable

    private lateinit var gridLayoutManager: GridLayoutManager

    var days = ""

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
        return inflater.inflate(R.layout.fragment_classes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gridLayoutManager = if (resources.getBoolean(R.bool.isTablet)) {
            GridLayoutManager(requireContext(), 2)
        } else {
            GridLayoutManager(requireContext(), 1)
        }

        classesAdapter = ClassesAdapter(requireContext(), dataList)

        loadIntoList()

        val search = requireView().findViewById<TextInputEditText>(R.id.searchClasses)
        val recyclerView = requireActivity().findViewById<RecyclerView>(R.id.classesRecyclerView)

        recyclerView?.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()!!
            }
        })

        search?.setOnKeyListener(View.OnKeyListener { _, i, keyEvent ->
            if (i == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_DOWN) {
                search.clearFocus()
                hideKeyboard()
                return@OnKeyListener true
            }
            if (i == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
                hideKeyboard()
                search.clearFocus()
                return@OnKeyListener true
            }
            false
        })

        search?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                selectedItems.clear()
                try {
                    if (s.toString() != "") {
                        for (i in 0 until dataList.count()) {
                            if (dataList[i]["className"]!!.lowercase().contains(s.toString().lowercase())) {
                                selectedItems.add(dataList[i])
                            }
                            else if (dataList[i]["classTime"]!!.lowercase().contains(s.toString().lowercase())) {
                                selectedItems.add(dataList[i])
                            }
                            else {
                                recyclerView.adapter?.notifyItemRemoved(i)

                                classesAdapter = ClassesAdapter(requireContext(), selectedItems)

                                recyclerView.adapter = classesAdapter
                                recyclerView.invalidate()
                            }
                        }
                    }
                    else {
                        classesAdapter = ClassesAdapter(requireContext(), dataList)

                        recyclerView.adapter = classesAdapter
                        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                selectedItems.clear()
                try {
                    if (s.toString() != "") {
                        for (i in 0 until dataList.count()) {
                            if (dataList[i]["className"]!!.lowercase().contains(s.toString().lowercase())) {
                                selectedItems.add(dataList[i])
                            }
                            else if (dataList[i]["classTime"]!!.lowercase().contains(s.toString().lowercase())) {
                                selectedItems.add(dataList[i])
                            }
                            else {
                                recyclerView.adapter?.notifyItemRemoved(i)
                                classesAdapter = ClassesAdapter(requireContext(), selectedItems)

                                recyclerView.adapter = classesAdapter
                                recyclerView.invalidate()
                            }
                        }
                    }
                    else {
                        classesAdapter = ClassesAdapter(requireContext(), dataList)

                        recyclerView.adapter = classesAdapter
                        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                selectedItems.clear()
                try {
                    if (s.toString() != "") {
                        for (i in 0 until dataList.count()) {
                            if (dataList[i]["className"]!!.lowercase().contains(s.toString().lowercase())) {
                                selectedItems.add(dataList[i])
                            }
                            else if (dataList[i]["classTime"]!!.lowercase().contains(s.toString().lowercase())) {
                                selectedItems.add(dataList[i])
                            }
                            else {
                                recyclerView.adapter?.notifyItemRemoved(i)
                                classesAdapter = ClassesAdapter(requireContext(), selectedItems)

                                recyclerView.adapter = classesAdapter
                                recyclerView.invalidate()
                            }
                        }
                    }
                    else {
                        classesAdapter = ClassesAdapter(requireContext(), dataList)

                        recyclerView.adapter = classesAdapter
                        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        val topAppBar = activity?.findViewById<MaterialToolbar>(R.id.materialToolBarClasses)

        topAppBar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.addClass -> {

                    val daysArray :MutableList<Int> = ArrayList()

                    val dialog = BottomSheetDialog(requireContext())
                    val addGradeView = layoutInflater.inflate(R.layout.add_class_bottom_sheet, null)
                    dialog.setCancelable(false)
                    dialog.setContentView(addGradeView)
                    if (resources.getBoolean(R.bool.isTablet)) {
                        val bottomSheet =
                            dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        bottomSheetBehavior.skipCollapsed = true
                        bottomSheetBehavior.isHideable = false
                        bottomSheetBehavior.isDraggable = false
                    }
                    val nameEditText = dialog.findViewById<TextInputEditText>(R.id.name)
                    val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)
                    val addClassButton = dialog.findViewById<Button>(R.id.addClassButton)
                    dialog.show()
                    val netClassSwitch = dialog.findViewById<MaterialSwitch>(R.id.netClassSwitch)
                    val netClassSwitchConstraint = dialog.findViewById<ConstraintLayout>(R.id.switchConstraintLayout)

                    val toggleGroup = dialog.findViewById<MaterialButtonToggleGroup>(R.id.toggleGroup)

                    val mon = dialog.findViewById<MaterialButton>(R.id.mon)
                    val tue = dialog.findViewById<MaterialButton>(R.id.tue)
                    val wed = dialog.findViewById<MaterialButton>(R.id.wed)
                    val thur = dialog.findViewById<MaterialButton>(R.id.thur)
                    val fri = dialog.findViewById<MaterialButton>(R.id.fri)

                    mon?.setOnClickListener {
                        if (mon.isChecked) {
                            daysArray.add(1)
                            mon.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.toggleButtonCheckedBackground))
                        }
                        else {
                            daysArray.sort()
                            daysArray.remove(1)
                            mon.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
                        }
                    }

                    tue?.setOnClickListener {
                        if (tue.isChecked) {
                            daysArray.add(2)
                            tue.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.toggleButtonCheckedBackground))
                        }
                        else {
                            daysArray.sort()
                            daysArray.remove(2)
                            tue.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
                        }
                    }

                    wed?.setOnClickListener {
                        if (wed.isChecked) {
                            daysArray.add(3)
                            wed.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.toggleButtonCheckedBackground))
                        }
                        else {
                            daysArray.sort()
                            daysArray.remove(3)
                            wed.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
                        }
                    }

                    thur?.setOnClickListener {
                        if (thur.isChecked) {
                            daysArray.add(4)
                            thur.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.toggleButtonCheckedBackground))
                        }
                        else {
                            daysArray.sort()
                            daysArray.remove(4)
                            thur.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
                        }
                    }

                    fri?.setOnClickListener {
                        if (fri.isChecked) {
                            daysArray.add(5)
                            fri.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.toggleButtonCheckedBackground))
                        }
                        else {
                            daysArray.sort()
                            daysArray.remove(5)
                            fri.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
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
                    netClassSwitchConstraint?.setOnClickListener {
                        netClassSwitch?.isChecked = !netClassSwitch!!.isChecked
                        if (netClassSwitch.isChecked) {
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
                        if (nameEditText?.text == null || textString == "") {
                            Toast.makeText(
                                requireContext(),
                                "Class Name is required",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (netClassSwitch!!.isChecked) {
                                days = "Web"
                            } else if (!netClassSwitch.isChecked && daysArray.isEmpty()) {
                                Toast.makeText(
                                    requireContext(),
                                    "Must select a day of the week or web course",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (!netClassSwitch.isChecked && daysArray.isNotEmpty()){
                                daysArray.sort()
                                for (i in 0 until daysArray.count()) {
                                    if (daysArray[i] == 1) {
                                        days += "Mon"
                                    }
                                    if (daysArray[i] == 2) {
                                        days += if (daysArray.count() == 1) {
                                            "Tue"
                                        } else {
                                            when (i) {
                                                daysArray.count() - 1 -> {
                                                    " and Tue"
                                                }
                                                0 -> {
                                                    "Tue"
                                                }
                                                else -> {
                                                    ", Tue"
                                                }
                                            }
                                        }
                                    }
                                    if (daysArray[i] == 3) {
                                        if (daysArray.count() == 1) {
                                            days += "Wed"
                                        }
                                        else {
                                            days += when (i) {
                                                daysArray.count() - 1 -> {
                                                    " and Wed"
                                                }
                                                0 -> {
                                                    "Wed"
                                                }
                                                else -> {
                                                    ", Wed"
                                                }
                                            }
                                        }
                                    }
                                    if (daysArray[i] == 4) {
                                        days += if (daysArray.count() == 1) {
                                            "Thur"
                                        } else {
                                            when (i) {
                                                daysArray.count() - 1 -> {
                                                    " and Thur"
                                                }
                                                0 -> {
                                                    "Thur"
                                                }
                                                else -> {
                                                    ", Thur"
                                                }
                                            }
                                        }
                                    }
                                    if (daysArray[i] == 5) {
                                        days += if (daysArray.count() == 1) {
                                            "Fri"
                                        } else {
                                            if (daysArray.count() == 2) {
                                                " and Fri"
                                            } else {
                                                if (i == 0) {
                                                    "Fri"
                                                } else {
                                                    ", and Fri"
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (textString != "" && (daysArray.isNotEmpty() || netClassSwitch.isChecked)) {
                                addClass(nameEditText.text.toString().trim(), days)
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

    fun deleteAll() {
        val animation = AlphaAnimation(1f, 0f)
        animation.duration = 500
        val listView = view?.findViewById<RecyclerView>(R.id.classesRecyclerView)
        val noClassesStoredTextView = activity?.findViewById<TextView>(R.id.noClassesStoredTextView)

        val textViewAnimation = AlphaAnimation(0f, 1f)
        textViewAnimation.duration = 500
        noClassesStoredTextView?.startAnimation(textViewAnimation)

        noClassesStoredTextView?.visibility = View.VISIBLE

        listView?.startAnimation(animation)

        Handler(Looper.getMainLooper()).postDelayed({
            loadIntoList()
        }, 500)
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

            val cursor = dbHandler.getAllRow()
            cursor?.moveToFirst()

            while (!cursor!!.isAfterLast) {
                val map = HashMap<String, String>()
                map["id"] = cursor.getString(cursor.getColumnIndex(ClassesDBHelper.COLUMN_ID))
                map["className"] =
                    cursor.getString(cursor.getColumnIndex(ClassesDBHelper.COLUMN_CLASS_NAME))
                map["classTime"] = cursor.getString(cursor.getColumnIndex(ClassesDBHelper.COLUMN_CLASS_TIME))
                dataList.add(map)

                cursor.moveToNext()
            }
        val recyclerView = activity?.findViewById<RecyclerView>(R.id.classesRecyclerView)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.adapter = classesAdapter
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

    fun hideKeyboard() {
        try {
            val inputManager: InputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val focusedView = activity?.currentFocus

            if (focusedView != null) {
                inputManager.hideSoftInputFromWindow(
                    focusedView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }
}