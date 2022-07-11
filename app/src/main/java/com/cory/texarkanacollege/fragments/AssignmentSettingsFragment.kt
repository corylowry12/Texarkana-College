package com.cory.texarkanacollege.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.*
import com.google.android.material.appbar.MaterialToolbar

class AssignmentSettingsFragment : Fragment() {

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

        val defaultCategoryData = DefaultCategoryData(requireContext())
        val examDefaultCategory = activity?.findViewById<RadioButton>(R.id.examCategory)
        val homeworkDefaultCategory = activity?.findViewById<RadioButton>(R.id.homeworkCategory)
        val otherDefaultCategory = activity?.findViewById<RadioButton>(R.id.otherCategory)

        if (defaultCategoryData.loadDefaultCategory() == 0) {
            examDefaultCategory?.isChecked = true
        }
        else if (defaultCategoryData.loadDefaultCategory() == 1) {
            homeworkDefaultCategory?.isChecked = true
        }
        else if (defaultCategoryData.loadDefaultCategory() == 2) {
            otherDefaultCategory?.isChecked = true
        }

        examDefaultCategory?.setOnClickListener {
            defaultCategoryData.setDefaultCategory(0)
        }
        homeworkDefaultCategory?.setOnClickListener {
            defaultCategoryData.setDefaultCategory(1)
        }
        otherDefaultCategory?.setOnClickListener {
            defaultCategoryData.setDefaultCategory(2)
        }

        val rememberData = RememberRecyclerViewVisibilityForAssignments(requireContext())
        val remember = activity?.findViewById<RadioButton>(R.id.remember)
        val dontRemember = activity?.findViewById<RadioButton>(R.id.dontRemember)

        if (rememberData.loadState()) {
            remember?.isChecked = true
        }
        else {
            dontRemember?.isChecked = true
        }

        remember?.setOnClickListener {
            rememberData.setState(true)
        }
        dontRemember?.setOnClickListener {
            rememberData.setState(false)
        }
    }
}