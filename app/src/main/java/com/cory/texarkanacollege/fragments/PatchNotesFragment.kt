package com.cory.texarkanacollege.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cory.hourcalculator.adapters.PatchNotesBugFixesAdapter
import com.cory.hourcalculator.adapters.PatchNotesEnhancementsAdapter
import com.cory.hourcalculator.adapters.PatchNotesNewFeaturesAdapter
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.DarkThemeData
import com.cory.texarkanacollege.classes.Version
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar

class PatchNotesFragment : Fragment() {

    private var bugFixesArray = arrayOf("Fixed issue where if you would edit an assignment and set the category to \"Other\", it wouldn't change the color", "Fixed some issues with assignments not being sorted " +
                                        "to upcoming or past due properly if the assignment due date was in a different month than the current one", "Fixed issue with the date picker dialog not having the due date " +
                                        "of the assignment preselected when editing assignments", "Fixed issue with the minimum date for the date picker dialog being set to the current time even though the assignment due date was in the past",
                                        "Fixed issue where if you would edit a past due assignment and set the due date to current date or later, the updated assignment would not be removed from past due and inserted into upcoming",
                                        "Fixed issue where the add class bottom sheet wouldn't be fully expanded on some devices", "Fixed issue where the add assignment bottom sheet wouldn't be full expanded on some devices",
                                        "Fixed issue with the title bar in the classes view being slightly purple", "Fixed issue with the title bar in the grades view being slightly purple", "Fixed issue with the bottom nav bar being slightly purple, it is now a light blue",
                                        "Made the background color for dialogs slightly lighter for better legibility", "Fixed issue with the title bar in settings be slightly purple when expanded", "Fixed issue with the community board having the wrong description",
                                        "Fixed issue with the category text view section in the assignment settings having the wrong text", "Fixed issue with some bottom sheets in the assignments view not being expanded (tablets only)",
                                        "Fixed issue where you could scroll and collapse the tool bar in the grades view if there was no grades stored", "Fixed issue where you could scroll and collapse the tool bar in the classes view if there was no classes stored",
                                        "Fixed issue with the spinning circle when refreshing a post not respecting the theme", "Fixed issue with the spinning circle when refreshing the list of community board posts not respecting the theme",
                                        "Fixed issue when crashing when pressing the back button (tablets only)", "Fixed issue with in app updater saying click restart, and the app wouldn't actually restart")

    private var newFeaturesArray = arrayOf("Added the option to set a default category to be pre-selected when creating an assignment")

    private var enhancementsArray = arrayOf("Tweaked the design of the assignment settings view", "Assignments will now be sorted by the assignment due date", "Now when long pressing an assignment, the assignment name will show in the options bottom sheet so you know which assignment you are showing the options for",
                                            "App will no longer say it marked an assignment as done if it didn't or there was an error", "Tweaked the green color for class average if you have an A in that class to make it more legible",
                                            "A toast message will now be displayed when clicking to view likes if there are no likes for that post", "Changed the icon on the bottom nav bar for the classes tab", "Tweaked the design of the \"About the Community Board\" bottom sheet")

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
        return inflater.inflate(R.layout.fragment_patch_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topAppBar = activity?.findViewById<MaterialToolbar>(R.id.materialToolBarPatchNotes)

        topAppBar?.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        val bugFixesCounterTextView = requireView().findViewById<TextView>(R.id.bugFixesCounterTextView)
        bugFixesCounterTextView.text = bugFixesArray.count().toString()

        val newFeaturesCounterTextView = requireView().findViewById<TextView>(R.id.newFeaturesCounterTextView)
        newFeaturesCounterTextView.text = newFeaturesArray.count().toString()

        val enhancementsCounterTextView = requireView().findViewById<TextView>(R.id.enhancementsCounterTextView)
        enhancementsCounterTextView.text = enhancementsArray.count().toString()

        val bugFixesConstraint = requireView().findViewById<ConstraintLayout>(R.id.bugFixesConstraint)

        bugFixesConstraint.setOnClickListener {
            val bugFixesRecyclerView = requireView().findViewById<RecyclerView>(R.id.bugFixesRecyclerView)
            bugFixesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            bugFixesRecyclerView.adapter = PatchNotesBugFixesAdapter(requireContext(), bugFixesArray)
            val bugFixesChevron = requireView().findViewById<ImageView>(R.id.bugFixesChevronImage)

            if (bugFixesRecyclerView.visibility == View.GONE) {
                bugFixesRecyclerView.visibility = View.VISIBLE
                bugFixesChevron.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            }
            else {
                bugFixesRecyclerView.visibility = View.GONE
                bugFixesChevron.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
        }

        val newFeaturesConstraint = requireView().findViewById<ConstraintLayout>(R.id.newFeaturesConstraint)

        newFeaturesConstraint.setOnClickListener {
            val newFeaturesRecyclerView = requireView().findViewById<RecyclerView>(R.id.newFeaturesRecyclerView)
            newFeaturesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            newFeaturesRecyclerView.adapter = PatchNotesNewFeaturesAdapter(requireContext(), newFeaturesArray)
            val newFeaturesChevron = requireView().findViewById<ImageView>(R.id.newFeaturesChevronImage)

            if (newFeaturesRecyclerView.visibility == View.GONE) {
                newFeaturesRecyclerView.visibility = View.VISIBLE
                newFeaturesChevron.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            }
            else {
                newFeaturesRecyclerView.visibility = View.GONE
                newFeaturesChevron.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
        }

        val enhancementsConstraint = requireView().findViewById<ConstraintLayout>(R.id.enhancementsConstraint)

        enhancementsConstraint.setOnClickListener {
            val enhancementsRecyclerView = requireView().findViewById<RecyclerView>(R.id.enhancementsRecyclerView)
            enhancementsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            enhancementsRecyclerView.adapter = PatchNotesEnhancementsAdapter(requireContext(), enhancementsArray)
            val enhancementsChevron = requireView().findViewById<ImageView>(R.id.enhancementsChevronImage)

            if (enhancementsRecyclerView.visibility == View.GONE) {
                enhancementsRecyclerView.visibility = View.VISIBLE
                enhancementsChevron.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            }
            else {
                enhancementsRecyclerView.visibility = View.GONE
                enhancementsChevron.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
        }

        Version(requireContext()).setVersion(getString(R.string.versionNumber))

        val runnable = Runnable {
            (context as MainActivity).setSmallSettingsBadge()
        }
        MainActivity().runOnUiThread(runnable)

    }
}