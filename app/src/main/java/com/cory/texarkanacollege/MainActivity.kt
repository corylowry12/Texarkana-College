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
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cory.texarkanacollege.classes.*
import com.cory.texarkanacollege.fragments.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigationrail.NavigationRailView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()

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
                if (extras.getString("widget") == "Widget") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                }
                else {
                    bottomNav.menu.findItem(R.id.home).isChecked = true
                }
            } else {
                val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
                if (extras.getString("widget") == "Widget") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                }
                else {
                    bottomNav.menu.findItem(R.id.home).isChecked = true
                }
            }

            if (extras.getString("widget") == "Widget") {
                replaceFragment(CampusMapFragment())
            }
            else if (intent.action == Intent.ACTION_VIEW) {
                val args = Bundle()
                args.putString("deepLink", intent.dataString)
                homeFragment.arguments = args
                replaceFragment(homeFragment)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSmallSettingsBadge()

        val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            val communityBoard = remoteConfig.getBoolean("community_board")
            CommunityBoardVisibileData(this).setCommunityBoardVisible(true)
        }

        if (savedInstanceState == null) {
            replaceFragment(homeFragment)
        }
        val extras = intent.extras

        if (extras != null) {
            if (resources.getBoolean(R.bool.isTablet)) {
                val bottomNav = findViewById<NavigationRailView>(R.id.bottomNav)
                if (extras.getString("widget") == "Widget") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                }
                else {
                    bottomNav.menu.findItem(R.id.home).isChecked = true
                }
            } else {
                val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
                if (extras.getString("widget") == "Widget") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                }
                else {
                    bottomNav.menu.findItem(R.id.home).isChecked = true
                }
            }

            if (extras.getString("widget") == "Widget") {
                replaceFragment(CampusMapFragment())
            }
            else if (intent.action == Intent.ACTION_VIEW) {
                val args = Bundle()
                args.putString("deepLink", intent.dataString)
                homeFragment.arguments = args
                replaceFragment(homeFragment)
            }
        }

        MobileAds.initialize(this)
        val adView = AdView(this)
        adView.adUnitId = "ca-app-pub-4546055219731501/9641132280"
        val mAdView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder()
            .build()
        mAdView.loadAd(adRequest)

        if (resources.getBoolean(R.bool.isTablet)) {
            val bottomNav = findViewById<NavigationRailView>(R.id.bottomNav)
            bottomNav.itemActiveIndicatorColor =
                ContextCompat.getColorStateList(this, R.color.itemIndicatorColor)
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
        } else {
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
            bottomNav.itemActiveIndicatorColor =
                ContextCompat.getColorStateList(this, R.color.itemIndicatorColor)
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

    private fun replaceFragment(fragment: Fragment) {
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

    fun getRealPathFromURI(contentURI: Uri): String {
        var result = ""
        val cursor = this.contentResolver?.query(contentURI, null, null, null, null)
        if (cursor == null) {
            result = contentURI.path.toString()
        } else {
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

    fun assignmentLoadIntoList() {
        assignmentFragment.loadIntoList()
    }

    private val showCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {

            val ei = ExifInterface(CurrentPhotoPathData(this).loadPhotoPath())
            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            val m = Matrix()
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90f)
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                m.postRotate(180f)
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270f)
            }

            val originalBitmap =
                BitmapFactory.decodeFile(CurrentPhotoPathData(this@MainActivity).loadPhotoPath())

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
                .format(System.currentTimeMillis())
            val storageDir = File(
                Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .toString() + "/TexarkanaCollege/"
            )

            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
            val image = File.createTempFile(timeStamp, ".jpeg", storageDir)

            val f = File(image.toString())
            val fileOutputStream = FileOutputStream(f)
            val rotatedBitmap = Bitmap.createBitmap(
                originalBitmap,
                0,
                0,
                originalBitmap.width,
                originalBitmap.height,
                m,
                true
            )
            val bitmap = rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            MediaScannerConnection.scanFile(this, arrayOf(image.toString()), null, null)

            ImagePathData(this).setPath(image.toString())
        }
    }

    private fun createImageFile(): File {
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
            var photFile: File? = null

            try {
                photFile = createImageFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (photFile != null) {
                val photoUri = FileProvider.getUriForFile(
                    this.applicationContext,
                    "com.cory.texarkanacollege.FileProvider",
                    photFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                showCamera.launch(intent)
            }
        }
    }

    fun setSmallSettingsBadge() {
        val badge =
            findViewById<BottomNavigationView>(R.id.bottomNav).getOrCreateBadge(R.id.settings)
        if (Version(this).loadVersion() != getString(R.string.versionNumber)) {
            badge.isVisible = true

            badge.backgroundColor = ContextCompat.getColor(this, R.color.redBadgeColor)
        } else {
            badge.isVisible = false
        }
    }

    fun fetchTOSJsonBadget() {
        val request = Request.Builder()
            .url("https://raw.githubusercontent.com/corylowry12/Texarkana-College/main/community_board_tos.json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("failed fetching json")
            }

            override fun onResponse(call: Call, response: Response) {

                val strResponse = response.body()!!.string()

                val jsonContact = JSONObject(strResponse)

                val jsonObjectDetail = jsonContact.getString("version")

                GlobalScope.launch(Dispatchers.Main) {
                    if (TOSJsonVersion(this@MainActivity).loadVersion() != jsonObjectDetail) {

                    }
                }
            }
        })
    }
}