package com.cory.texarkanacollege.fragments

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cory.texarkanacollege.adapters.PatchNotesBugFixesAdapter
import com.cory.texarkanacollege.adapters.PatchNotesEnhancementsAdapter
import com.cory.texarkanacollege.adapters.PatchNotesNewFeaturesAdapter
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.DarkThemeData
import com.cory.texarkanacollege.classes.Version
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip

class PatchNotesFragment : Fragment() {

    private var bugFixesArray = arrayOf("Added error checking to prevent crashing in the Campus News view", "Fixed issue where if you hit the load all button in campus news and then hit the cancel button in the dialog and scrolled and forced it to load more, it would skip a page",
                                        "Fixed issue where right arrow in the classes items wasn't centered", "Fixed issue where if class or grade name was too long you couldn't press some of the buttons",
                                        "Fixed issue with janky image scaling if there was too much content in the grades list item", "Fixed issue with there being a small dot on the settings icon in the bottom nav bar even if you already viewed patch notes",
                                        "Fixed issue with text not wrapping properly if assignment name is too long", "Fixed issue with the dark webview text still showing up if you were on less than android 10", "Fixed issue with image not saving when adding a grade if you chose to take a picture",
                                        "Fixed some issues when viewing images after you already deleted the image from your gallery", "Fixed issue with text color in the uploading image dialog for the community board", "Fixed issue with toolbar icons being slightly off-centered when the toolbar was collapsed",
                                        "Fixed issue where you could go to the campus news view and try to search and hit the back arrow in the toolbar and go back and keyboard would still be shown")

    private var newFeaturesArray = arrayOf("Added a sign in button to the community board post view", "Added the option to share link, copy link, or open link in browser by long pressing an item in the campus news view")

    private var enhancementsArray = arrayOf("Moved cancel buttons to the left in certain dialogs throughout the app", "Fixed some grammatical errors throughout the app", "Tweaked colors in dark theme to help improve readability", "Tweaked the design of the text boxes throughout the app",
                                            "Redesigned some switches throughout the app", "Redesigned some menus throughout the app", "Made the text in the search bars no longer bold", "Added support for the new image picker in Android 13", "It will no longer show permission dialog on app launch",
                                            "App will no longer request read/write permissions when trying to download files on Android 10+ as they are not required", "Redesigned the grade counter in the class list item to give it a much nicer aesthetic",
                                            "Tweaked the padding of some layout items for Appearance, App Settings, Class Settings, Grade Settings, and Assignment Settings", "Tweaked the layout for all dialogs throughout the app", "Tweaked the layout for all bottom sheets throughout the app",
                                            "Removed the \"Force webview to be dark\" option on Android 13+ as it no longer works properly", "Redesigned the uploading image dialog in the community board", "Redesigned the delete post from community board dialog",
                                            "Redesigned the assignment counters in the Assignments view", "Added a 30 character limit when displaying class names in the classes view, can view the full name now by clicking on the name",
                                            "Added a 30 character limit when displaying grade names in the grades view, can view the full name now by clicking on the name", "The search icon will now be the same color as the hint in the text box in the classes and campus news views",
                                            "Tweaked the layout of the list items in the campus news view", "Redesigned the delete app data dialogs in the settings")

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

        val bugFixesChip = requireView().findViewById<Chip>(R.id.bugFixesChip)
        bugFixesChip.text = bugFixesArray.count().toString()

        val newFeaturesChip = requireView().findViewById<Chip>(R.id.newFeaturesChip)
        newFeaturesChip.text = newFeaturesArray.count().toString()

        val enhancementsChip = requireView().findViewById<Chip>(R.id.enhancementsChip)
        enhancementsChip.text = enhancementsArray.count().toString()

        val bugFixesConstraint = requireView().findViewById<ConstraintLayout>(R.id.bugFixesConstraint)

        bugFixesConstraint.setOnClickListener {
            val bugFixesRecyclerView = requireView().findViewById<RecyclerView>(R.id.bugFixesRecyclerView)
            bugFixesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            bugFixesRecyclerView.adapter = PatchNotesBugFixesAdapter(requireContext(), bugFixesArray)

            if (bugFixesRecyclerView.visibility == View.GONE) {
                bugFixesRecyclerView.visibility = View.VISIBLE
                bugFixesChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24)
            }
            else {
                bugFixesRecyclerView.visibility = View.GONE
                bugFixesChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
        }

        val newFeaturesConstraint = requireView().findViewById<ConstraintLayout>(R.id.newFeaturesConstraint)

        newFeaturesConstraint.setOnClickListener {
            val newFeaturesRecyclerView = requireView().findViewById<RecyclerView>(R.id.newFeaturesRecyclerView)
            newFeaturesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            newFeaturesRecyclerView.adapter = PatchNotesNewFeaturesAdapter(requireContext(), newFeaturesArray)

            if (newFeaturesRecyclerView.visibility == View.GONE) {
                newFeaturesRecyclerView.visibility = View.VISIBLE
                newFeaturesChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24)
            }
            else {
                newFeaturesRecyclerView.visibility = View.GONE
                newFeaturesChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
        }

        val enhancementsConstraint = requireView().findViewById<ConstraintLayout>(R.id.enhancementsConstraint)

        enhancementsConstraint.setOnClickListener {
            val enhancementsRecyclerView = requireView().findViewById<RecyclerView>(R.id.enhancementsRecyclerView)
            enhancementsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            enhancementsRecyclerView.adapter = PatchNotesEnhancementsAdapter(requireContext(), enhancementsArray)

            if (enhancementsRecyclerView.visibility == View.GONE) {
                enhancementsRecyclerView.visibility = View.VISIBLE
                enhancementsChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_up_24)
            }
            else {
                enhancementsRecyclerView.visibility = View.GONE
                enhancementsChip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
        }

        Version(requireContext()).setVersion(getString(R.string.build_number))

        val runnable = Runnable {
            (context as MainActivity).setSmallSettingsBadge()
        }
        MainActivity().runOnUiThread(runnable)
    }
}