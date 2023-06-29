package com.cory.texarkanacollege.fragments

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.blue
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.DarkThemeData
import com.cory.texarkanacollege.classes.DarkWebViewData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

@OptIn(DelicateCoroutinesApi::class)
class CampusNewsInfoFragment : Fragment() {

    var link : String? = null
    var imgLink : String? = null

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
        return inflater.inflate(R.layout.fragment_campus_news_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolBar = activity?.findViewById<MaterialToolbar>(R.id.materialToolBarCampusNewsInfo)
        toolBar?.setNavigationOnClickListener {
            hideKeyboard()
            activity?.supportFragmentManager?.popBackStack()
        }

        val args = arguments
        link = args?.getString("link", "")
        imgLink = args?.getString("imgLink", "")

        if (savedInstanceState == null) {
            val circularProgressDrawable = CircularProgressDrawable(requireContext())
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.blue))
            circularProgressDrawable.start()

            val imageView = requireActivity().findViewById<ImageView>(R.id.imageView)
            Glide.with(requireContext())
                .load(imgLink)
                .centerCrop()
                .placeholder(circularProgressDrawable)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        imageView.visibility = View.INVISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {

                        Palette.Builder(resource!!.toBitmap(1920, 1080)).generate { palette ->
                            val vSwatch = palette?.dominantSwatch?.rgb
                            val color =
                                Color.rgb(
                                    255 - vSwatch!!.red,
                                    255 - vSwatch.green,
                                    255 - vSwatch.blue
                                )
                            try {

                                val topAppBar =
                                    requireActivity().findViewById<MaterialToolbar>(R.id.materialToolBarCampusNewsInfo)
                                topAppBar.setNavigationIconTint(color)

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        return false
                    }

                })

                .into(imageView)

            GlobalScope.launch(Dispatchers.IO) {
                val webView = view.findViewById<WebView>(R.id.contents)
                try {

                    val document = Jsoup.connect(link).get()
                    val name = document.select("article")
                    GlobalScope.launch(Dispatchers.Main) {
                        webView!!.loadDataWithBaseURL(
                            null,
                            "<style>img{display: inline;height: auto;max-width: 85%;}</style>$name",
                            "text/html",
                            "UTF-8",
                            null
                        )
                    }
                }
                catch (e : Exception) {
                    GlobalScope.launch(Dispatchers.Main) {
                        activity?.supportFragmentManager?.popBackStack()
                        Toast.makeText(requireContext(), getString(R.string.there_was_an_error_check_connection), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                GlobalScope.launch(Dispatchers.Main) {
                    webView?.settings?.loadWithOverviewMode = true

                    val cardView = view.findViewById<CardView>(R.id.webViewCardView)
                    cardView!!.addOnLayoutChangeListener { v, _, _, _, _, _, topWas, _, bottomWas ->
                        val heightWas = bottomWas - topWas
                        if (v.height != heightWas) {
                            val progressBar =
                                activity?.findViewById<ProgressBar>(R.id.webViewProgressBar)
                            progressBar?.visibility = View.GONE
                        }
                    }

                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                            val darkThemeData = DarkThemeData(requireContext())
                            when {
                                darkThemeData.loadState() == 1 -> {
                                    cardView.setCardBackgroundColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.darkWebViewCardBackgroundColor
                                        )
                                    )
                                }
                                darkThemeData.loadState() == 0 -> {
                                    cardView.setCardBackgroundColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.white
                                        )
                                    )
                                }
                                darkThemeData.loadState() == 2 -> {
                                    when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                                        Configuration.UI_MODE_NIGHT_NO -> {
                                            cardView.setCardBackgroundColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.white
                                                )
                                            )
                                        }
                                        Configuration.UI_MODE_NIGHT_YES -> {
                                            cardView.setCardBackgroundColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.darkWebViewCardBackgroundColor
                                                )
                                            )
                                        }
                                        Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                                            cardView.setCardBackgroundColor(
                                                ContextCompat.getColor(
                                                    requireContext(),
                                                    R.color.darkWebViewCardBackgroundColor
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    catch (e: Exception) {
                        e.printStackTrace()
                    }

                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && DarkWebViewData(
                                requireContext()
                            ).loadDarkWebView() && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                        ) {
                            webView.settings.forceDark = WebSettings.FORCE_DARK_ON
                            cardView.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.darkWebViewCardBackgroundColor
                                )
                            )
                        } else {
                            cardView.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.white
                                )
                            )
                        }
                    }
                     catch (e: Exception) {
                         e.printStackTrace()
                     }

                    val search = view.findViewById<TextInputEditText>(R.id.search)
                    val searchLayout = view.findViewById<TextInputLayout>(R.id.outlinedTextFieldSearch)
                    searchLayout.setEndIconOnClickListener {
                        search.clearFocus()
                        search.setText("")
                        webView.clearMatches()
                    }
                    searchLayout.setStartIconOnClickListener {
                        webView.findNext(true)
                    }
                    search?.setOnKeyListener(View.OnKeyListener { _, i, keyEvent ->
                        if (i == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_DOWN) {
                            search.clearFocus()
                            hideKeyboard()
                            return@OnKeyListener true
                        }
                        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
                            webView.findNext(true)
                            return@OnKeyListener true
                        }
                        false
                    })
                    search?.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            p0: CharSequence?,
                            p1: Int,
                            p2: Int,
                            p3: Int
                        ) {
                        }

                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            webView.findAllAsync(search.text.toString())
                        }


                        override fun afterTextChanged(p0: Editable?) {
                        }

                    })
                }
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    hideKeyboard()
                    activity?.supportFragmentManager?.popBackStack()
                }
            })
    }

    fun hideKeyboard() {
        try {
            val inputManager: InputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val focusedView = activity?.currentFocus

            if (view?.findViewById<TextInputEditText>(R.id.search)!!.hasFocus()) {
                view?.findViewById<TextInputEditText>(R.id.search)!!.clearFocus()
            }

            if (focusedView != null) {
                inputManager.hideSoftInputFromWindow(
                    focusedView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }
}