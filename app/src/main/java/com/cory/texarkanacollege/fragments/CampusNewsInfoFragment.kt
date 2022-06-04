package com.cory.texarkanacollege.fragments

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
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
import com.google.android.material.appbar.MaterialToolbar
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_campus_news_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolBar = activity?.findViewById<MaterialToolbar>(R.id.materialToolBarCampusNewsInfo)
        toolBar?.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        val args = arguments
        link = args?.getString("link", "")
        imgLink = args?.getString("imgLink", "")

        if (savedInstanceState == null) {
            val circularProgressDrawable = CircularProgressDrawable(requireContext())
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
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
                        Toast.makeText(requireContext(), "Error loading image", Toast.LENGTH_SHORT)
                            .show()
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
                val document = Jsoup.connect(link).get()
                val name = document.select("article")

                GlobalScope.launch(Dispatchers.Main) {
                    val webView = view.findViewById<WebView>(R.id.contents)
                    webView?.settings?.loadWithOverviewMode = true

                    val cardView = view.findViewById<CardView>(R.id.webViewCardView)
                    cardView!!.addOnLayoutChangeListener { v, left, top, right, bottom, leftWas, topWas, rightWas, bottomWas ->
                        val heightWas = bottomWas - topWas
                        if (v.height != heightWas) {
                            val progressBar =
                                activity?.findViewById<ProgressBar>(R.id.webViewProgressBar)
                            progressBar?.visibility = View.GONE
                        }
                    }

                    webView!!.loadDataWithBaseURL(
                        null,
                        "<style>img{display: inline;height: auto;max-width: 85%;}</style>" + name.toString(),
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.supportFragmentManager?.popBackStack()
                }
            })
    }
}