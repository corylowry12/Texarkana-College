package com.cory.texarkanacollege

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.*
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.ads.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.RuntimeException

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private var isLoaded: Boolean = false
    private val permissionRequestCode = 1
    private lateinit var managePermissions: ManagePermissions

    val homeFragment = HomeFragment()
    val classesFragment = ClassesFragment()
    private lateinit var gradeFragment: GradeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*// Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this)
        val adView = AdView(this)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = "ca-app-pub-4546055219731501/9641132280"
        val mAdView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder()
            .build()
        mAdView.loadAd(adRequest)
        mAdView.adListener = object : AdListener() {

        }

        refreshLayout = findViewById(R.id.refreshLayout)
        webView = findViewById(R.id.webView)

        val list = listOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        managePermissions = ManagePermissions(this, list, permissionRequestCode)

        managePermissions.checkPermissions(this)

        loadWebview()*/

        replaceFragment(homeFragment)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(homeFragment)
                R.id.classes -> replaceFragment(classesFragment)
            }
            true
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    /*@SuppressLint("SetJavaScriptEnabled")
    private fun loadWebview() {
        val url = "https://my.texarkanacollege.edu/ICS/"
        webView.loadUrl(url)
        webView.isVerticalScrollBarEnabled = true
        webView.isHorizontalScrollBarEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url2 = request?.url.toString()
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
                if (refreshLayout.isRefreshing) {
                    refreshLayout.isRefreshing = false
                }
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                isLoaded = false
                Snackbar.make(findViewById(R.id.constraintLayout), applicationContext.getString(R.string.please_refresh), Snackbar.LENGTH_LONG)
                    .show()
                super.onReceivedError(view, request, error)
            }
        }

        webView.setDownloadListener { url1, userAgent, contentDisposition, mimeType, _ ->
            if (managePermissions.checkPermissions(this)) {
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
                val downloadManager : DownloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                downloadManager.enqueue(request)
                Snackbar.make(findViewById(R.id.constraintLayout), applicationContext.getString(R.string.downloading), Snackbar.LENGTH_LONG)
                    .show()
            } else {
               managePermissions.showAlert(this)
            }
        }

        val settings = webView.settings
        settings.domStorageEnabled = true
        settings.javaScriptEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true

        refreshLayout.setOnRefreshListener { webView.reload() }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            permissionRequestCode -> {
                val isPermissionGranted = managePermissions.processPermissionsResult(requestCode, permissions, grantResults)
                if(isPermissionGranted) {
                    Snackbar.make(findViewById(R.id.constraintLayout), applicationContext.getString(R.string.permissions_granted), Snackbar.LENGTH_LONG)
                        .show()
                }
                else {
                    Snackbar.make(findViewById(R.id.constraintLayout), applicationContext.getString(R.string.permissions_denied), Snackbar.LENGTH_LONG)
                        .show()
                }
                return
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        webView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView.restoreState(savedInstanceState)
    }

    private var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack()
        }
        else {
            if(doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }
           this.doubleBackToExitPressedOnce = true
            Snackbar.make(findViewById(R.id.constraintLayout), applicationContext.getString(R.string.please_click_back_again), Snackbar.LENGTH_LONG)
                .show()

            Looper.myLooper()?.let {
                Handler(it).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
        }
    }*/
}