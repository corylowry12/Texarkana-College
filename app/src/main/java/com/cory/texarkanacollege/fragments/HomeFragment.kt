package com.cory.texarkanacollege.fragments

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.classes.ManagePermissions
import com.cory.texarkanacollege.R
import com.google.android.material.snackbar.Snackbar
import java.lang.IllegalStateException

class HomeFragment : Fragment() {

    var webViewState = Bundle()

    private lateinit var webView: WebView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var progressBar : ProgressBar

    private var isLoaded: Boolean = false
    private val permissionRequestCode = 1
    private lateinit var managePermissions: ManagePermissions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshLayout = requireView().findViewById(R.id.refreshLayout)
        webView = requireView().findViewById(R.id.webView)
        progressBar = requireView().findViewById<ProgressBar>(R.id.progressBar)

        val list = listOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        managePermissions = ManagePermissions(requireActivity(), list, permissionRequestCode)

        if (!managePermissions.checkPermissions(requireContext())) {
            managePermissions.showAlert(requireContext())
        }

        var url = "https://my.texarkanacollege.edu/ICS/"

        val deepLink = arguments?.getString("deepLink", "")
        if (deepLink != "" && deepLink != null) {
            url = deepLink.toString()
        }

        if (webViewState.isEmpty) {
            webView.loadUrl(url)
        }
        else {
            webView.restoreState(webViewState)
        }

        progressBar.visibility = View.VISIBLE
        webView.visibility = View.GONE

        webView.isVerticalScrollBarEnabled = true
        webView.isHorizontalScrollBarEnabled = true
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url2 = request?.url.toString()
                progressBar.visibility = View.VISIBLE
                if(url2.contains("LearningToolsPortlet")) {
                    view?.loadUrl("javascript:$url2")
                    return true
                }
                else {
                    view?.loadUrl(url2)
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                isLoaded = true
                progressBar.visibility = View.INVISIBLE
                webView.visibility = View.VISIBLE
                if (refreshLayout.isRefreshing) {
                    refreshLayout.isRefreshing = false
                }
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                isLoaded = false
                super.onReceivedError(view, request, error)
            }
        }

        webView.setDownloadListener { url1, userAgent, contentDisposition, mimeType, _ ->
            if (managePermissions.checkPermissions(requireContext())) {
                val request = DownloadManager.Request(Uri.parse(url1))
                request.setMimeType(mimeType)
                val cookies = CookieManager.getInstance().getCookie(url1)
                request.addRequestHeader("cookie", cookies)
                request.addRequestHeader("User-Agent", userAgent)
                request.setDescription("Downloading file...")
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
                //request.allowScanningByMediaScanner()
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType))
                val downloadManager : DownloadManager = requireActivity().baseContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                downloadManager.enqueue(request)
                val snackBar = Snackbar.make(requireView().findViewById(R.id.constraintLayout), requireContext().getString(
                    R.string.downloading
                ), Snackbar.LENGTH_LONG)
                snackBar.apply {
                    snackBar.view.background = ResourcesCompat.getDrawable(context.resources, R.drawable.snackbar_corners, context.theme)
                }
                    snackBar.show()
            } else {
                managePermissions.showAlert(requireContext())
            }
        }



        val settings = webView.settings
        settings.domStorageEnabled = true
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        refreshLayout.setOnRefreshListener { webView.reload() }

        var doubleBackToExitPressedOnce = false

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    }
                    else {
                        if (doubleBackToExitPressedOnce) {
                            activity?.finishAffinity()
                        } else {
                            doubleBackToExitPressedOnce = true

                            Toast.makeText(
                                requireContext(),
                                "Press BACK again to exit",
                                Toast.LENGTH_SHORT
                            ).show()

                            Handler(Looper.getMainLooper()).postDelayed({
                                doubleBackToExitPressedOnce = false
                            }, 2000)
                        }
                    }
                }
            })

    }

    override fun onPause() {
        super.onPause()

        webViewState = Bundle()
        webView.saveState(webViewState)
    }

    override fun onResume() {
        super.onResume()
        if (!webViewState.isEmpty) {
            webView.restoreState(webViewState)
        }
        val deepLink = arguments?.getString("deepLink", "")
        if (deepLink != "" && deepLink != null) {
            webView.loadUrl(deepLink.toString())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        webViewState = Bundle()
        webView.saveState(webViewState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        webView.restoreState(webViewState)
    }

}