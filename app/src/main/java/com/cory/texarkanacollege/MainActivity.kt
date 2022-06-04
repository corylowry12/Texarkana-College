package com.cory.texarkanacollege

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cory.texarkanacollege.classes.CurrentPhotoPathData
import com.cory.texarkanacollege.classes.ImagePathData
import com.cory.texarkanacollege.classes.ManagePermissions
import com.cory.texarkanacollege.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private var isLoaded: Boolean = false
    private val permissionRequestCode = 1
    private lateinit var managePermissions: ManagePermissions

    var path = ""

    val homeFragment = HomeFragment()
    val classesFragment = ClassesFragment()
    val settingsFragment = SettingsFragment()
    var gradeFragment = GradeFragment()
    val assignmentFragment = AssignmentFragment()
    var campusNewsFragment = CampusNewsFragment()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val extras = intent.extras

        if (extras != null) {
            if (resources.getBoolean(R.bool.isTablet)) {
                val bottomNav = findViewById<NavigationRailView>(R.id.bottomNav)
                bottomNav.menu.findItem(R.id.settings).isChecked = true
            }
            else {
                val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
                bottomNav.menu.findItem(R.id.settings).isChecked = true
            }

            replaceFragment(CampusMapFragment())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            replaceFragment(homeFragment)
        }
        val extras = intent.extras

        if (extras != null) {
            if (resources.getBoolean(R.bool.isTablet)) {
                val bottomNav = findViewById<NavigationRailView>(R.id.bottomNav)
                bottomNav.menu.findItem(R.id.settings).isChecked = true
            }
            else {
                val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
                bottomNav.menu.findItem(R.id.settings).isChecked = true
            }

            replaceFragment(CampusMapFragment())
        }

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

        if (resources.getBoolean(R.bool.isTablet)) {
            val bottomNav = findViewById<NavigationRailView>(R.id.bottomNav)

            bottomNav.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.home -> {
                        replaceFragment(homeFragment)
                    }
                    R.id.classes -> {
                        replaceFragment(classesFragment)
                    }
                    R.id.assignments -> {
                        replaceFragment(assignmentFragment)
                    }
                    R.id.settings -> {
                        replaceFragment(settingsFragment)
                    }
                }
                true
            }
        }
        else {
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

            bottomNav.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.home -> {
                        replaceFragment(homeFragment)
                    }
                    R.id.classes -> {
                        replaceFragment(classesFragment)
                    }
                    R.id.assignments -> {
                        replaceFragment(assignmentFragment)
                    }
                    R.id.settings -> {
                        replaceFragment(settingsFragment)
                    }
                }
                true
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

          setContentView(R.layout.activity_main)
    }

    fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.replace(R.id.fragment_container, fragment).addToBackStack(null)
        transaction.commit()

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putAll(outPersistentState)
    }

    fun textViewVisibilityClasses() {
        classesFragment.textViewVisibility()
    }

    fun textViewVisibilityGrades() {
        gradeFragment.textViewVisibility()
    }

    fun hideKeyboardCampusNews() {
        campusNewsFragment.hideKeyboard()
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

    val showImagePicker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            ImagePathData(this).setPath("")
            val data = result.data
            val selectedImage =
                Objects.requireNonNull(data)!!.data
            var imageStream: InputStream? = null
            try {
                imageStream =
                    this.contentResolver?.openInputStream(
                        selectedImage!!
                    )
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            val imageBitmap = BitmapFactory.decodeStream(imageStream)
            val stream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()
            val selectedFile = File(getRealPathFromURI(selectedImage!!))
            ImagePathData(this).setPath(selectedFile.toString())// To display selected image in image view
        }
    }

    fun getRealPathFromURI(contentURI: Uri) : String {
        var result = ""
        val cursor = this.contentResolver?.query(contentURI, null, null, null, null)
        if (cursor == null) {
            result = contentURI.path.toString()
        }
        else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    fun deleteAll() {
        classesFragment.deleteAll()
    }

    val showCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {

            val ei = ExifInterface(CurrentPhotoPathData(this).loadPhotoPath())
            val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

            val m = Matrix()
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90f)
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                m.postRotate(180f)
            }
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270f)
            }

            val originalBitmap = BitmapFactory.decodeFile(CurrentPhotoPathData(this@MainActivity).loadPhotoPath())
            Toast.makeText(this, CurrentPhotoPathData(this@MainActivity).loadPhotoPath(), Toast.LENGTH_SHORT).show()
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
                .format(System.currentTimeMillis())
            val storageDir = File(
                Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/TexarkanaCollege/");

            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
            val image = File.createTempFile(timeStamp, ".jpeg", storageDir)

            val f = File(image.toString())
            val fileOutputStream = FileOutputStream(f)
            val rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, m, true)
            val bitmap = rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            MediaScannerConnection.scanFile(this, arrayOf(image.toString()), null, null)

             ImagePathData(this).setPath(image.toString())
        }
    }

    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val storageDir: File = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".png", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            CurrentPhotoPathData(this@MainActivity).setPhotoPath(absolutePath)
        }
    }

    fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(this.packageManager) != null) {
            var photFile : File? = null

            try {
                photFile = createImageFile()
            }
            catch (e : IOException) {
                e.printStackTrace()
            }

            if (photFile != null) {
                val photoUri = FileProvider.getUriForFile(this.applicationContext, "com.cory.texarkanacollege.FileProvider", photFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                showCamera.launch(intent)
            }
        }
    }
}