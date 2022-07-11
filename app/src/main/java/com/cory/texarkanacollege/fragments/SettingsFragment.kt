package com.cory.texarkanacollege.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.*
import com.cory.texarkanacollege.database.AssignmentsDBHelper
import com.cory.texarkanacollege.database.ClassesDBHelper
import com.cory.texarkanacollege.database.GradesDBHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth


class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appearanceConstraint = view.findViewById<ConstraintLayout>(R.id.constraintAppearance)
        appearanceConstraint?.setOnClickListener {
            openFragment(AppearanceFragment())
        }

        val appSettingsConstraint = view.findViewById<ConstraintLayout>(R.id.constraintAppSettings)
        appSettingsConstraint.setOnClickListener {
            openFragment(AppSettingsFragment())
        }

        val assignmentSettingsConstraint = view.findViewById<ConstraintLayout>(R.id.constraintAssignmentSettings)
        assignmentSettingsConstraint?.setOnClickListener {
            openFragment(AssignmentSettingsFragment())
        }

        val experimentalConstraint = view.findViewById<ConstraintLayout>(R.id.constraintExperimental)
        val experimentalCardView = view.findViewById<CardView>(R.id.cardViewExperimental)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            experimentalCardView?.visibility = View.GONE
        }
        experimentalConstraint?.setOnClickListener {
            openFragment(ExperimentalFragment())
        }

        val campusNewsConstraint = view.findViewById<ConstraintLayout>(R.id.constraintCampusNews)
        val campusNewsCardView = view.findViewById<CardView>(R.id.cardViewCampusNews)

        if (!CampusNewsVisibleData(requireContext()).loadCampusNewsVisible()) {
            campusNewsCardView!!.visibility = View.GONE
        }
        campusNewsConstraint?.setOnClickListener {
            val campusNewsFragment = CampusNewsFragment()
            (context as MainActivity).campusNewsFragment = campusNewsFragment
            openFragment(campusNewsFragment)
        }

        val communityBoardConstraint = view.findViewById<ConstraintLayout>(R.id.communityBoardConstraint)
        val communityBoardCardView = view.findViewById<MaterialCardView>(R.id.communityBoardCardView)

        if (BottomNavWithCommunityBoard(requireContext()).loadState() || !CommunityBoardVisibileData(requireContext()).loadCommunityBoardVisible() || BottomNavContainsCommunityBoard(requireContext()).loadState()) {
            communityBoardCardView!!.visibility = View.GONE
        }

        communityBoardConstraint!!.setOnClickListener {
            openFragment(CommunityBoardFragment())
        }

        val patchNotesConstraint = view.findViewById<ConstraintLayout>(R.id.constraintPatchNotes)
        val patchNotesChevron = view.findViewById<ImageView>(R.id.patchNotesChevron)
        patchNotesConstraint?.setOnClickListener {
            openFragment(PatchNotesFragment())
        }

        if (Version(requireContext()).loadVersion() != getString(R.string.versionNumber)) {
            patchNotesChevron?.setImageResource(R.drawable.redcircle)
            patchNotesChevron?.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.redAccent
                )
            )
        }

        val whyChooseTCConstraint = view.findViewById<ConstraintLayout>(R.id.constraintWhyChooseTC)
        whyChooseTCConstraint?.setOnClickListener {
            openFragment(WhyChoooseTCFragment())
        }

        val campusMapConstraint = view.findViewById<ConstraintLayout>(R.id.constraintCampusMap)
        campusMapConstraint.setOnClickListener {
            openFragment(CampusMapFragment())
        }

        val appNewsConstraint = view.findViewById<ConstraintLayout>(R.id.constraintAppNews)
        appNewsConstraint.setOnClickListener {
            openFragment(AppNewsFragment())
        }

        val manageLinksConstraint = view.findViewById<ConstraintLayout>(R.id.constraintManageLinks)
        manageLinksConstraint.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("package:" + requireContext().packageName)
            startActivity(intent)
        }

        val socialMediaConstraint = view.findViewById<ConstraintLayout>(R.id.constraintSocialMedia)
        socialMediaConstraint.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            val viewSocialMediaLayout = layoutInflater.inflate(R.layout.social_media_bottom_sheet, null)
            dialog.setContentView(viewSocialMediaLayout)
            if (resources.getBoolean(R.bool.isTablet)) {
                val bottomSheet =
                    dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                bottomSheetBehavior.skipCollapsed = true
                bottomSheetBehavior.isHideable = false
                bottomSheetBehavior.isDraggable = false
            }
            val facebookButton = viewSocialMediaLayout.findViewById<Button>(R.id.facebookButton)
            val instagramButton = viewSocialMediaLayout.findViewById<Button>(R.id.instagramButton)
            val twitterButton = viewSocialMediaLayout.findViewById<Button>(R.id.twitterButton)
            val linkedInButton = viewSocialMediaLayout.findViewById<Button>(R.id.linkedInButton)
            val cancelButton = viewSocialMediaLayout.findViewById<Button>(R.id.cancelButton)
            facebookButton.setOnClickListener {
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.data = Uri.parse("fb://page/182268674190")
                    startActivity(intent)
                } catch (e: Exception) {
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/texarkanacollege"))
                }
            }
            instagramButton.setOnClickListener {
                val uri = Uri.parse("http://instagram.com/_u/txkcollege")
                val likeIng = Intent(Intent.ACTION_VIEW, uri)

                likeIng.setPackage("com.instagram.android")

                try {
                    startActivity(likeIng)
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/txkcollege")
                        )
                    )
                }
            }
            twitterButton.setOnClickListener {
                try {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("twitter://user?screen_name=TxkCollege")
                    )
                    startActivity(intent)
                } catch (e: java.lang.Exception) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://twitter.com/#!/TxkCollege")
                        )
                    )
                }
            }
            linkedInButton.setOnClickListener {

                val materialAlertDialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogStyle)
                materialAlertDialog.setCancelable(false)
                materialAlertDialog.setTitle("Warning")
                materialAlertDialog.setMessage("This link is gonna redirect you to outside this app, would you like to continue?")
                materialAlertDialog.setPositiveButton("Yes") { _, _ ->
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.linkedin.com/school/texarkana-college/")
                        )
                    )
                }
                materialAlertDialog.setNegativeButton("No", null)
                materialAlertDialog.show()
            }
            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        val versionInfoConstraint = view.findViewById<ConstraintLayout>(R.id.constraintVersionInfo)
        versionInfoConstraint.setOnClickListener {
            openFragment(VersionInfoFragment())
        }

        val deleteAppDataConstraint = view.findViewById<ConstraintLayout>(R.id.constraintDeleteData)
        deleteAppDataConstraint.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            val deleteDataView = layoutInflater.inflate(R.layout.delete_app_data_bottom_sheet, null)
            dialog.setContentView(deleteDataView)
            if (resources.getBoolean(R.bool.isTablet)) {
                val bottomSheet =
                    dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                bottomSheetBehavior.skipCollapsed = true
                bottomSheetBehavior.isHideable = false
                bottomSheetBehavior.isDraggable = false
            }
            val classes = deleteDataView.findViewById<Button>(R.id.deleteClassesButton)
            val grades = deleteDataView.findViewById<Button>(R.id.deleteGradesButton)
            val assignments = deleteDataView.findViewById<Button>(R.id.deleteAssignmentsButton)
            val images = deleteDataView.findViewById<Button>(R.id.deleteImagesButton)
            val settings = deleteDataView.findViewById<Button>(R.id.deleteSettingsButton)
            val everything = deleteDataView.findViewById<Button>(R.id.deleteEverythingButton)
            val cancel = deleteDataView.findViewById<Button>(R.id.cancelButton)

            classes.setOnClickListener {
                val materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogStyle)
                materialAlertDialogBuilder.setTitle("Warning")
                materialAlertDialogBuilder.setMessage("This will erase all classes, grades, and assignments. Continue?")
                materialAlertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                    ClassesDBHelper(requireContext(), null).deleteAll()
                    GradesDBHelper(requireContext(), null).deleteAll()
                    AssignmentsDBHelper(requireContext(), null).deleteAll()
                    dialog.dismiss()
                }
                materialAlertDialogBuilder.setNegativeButton("No", null)
                materialAlertDialogBuilder.show()
            }
            grades.setOnClickListener {
                val materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogStyle)
                materialAlertDialogBuilder.setTitle("Warning")
                materialAlertDialogBuilder.setMessage("This will erase all grades. Continue?")
                materialAlertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                    GradesDBHelper(requireContext(), null).deleteAll()
                    dialog.dismiss()
                }
                materialAlertDialogBuilder.setNegativeButton("No", null)
                materialAlertDialogBuilder.show()
            }
            assignments.setOnClickListener {
                val materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogStyle)
                materialAlertDialogBuilder.setTitle("Warning")
                materialAlertDialogBuilder.setMessage("This will erase all assignments. Continue?")
                materialAlertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                    AssignmentsDBHelper(requireContext(), null).deleteAll()
                    dialog.dismiss()
                }
                materialAlertDialogBuilder.setNegativeButton("No", null)
                materialAlertDialogBuilder.show()
            }
            images.setOnClickListener {
                val materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogStyle)
                materialAlertDialogBuilder.setTitle("Warning")
                materialAlertDialogBuilder.setMessage("This will erase all images for grades. Continue?")
                materialAlertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                    GradesDBHelper(requireContext(), null).deleteAllImages()
                    dialog.dismiss()
                }
                materialAlertDialogBuilder.setNegativeButton("No", null)
                materialAlertDialogBuilder.show()
            }
            settings.setOnClickListener {
                val materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
                materialAlertDialogBuilder.setTitle("Warning")
                materialAlertDialogBuilder.setMessage("This will erase all saved preferences. Continue?")
                materialAlertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                    requireContext().getSharedPreferences("file", 0).edit().clear().apply()
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(requireContext(), "App Restarting...", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent =
                            requireContext().packageManager.getLaunchIntentForPackage(requireContext().packageName)
                        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        activity?.finish()
                    }, 1500)
                }
                materialAlertDialogBuilder.setNegativeButton("No", null)
                materialAlertDialogBuilder.show()
            }
            everything.setOnClickListener {
                val materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogStyle)
                materialAlertDialogBuilder.setTitle("Warning")
                materialAlertDialogBuilder.setMessage("This will erase all saved data. Continue?")
                materialAlertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                    ClassesDBHelper(requireContext(), null).deleteAll()
                    GradesDBHelper(requireContext(), null).deleteAll()
                    AssignmentsDBHelper(requireContext(), null).deleteAll()
                    requireContext().getSharedPreferences("file", 0).edit().clear().apply()
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(requireContext(), "App Restarting...", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent =
                            requireContext().packageManager.getLaunchIntentForPackage(requireContext().packageName)
                        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        activity?.finish()
                    }, 1500)
                }
                materialAlertDialogBuilder.setNegativeButton("No", null)
                materialAlertDialogBuilder.show()
            }

            cancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    private fun openFragment(fragment: Fragment) {
        val manager =
            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        manager.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        manager.replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
        manager.commit()
    }
}