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

        val campusNewsConstraint = activity?.findViewById<ConstraintLayout>(R.id.constraintCampusNews)
        campusNewsConstraint?.setOnClickListener {
            val campusNewsFragment = CampusNewsFragment()
            (context as MainActivity).campusNewsFragment = campusNewsFragment
            val manager =
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            manager.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            manager.replace(R.id.fragment_container, campusNewsFragment)
                .addToBackStack(null)
            manager.commit()
        }

        val patchNotesConstraint = activity?.findViewById<ConstraintLayout>(R.id.constraintPatchNotes)
        patchNotesConstraint?.setOnClickListener {
            val manager =
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            manager.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            manager.replace(R.id.fragment_container, PatchNotesFragment())
                .addToBackStack(null)
            manager.commit()
        }

        val campusMapConstraint = view.findViewById<ConstraintLayout>(R.id.constraintCampusMap)
        campusMapConstraint.setOnClickListener {
            val campusNewsFragment = CampusMapFragment()

            val manager =
                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            manager.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            manager.replace(R.id.fragment_container, campusNewsFragment)
                .addToBackStack(null)
            manager.commit()
        }
    }
}