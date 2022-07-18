package com.cory.texarkanacollege.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.ClassIcons
import com.cory.texarkanacollege.classes.ColoredClassGradeTextView
import com.google.android.material.appbar.MaterialToolbar

class ClassSettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_class_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val classSettingsTopAppBar = view.findViewById<MaterialToolbar>(R.id.topAppBarClassSettings)
        classSettingsTopAppBar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        val classIcons = ClassIcons(requireContext())
        val enableColoredIcons = view.findViewById<RadioButton>(R.id.enableColoredIcons)
        val disableColoredIcons = view.findViewById<RadioButton>(R.id.disableColoredIcons)

        if (classIcons.loadClassIcons()) {
            enableColoredIcons.isChecked = true
        }
        else {
            disableColoredIcons.isChecked = true
        }

        enableColoredIcons.setOnClickListener {
            classIcons.setClassIcons(true)
        }
        disableColoredIcons.setOnClickListener {
            classIcons.setClassIcons(false)
        }

        val coloredClassGradeTextView = ColoredClassGradeTextView(requireContext())
        val coloredTextView = view.findViewById<RadioButton>(R.id.coloredTextView)
        val coloredCardView = view.findViewById<RadioButton>(R.id.coloredCardView)
        val noColoredItems = view.findViewById<RadioButton>(R.id.noColoredItems)

        if (coloredClassGradeTextView.loadColoredClassTextView() == 0) {
            coloredTextView.isChecked = true
        }
        else if (coloredClassGradeTextView.loadColoredClassTextView() == 1) {
            coloredCardView.isChecked = true
        }
        else {
            noColoredItems.isChecked = true
        }

        coloredTextView.setOnClickListener {
            coloredClassGradeTextView.setColoredClassTextView(0)
        }
        coloredCardView.setOnClickListener {
            coloredClassGradeTextView.setColoredClassTextView(1)
        }
        noColoredItems.setOnClickListener {
            coloredClassGradeTextView.setColoredClassTextView(2)
        }
    }
}