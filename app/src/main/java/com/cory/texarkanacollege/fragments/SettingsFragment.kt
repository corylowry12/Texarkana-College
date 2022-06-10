package com.cory.texarkanacollege.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.CommunityBoardVisibileData
import com.google.android.material.card.MaterialCardView
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appearanceConstraint = activity?.findViewById<ConstraintLayout>(R.id.constraintAppearance)
        appearanceConstraint?.setOnClickListener {
            openFragment(AppearanceFragment())
        }

        val campusNewsConstraint = activity?.findViewById<ConstraintLayout>(R.id.constraintCampusNews)
        campusNewsConstraint?.setOnClickListener {
            val campusNewsFragment = CampusNewsFragment()
            (context as MainActivity).campusNewsFragment = campusNewsFragment
            openFragment(campusNewsFragment)
        }

        val communityBoardConstraint = activity?.findViewById<ConstraintLayout>(R.id.communityBoardConstraint)
        val communityBoardCardView = activity?.findViewById<MaterialCardView>(R.id.communityBoardCardView)

        if (!CommunityBoardVisibileData(requireContext()).loadCommunityBoardVisible()) {
            communityBoardCardView!!.visibility = View.GONE
        }

        communityBoardConstraint!!.setOnClickListener {
            openFragment(CommunityBoardFragment())
        }

        val patchNotesConstraint = activity?.findViewById<ConstraintLayout>(R.id.constraintPatchNotes)
        patchNotesConstraint?.setOnClickListener {
            openFragment(PatchNotesFragment())
        }

        val whyChooseTCConstraint = activity?.findViewById<ConstraintLayout>(R.id.constraintWhyChooseTC)
        whyChooseTCConstraint?.setOnClickListener {
            openFragment(WhyChoooseTCFragment())
        }

        val campusMapConstraint = view.findViewById<ConstraintLayout>(R.id.constraintCampusMap)
        campusMapConstraint.setOnClickListener {
            openFragment(CampusMapFragment())
        }

        val manageLinksConstraint = view.findViewById<ConstraintLayout>(R.id.constraintManageLinks)
        manageLinksConstraint.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("package:" + requireContext().packageName)
            startActivity(intent)
        }
    }

    fun openFragment(fragment: Fragment) {
        val manager =
            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        manager.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        manager.replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
        manager.commit()
    }

    val openDefaultSettings = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->

    }
}