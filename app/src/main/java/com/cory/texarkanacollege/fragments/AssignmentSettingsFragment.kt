package com.cory.texarkanacollege.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.CategoryTextViewVisible
import com.cory.texarkanacollege.classes.ColoredBackgroundsData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class AssignmentSettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assignment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val assignmentSettingsTopAppBar = activity?.findViewById<MaterialToolbar>(R.id.topAppBarAssignmentSettings)
        assignmentSettingsTopAppBar?.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        val coloredBackgroundsData = ColoredBackgroundsData(requireContext())
        val enableBackgroundsRadioButton = activity?.findViewById<RadioButton>(R.id.enableColoredBackgrounds)
        val disableBackgroundsRadioButton = activity?.findViewById<RadioButton>(R.id.disableColoredBackgrounds)

        if (coloredBackgroundsData.loadColoredBackgrounds()) {
            enableBackgroundsRadioButton?.isChecked = true
        }
        else {
            disableBackgroundsRadioButton?.isChecked = true
        }

        enableBackgroundsRadioButton?.setOnClickListener {
            coloredBackgroundsData.setColoredBackgrounds(true)
        }
        disableBackgroundsRadioButton?.setOnClickListener {
            coloredBackgroundsData.setColoredBackgrounds(false)
        }

        val categoryTextViewVisible = CategoryTextViewVisible(requireContext())
        val enableCategoryTextView = activity?.findViewById<RadioButton>(R.id.enableCategory)
        val disableCategoryTextView = activity?.findViewById<RadioButton>(R.id.disableCategory)

        if (categoryTextViewVisible.loadCategoryTextView()) {
            enableCategoryTextView?.isChecked = true
        }
        else {
            disableCategoryTextView?.isChecked = true
        }

        enableCategoryTextView?.setOnClickListener {
            categoryTextViewVisible.setCategoryTextView(true)
        }
        disableCategoryTextView?.setOnClickListener {
            categoryTextViewVisible.setCategoryTextView(false)
        }
    }
}