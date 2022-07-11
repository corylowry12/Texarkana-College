package com.cory.texarkanacollege.fragments

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.DarkThemeData
import com.cory.texarkanacollege.classes.DarkWebViewData
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.suke.widget.SwitchButton

class AppearanceFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_appearance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolBar = activity?.findViewById<MaterialToolbar>(R.id.materialToolBarAppearance)
        toolBar?.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        val lightTheme = view.findViewById<RadioButton>(R.id.lightTheme)
        val darkTheme = view.findViewById<RadioButton>(R.id.darkTheme)
        val followSystem = view.findViewById<RadioButton>(R.id.followSystem)
        val darkThemeData = DarkThemeData(requireContext())

        if (darkThemeData.loadState() == 0) {
            lightTheme.isChecked = true
        }
        else if (darkThemeData.loadState() == 1) {
            darkTheme.isChecked = true
        }
        else if (darkThemeData.loadState() == 2) {
            followSystem.isChecked = true
        }

        lightTheme.setOnClickListener {
            darkThemeData.setState(0)
            restartThemeChange()

            val runnable = Runnable {
                (context as MainActivity).setNavBarBackgroundColor()

            }

            MainActivity().runOnUiThread(runnable)
        }
        darkTheme.setOnClickListener {
            darkThemeData.setState(1)
            restartThemeChange()

            val runnable = Runnable {
                (context as MainActivity).setNavBarBackgroundColor()

            }

            MainActivity().runOnUiThread(runnable)
        }
        followSystem.setOnClickListener {
            darkThemeData.setState(2)
            restartThemeChange()

            val runnable = Runnable {
                (context as MainActivity).setNavBarBackgroundColor()

            }

            MainActivity().runOnUiThread(runnable)
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

    fun restartThemeChange() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.detach(this)?.commitNow()
        activity?.supportFragmentManager?.beginTransaction()
            ?.attach(this)?.commitNow()

        val collapsingToolbarLayout =
            requireView().findViewById<AppBarLayout>(R.id.appBarLayoutAppearance)

    }
}