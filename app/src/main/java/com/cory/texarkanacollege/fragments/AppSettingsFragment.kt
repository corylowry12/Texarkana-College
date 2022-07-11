package com.cory.texarkanacollege.fragments

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.DarkThemeData
import com.cory.texarkanacollege.classes.DefaultOpeningTabData
import com.google.android.material.appbar.MaterialToolbar

class AppSettingsFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_app_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolBar = activity?.findViewById<MaterialToolbar>(R.id.topAppBarAppSettings)
        toolBar?.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        val defaultOpeningTabData = DefaultOpeningTabData(requireContext())
        val enableDefaultHomeTab = view.findViewById<RadioButton>(R.id.enableDefaultHome)
        val enableDefaultClassesTab = view.findViewById<RadioButton>(R.id.enableDefaultClasses)
        val enableDefaultAssignmentsTab = view.findViewById<RadioButton>(R.id.enableDefaultAssignments)

        if (defaultOpeningTabData.loadDefaultTab() == 0) {
            enableDefaultHomeTab.isChecked = true
        }
        else if (defaultOpeningTabData.loadDefaultTab() == 1) {
            enableDefaultClassesTab.isChecked = true
        }
        else if (defaultOpeningTabData.loadDefaultTab() == 2) {
            enableDefaultAssignmentsTab.isChecked = true
        }

        enableDefaultHomeTab.setOnClickListener {
            defaultOpeningTabData.setDefaultTab(0)
        }
        enableDefaultClassesTab.setOnClickListener {
            defaultOpeningTabData.setDefaultTab(1)
        }
        enableDefaultAssignmentsTab.setOnClickListener {
            defaultOpeningTabData.setDefaultTab(2)
        }
    }
}