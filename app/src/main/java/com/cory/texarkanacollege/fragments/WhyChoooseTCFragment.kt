package com.cory.texarkanacollege.fragments

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.DarkThemeData
import com.cory.texarkanacollege.classes.DarkWebViewData
import com.google.android.material.appbar.MaterialToolbar

class WhyChoooseTCFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_why_choose_t_c, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val materialToolbar = activity?.findViewById<MaterialToolbar>(R.id.whyChooseTCToolbar)
        materialToolbar?.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        val webView = requireActivity().findViewById<WebView>(R.id.webViewWhyChooseTC)

        webView!!.loadUrl("https://www.texarkanacollege.edu/about/why/")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && DarkWebViewData(requireContext()).loadDarkWebView()) {
            webView.settings.forceDark = WebSettings.FORCE_DARK_ON
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    }
                    else {
                        activity?.supportFragmentManager?.popBackStack()
                    }
                }
            })
    }
}