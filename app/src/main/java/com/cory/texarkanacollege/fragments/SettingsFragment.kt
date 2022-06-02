package com.cory.texarkanacollege.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.R

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
        communityBoardConstraint?.setOnClickListener {
            openFragment(CommunityBoardFragment())
        }

        val patchNotesConstraint = activity?.findViewById<ConstraintLayout>(R.id.constraintPatchNotes)
        patchNotesConstraint?.setOnClickListener {
            openFragment(PatchNotesFragment())
        }

        val campusMapConstraint = view.findViewById<ConstraintLayout>(R.id.constraintCampusMap)
        campusMapConstraint.setOnClickListener {
            openFragment(CampusMapFragment())
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
}