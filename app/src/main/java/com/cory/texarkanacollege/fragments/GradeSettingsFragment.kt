package com.cory.texarkanacollege.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.GradesColoredTextView
import com.cory.texarkanacollege.classes.GradesIcons
import com.google.android.material.appbar.MaterialToolbar

class GradeSettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grade_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gradeSettingsTopAppBar = view.findViewById<MaterialToolbar>(R.id.topAppBarGradeSettings)
        gradeSettingsTopAppBar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        val gradesIcons = GradesIcons(requireContext())
        val enableColoredIcon = view.findViewById<RadioButton>(R.id.enableGradeColoredIcons)
        val disableColoredIcon = view.findViewById<RadioButton>(R.id.disableGradeColoredIcons)

        if (gradesIcons.loadGradeIcons()) {
            enableColoredIcon.isChecked = true
        }
        else {
            disableColoredIcon.isChecked = true
        }

        enableColoredIcon.setOnClickListener {
            gradesIcons.setGradeIcons(true)
        }
        disableColoredIcon.setOnClickListener {
            gradesIcons.setGradeIcons(false)
        }

        val gradesColoredTextView = GradesColoredTextView(requireContext())
        val enableGradesColoredTextView = view.findViewById<RadioButton>(R.id.enableColoredGradeTextView)
        val disableGradesColoredTextView = view.findViewById<RadioButton>(R.id.disableColoredGradeTextView)

        if (gradesColoredTextView.loadGradeColoredTextView()) {
            enableGradesColoredTextView.isChecked = true
        }
        else {
            disableGradesColoredTextView.isChecked = true
        }

        enableGradesColoredTextView.setOnClickListener {
            gradesColoredTextView.setGradeColoredTextView(true)
        }
        disableGradesColoredTextView.setOnClickListener {
            gradesColoredTextView.setGradeColoredTextView(false)
        }
    }
}