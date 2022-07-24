package com.cory.texarkanacollege.fragments

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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

class PatchNotesFragment : Fragment() {

    private var bugFixesArray = arrayOf("General Stability Improvements", "Fixed issue with the class options bottom sheet not expanding all the way (tablets only)", "Fixed issue with the grade options bottom sheet not expanding all the way (tablets only)",
                                        "Fixed issue with the options bottom sheet not dismissing when clicking the View likes button in the community board", "Fixed issue where you could click outside the restart dialog after an update was downloaded to dismiss the dialog",
                                        "Fixed issue where if you opened the campus map via the campus map widget and click the back arrow in the navigation bar, the active tab on the bottom navigation view wouldn't update",
                                        "Fixed issue with app saying press restart to install update, and it wouldn't actually restart to install the update", "Fixed issue with the tool bar being to dark when its collapsed in the assignment settings view",
                                        "Fixed issue with crashing when viewing a community board post and long pressing a comment if you were not signed in", "Fixed issue with showing the wrong people who liked a post in the community board under certain conditions",
                                        "Fixed issue with crashing when viewing likes for a post in the community board under certain conditions", "Fixed issue where if you were not logged in in the community board and clicked on a post and commented, or liked and logged in and went back, the menu would still say \"Sign In\"",
                                        "Fixed issue with the community board title bar being slightly off color", "Fixed issue with comments having the wrong date format when submitting them in the community board", "Fixed issue where if you swiped down to refresh a community board post, it would never stop \"refreshing\"",
                                        "Fixed issue where you could scroll in the assignments view and collapse the tool bar even if there were no assignments stored", "Fixed issue with images being too tall and causing items to be too tall and text being off-centered in the grades view",
                                        "Fixed crashing in the classes view when creating icons if your class name started with an \"R\"",
                                        "Fixed issue where if you would go and edit and add an image to two grades, it would remove both images unless you left and reentered the view", "Fixed issue where the text would still be black if you had a 0 grade but actually had grades stored",
                                        "Fixed issue with the view image dialog when adding a grade not matching the themes for the rest of the dialogs", "Fixed crashing if you went to edit a grade and didn't have permission to add an image, it will now request permission",
                                        "Fixed issue with the title bar in the classes view being the wrong color when collapsed", "Fixed an issue where when clicking the \"View Image\" button, the image would be rotated for certain images", "Fixed issue where if you edited a grade it would remove the image under certain conditions",
                                        "Fixed an issue where if you edited a grade and did not change the image, it would remove the image", "Fixed issue where sometimes when showing an image, the navigation arrow color would be the same color as the background",
                                        "Fixed issue with crashing if you went to select a photo for a grade and it was just a blank photo", "Fixed issue where if you opened the campus news and scrolled all the way to the bottom and loaded all items, it would show a toast message saying \"There is no more news\" everytime the scroll was changed",
                                        "Fixed issue where the recycler view would flicker and not animate properly when deleting a single grade item", "Fixed issue in campus news where if you tried to keep scrolling if there was no more pages loaded, it would skip pages when fetching more news",
                                        "Fixed issue where if the search text box in the campus news view had focus and was showing the \"Paste\" options and you selected a news item, those options would not disappear",
                                        "Fixed issue with crashing in the campus news view when trying to load more if you had no data connection", "Fixed issue with crashing when clicking the load all button in the campus news view if you had no data connection",
                                        "Fixed issue with crashing if you clicked on an item in the campus news view and had no connection", "Fixed some issues with janky scrolling if you scrolled while more items were loading in the campus news view")

    private var newFeaturesArray = arrayOf("Added an option in the settings to view other social media pages", "Added the ability to set the app theme to light, dark, or follow system in Settings->Appearance",
                                            "Added the ability to like comments by swiping to the left in the community board", "Added the ability to set a default opening tab when you open the app",
                                            "Added the option in the Assignment Settings view (Settings -> Assignment Settings) to remember the visibility of items in the assignments view, so if you were looking at something it will still be visible",
                                            "Added a section in the settings where you can easily and conveniently delete app data", "Added the ability to click the date for a post and alternate between the date or absolute time",
                                            "Added an icon in the grades view to display the letter grade of that item", "Added colored text to the grade in the grades view (Similar to what's in the Classes view)",
                                            "Added the option to have each class item be a certain color based on the grade instead of just the text", "Added the option to disable colored text for grade averages in the class view (Settings -> Class Settings)",
                                            "Added the ability to backup and restore data within the app (Settings -> Backup)", "Added the ability to search for classes via a search bar in the classes view", "Added the ability to search by page number in the campus news view",
                                            "Added the ability to search for text within the content in the campus news info view")

    private var enhancementsArray = arrayOf("Added support for themed icons on Android 13+", "Redesigned the need permissions dialog", "Moved community board to the bottom nav bar", "There will no longer be labels describing what tab you are on", "Tweaked the colors of the web class switch when adding a class",
                                            "The heart icon when liking a post will always be white instead of red if its liked like before", "Tweaked the colors of the switch to mark a post as urgent", "Tweaked the design of the sign out menu in the community board",
                                            "Migrated the dark webview switch to Settings->Appearance", "Tweaked the colors of the webview dark mode switch to set the webview to be dark", "When loading post in the community board it will no longer show the loading dialog",
                                            "Redesigned the items in the community board", "The background of the card view when viewing an urgent post will now be red if its urgent in the view post view", "Community board posts will now show how long ago it was posted instead of the date and time",
                                            "The web views within the app will now automatically be dark theme if you are on Android 10+", "Removed the email text view from the items in the community board, now to view email you have to click on the name text view",
                                            "Items will now animate when clicked or long pressed in the community board", "Classes will now animate when clicked or long pressed", "The colored text when the class average is a D will now be dark orange instead of red", "Tweaked the colors of the colored text to make them more legible",
                                            "Improved some animations", "Performance improvements throughout the app", "The status bar when viewing an image will now match the background color of the view", "Improved the animation when collapsing toolbars throughout the app",
                                            "Added a search icon to the search bar in the campus news view", "Performance improvements when adding photo or taking photo when adding a grade", "Added a margin to the top of the card view when viewing an assignment",
                                            "Improved performance when loading images", "Improved animations when loading images", "Changed the design of the cancel button so you can distinguish it easier at a glance", "Changed the design of the delete grade dialog",
                                            "Changed the design of the delete all grades dialog", "Tweaked the sizing of the add image dialog for grades", "Changed the design of the fetching news dialog", "Changed the design of the fetching all news dialog",
                                            "Added a separator to display what page number each item is on in the campus news view", "Improved animation when delete all classes", "Added an animation when deleting all grades for a class", "Redesigned the delete class dialog",
                                            "Redesigned the delete all classes dialog", "Reworded some dialogs to make them easier to understand", "Improved performance when loading grades if they contain images", "Added a badge to the classes view that displays the number of grades for that class",
                                            "Redesigned the view image dialog")

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

        Version(requireContext()).setVersion(getString(R.string.build_number))

        val runnable = Runnable {
            (context as MainActivity).setSmallSettingsBadge()
        }
        MainActivity().runOnUiThread(runnable)
    }
}