package com.cory.texarkanacollege.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.DarkWebViewData
import com.google.android.material.appbar.MaterialToolbar
import com.suke.widget.SwitchButton

class ExperimentalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_experimental, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val experimentalTopAppBar = activity?.findViewById<MaterialToolbar>(R.id.topAppBarExperimentalSettings)
        experimentalTopAppBar?.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        val darkWebViewSwitch = activity?.findViewById<SwitchButton>(R.id.darkWebViewSwitch)
        val darkWebViewCardView = activity?.findViewById<CardView>(R.id.darkWebViewCardView)
        val darkWebViewData = DarkWebViewData(requireContext())

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            darkWebViewCardView?.visibility = View.GONE
        }

        darkWebViewSwitch?.isChecked = darkWebViewData.loadDarkWebView()

        darkWebViewCardView?.setOnClickListener {
            darkWebViewSwitch!!.isChecked = !darkWebViewSwitch.isChecked
        }
        darkWebViewSwitch?.setOnCheckedChangeListener { view, isChecked ->
            darkWebViewData.setDarkWebView(isChecked)
        }
    }
}