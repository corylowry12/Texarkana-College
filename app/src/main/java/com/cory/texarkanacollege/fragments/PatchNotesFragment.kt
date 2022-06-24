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
import com.cory.texarkanacollege.classes.Version
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar

class PatchNotesFragment : Fragment() {

    private var bugFixesArray = arrayOf("Fixed issue where app would crash if you clicked on an image in the community board before it loaded", "Fixed some issues with janky scrolling in the Patch Notes view",
                                        "Fixed issue with the \"There are no comments\" text view being pushed down to the middle of the screen if you opened the community board post with no data connection",
                                        "Fixed issue with image failing to load and it not being visible and if you refreshed it still wouldn't show up", "Fixed issue with the text selection being purple instead of blue " +
                                        "blue when copying text when viewing a post", "Fixed some crashing issues when adding assignments and viewing them after the version 3.0.4 update")

    private var newFeaturesArray = arrayOf("Added the ability to click an image to dismiss it when viewing an image in the community board", "Added an in app updater to force you to update the app so you always have the latest and " +
                                            "greatest version", "Added the option to set categories for your assignments", "Added colored backgrounds for each assignment category", "Added the option to enable/disable " +
                                            "colored backgrounds for the assignments", "Added the option to enable/disable the category text view for each assignment, so you don't see it and can still have the colored background")

    private var enhancementsArray = arrayOf("Slightly smaller app size", "View will now automatically refresh if you have no data connection and recover connection when viewing a post", "Now when refreshing a post when viewing " +
                                            "it, it will reload the entire post instead of just the comments", "Redesigned the comments section for the view post to have a much nicer aesthetic", "The title bar will now be set " +
                                             "to a blue color when it collapses when the content is scrolled")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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