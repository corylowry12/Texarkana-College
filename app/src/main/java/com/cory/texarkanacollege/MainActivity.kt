@file:OptIn(DelicateCoroutinesApi::class)

package com.cory.texarkanacollege

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.Icon
import androidx.exifinterface.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.BuildCompat
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cory.texarkanacollege.adapters.GradesAdapter
import com.cory.texarkanacollege.classes.*
import com.cory.texarkanacollege.database.AssignmentsDBHelper
import com.cory.texarkanacollege.database.ClassesDBHelper
import com.cory.texarkanacollege.fragments.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.DelicateCoroutinesApi
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

    private val homeFragment = HomeFragment()
    private val classesFragment = ClassesFragment()
    private val settingsFragment = SettingsFragment()
    var gradeFragment = GradeFragment()
    private val assignmentFragment = AssignmentFragment()
    var campusNewsFragment = CampusNewsFragment()
    private val communityBoardFragment = CommunityBoardFragment()
    var viewPostCommunityBoardFragment = ViewCommunityBoardPostFragment()

    lateinit var gradesAdapter: GradesAdapter

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        val extras = intent.extras

        if (extras != null) {
            if (resources.getBoolean(R.bool.isTablet)) {
                val bottomNav = findViewById<NavigationRailView>(R.id.bottomNav)
                bottomNav.menu.findItem(R.id.communityBoard).isVisible = CommunityBoardVisibileData(this).loadCommunityBoardVisible()
                if (intent.getStringExtra("widget") == "Widget") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                } else if (intent.getStringExtra("view_classes") == "view_classes") {
                    bottomNav.menu.findItem(R.id.classes).isChecked = true
                } else if (intent.getStringExtra("view_assignments") == "view_assignments") {
                    bottomNav.menu.findItem(R.id.assignments).isChecked = true
                } else if (intent.getStringExtra("view_map") == "view_map") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                } else {
                    bottomNav.menu.findItem(R.id.home).isChecked = true
                }
            } else {
                val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
                bottomNav.menu.findItem(R.id.communityBoard).isVisible = CommunityBoardVisibileData(this).loadCommunityBoardVisible()
                if (intent.getStringExtra("widget") == "Widget") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                } else if (intent.getStringExtra("view_classes") == "view_classes") {
                    bottomNav.menu.findItem(R.id.classes).isChecked = true
                } else if (intent.getStringExtra("view_assignments") == "view_assignments") {
                    bottomNav.menu.findItem(R.id.assignments).isChecked = true
                } else if (intent.getStringExtra("view_map") == "view_map") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                } else {
                    bottomNav.menu.findItem(R.id.home).isChecked = true
                }
            }

            if (intent.getStringExtra("widget") == "Widget") {
                replaceFragment(CampusMapFragment())
            } else if (intent.getStringExtra("view_classes") == "view_classes") {
                replaceFragment(classesFragment)
            } else if (intent.getStringExtra("view_assignments") == "view_assignments") {
                replaceFragment(assignmentFragment)
            } else if (intent.getStringExtra("view_map") == "view_map") {
                replaceFragment(CampusMapFragment())
            } else if (intent.action == Intent.ACTION_VIEW) {
                val args = Bundle()
                args.putString("deepLink", intent.dataString)
                homeFragment.arguments = args
                replaceFragment(homeFragment)
            }
        }
    }

    @SuppressLint("Range")
    @RequiresApi(Build.VERSION_CODES.N_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val darkThemeData = DarkThemeData(this)
        when {
            darkThemeData.loadState() == 1 -> {
                setTheme(R.style.Dark)
            }
            darkThemeData.loadState() == 0 -> {
               setTheme(R.style.Theme_MyApplication)
            }
            darkThemeData.loadState() == 2 -> {
                when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_NO -> {
                       setTheme(R.style.Theme_MyApplication)
                    }
                    Configuration.UI_MODE_NIGHT_YES -> {
                       setTheme(R.style.Dark)
                    }
                    Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                        setTheme(R.style.Dark)
                    }
                }
            }
        }
        setContentView(R.layout.activity_main)

        setNavBarBackgroundColor()

        val dbHandler = AssignmentsDBHelper(this, null)
        val cursor = dbHandler.getAllRow()
        cursor!!.moveToFirst()

        while (!cursor.isAfterLast) {

            try {
                val formatter = SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
                val dateFormatted =
                    formatter.parse(cursor.getString(cursor.getColumnIndex(AssignmentsDBHelper.COLUMN_ASSIGNMENT_DUE_DATE))) as Date
                val formatter2 = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val dateFormatted2 = formatter2.format(dateFormatted)

                dbHandler.update(
                    cursor.getString(cursor.getColumnIndex(AssignmentsDBHelper.COLUMN_ID)),
                    dateFormatted2
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            cursor.moveToNext()
        }

        val classDBHandler = ClassesDBHelper(this, null)
        val classesCursor = classDBHandler.getAllRow()
        classesCursor!!.moveToFirst()

        while (!classesCursor.isAfterLast) {

            try {

                val id = classesCursor.getString(classesCursor.getColumnIndex(ClassesDBHelper.COLUMN_ID))

                classDBHandler.classIDUpdate(
                    id,
                    classesCursor.getString(classesCursor.getColumnIndex(ClassesDBHelper.COLUMN_ID)),
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            classesCursor.moveToNext()
        }

        val classesIntent = Intent(this, MainActivity::class.java)
        classesIntent.action = Intent.ACTION_VIEW
        classesIntent.putExtra("view_classes", "view_classes")

        val assignmentIntent = Intent(this, MainActivity::class.java)
        assignmentIntent.action = Intent.ACTION_VIEW
        assignmentIntent.putExtra("view_assignments", "view_assignments")

        val mapIntent = Intent(this, MainActivity::class.java)
        mapIntent.action = Intent.ACTION_VIEW
        mapIntent.putExtra("view_map", "view_map")

        try {
            val shortcutManager = getSystemService(ShortcutManager::class.java) as ShortcutManager
            val classesShortCut = ShortcutInfo.Builder(this, "classes")
                .setShortLabel("Classes")
                .setLongLabel("View Classes")
                .setIcon(Icon.createWithResource(this, R.drawable.ic_baseline_class_24_shortcut))
                .setIntent(classesIntent)
                .build()
            val assignmentsShortcut = ShortcutInfo.Builder(this, "assignments")
                .setShortLabel("Assignments")
                .setLongLabel("View Assignments")
                .setIcon(
                    Icon.createWithResource(
                        this,
                        R.drawable.ic_baseline_assignment_24_shortcut
                    )
                )
                .setIntent(assignmentIntent)
                .build()
            val mapShortcut = ShortcutInfo.Builder(this, "map")
                .setShortLabel("Map")
                .setLongLabel("View Campus Map")
                .setIcon(Icon.createWithResource(this, R.drawable.ic_baseline_map_24_shortcut))
                .setIntent(mapIntent)
                .build()
            GlobalScope.launch(Dispatchers.Main) {
                shortcutManager.dynamicShortcuts =
                    listOf(classesShortCut, assignmentsShortcut, mapShortcut)
            }
        }
        catch (e : NoClassDefFoundError) {
            e.printStackTrace()
        }

        setSmallSettingsBadge()
        //fetchTOSJsonBadge()

        val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1
        }
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            val communityBoard = remoteConfig.getBoolean("community_board_302")
            CommunityBoardVisibileData(this).setCommunityBoardVisible(communityBoard)
            val campusNews = remoteConfig.getBoolean("campus_news")
            CampusNewsVisibleData(this).setCampusNewsVisible(campusNews)
            val imageViewIntent = remoteConfig.getBoolean("image_view_intent")
            ImageViewIntentData(this).setImageView(imageViewIntent)
            val pinnedSwitchVisible = remoteConfig.getBoolean("pinned_switch")
            PinnedSwitchVisible(this).setPinnedSwitchVisible(pinnedSwitchVisible)
            val commentLikeCounter = remoteConfig.getBoolean("comment_and_like_counter")
            CommentLikeCounter(this).setCounterVisibility(commentLikeCounter)
        }

        this.cacheDir.deleteRecursively()

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
            bottomNav.menu.findItem(R.id.communityBoard).isVisible = CommunityBoardVisibileData(this).loadCommunityBoardVisible()
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
                    R.id.communityBoard -> {
                        replaceFragment(communityBoardFragment)
                    }
                    R.id.settings -> {
                        replaceFragment(settingsFragment)
                    }
                }
                true
            }
        } else {
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
            bottomNav.menu.findItem(R.id.communityBoard).isVisible = CommunityBoardVisibileData(this).loadCommunityBoardVisible()
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
                    R.id.communityBoard -> {
                        replaceFragment(communityBoardFragment)
                    }
                    R.id.settings -> {
                        replaceFragment(settingsFragment)
                    }
                }
                true
            }
        }

        if (DefaultOpeningTabData(this).loadDefaultTab() == 0) {
            if (resources.getBoolean(R.bool.isTablet)) {
                val bottomNav = findViewById<NavigationRailView>(R.id.bottomNav)
                bottomNav.menu.findItem(R.id.home).isChecked = true
            }
            else {
                val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
                 bottomNav.menu.findItem(R.id.home).isChecked = true
            }
            replaceFragment(homeFragment)
        }
        else if (DefaultOpeningTabData(this).loadDefaultTab() == 1) {
            if (resources.getBoolean(R.bool.isTablet)) {
                val bottomNav = findViewById<NavigationRailView>(R.id.bottomNav)
                bottomNav.menu.findItem(R.id.classes).isChecked = true
            }
            else {
                val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
                bottomNav.menu.findItem(R.id.classes).isChecked = true
            }
            replaceFragment(classesFragment)
        }
        else if (DefaultOpeningTabData(this).loadDefaultTab() == 2) {
            if (resources.getBoolean(R.bool.isTablet)) {
                val bottomNav = findViewById<NavigationRailView>(R.id.bottomNav)
                bottomNav.menu.findItem(R.id.assignments).isChecked = true
            }
            else {
                val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
                bottomNav.menu.findItem(R.id.assignments).isChecked = true
            }
            replaceFragment(assignmentFragment)
        }

        val extras = intent.extras

        if (extras != null) {
            if (resources.getBoolean(R.bool.isTablet)) {
                val bottomNav = findViewById<NavigationRailView>(R.id.bottomNav)
                bottomNav.menu.findItem(R.id.communityBoard).isVisible = CommunityBoardVisibileData(this).loadCommunityBoardVisible()
                if (intent.getStringExtra("widget") == "Widget") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                } else if (intent.getStringExtra("view_classes") == "view_classes") {
                    bottomNav.menu.findItem(R.id.classes).isChecked = true
                } else if (intent.getStringExtra("view_assignments") == "view_assignments") {
                    bottomNav.menu.findItem(R.id.assignments).isChecked = true
                } else if (intent.getStringExtra("view_map") == "view_map") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                } else {
                    bottomNav.menu.findItem(R.id.home).isChecked = true
                }
            } else {
                val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
                bottomNav.menu.findItem(R.id.communityBoard).isVisible = CommunityBoardVisibileData(this).loadCommunityBoardVisible()

                if (intent.getStringExtra("widget") == "Widget") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                } else if (intent.getStringExtra("view_classes") == "view_classes") {
                    bottomNav.menu.findItem(R.id.classes).isChecked = true
                } else if (intent.getStringExtra("view_assignments") == "view_assignments") {
                    bottomNav.menu.findItem(R.id.assignments).isChecked = true
                } else if (intent.getStringExtra("view_map") == "view_map") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                }
            }

            if (intent.getStringExtra("widget") == "Widget") {
                replaceFragment(CampusMapFragment())
            } else if (intent.getStringExtra("view_classes") == "view_classes") {
                replaceFragment(classesFragment)
            } else if (intent.getStringExtra("view_assignments") == "view_assignments") {
                replaceFragment(assignmentFragment)
            } else if (intent.getStringExtra("view_map") == "view_map") {
                replaceFragment(CampusMapFragment())
            }else if (intent.action == Intent.ACTION_VIEW) {
                val args = Bundle()
                args.putString("deepLink", intent.dataString)
                homeFragment.arguments = args
                replaceFragment(homeFragment)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        //setContentView(R.layout.activity_main)


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

    fun hideKeyboardClasses() {
        classesFragment.hideKeyboard()
    }

    val showImagePicker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
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
                //val byteArray = stream.toByteArray()
                val selectedFile = File(getRealPathFromURI(selectedImage!!))
                ImagePathData(this).setPath(selectedFile.toString())// To display selected image in image view
                gradesAdapter.addImageButton.text = getString(R.string.view_image)
            } catch (e : NullPointerException) {
                e.printStackTrace()
                Toast.makeText(this, getString(R.string.error_selecting_image), Toast.LENGTH_SHORT).show()
            }
        }
    }

    val showImagePickerAndroid13 = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            try {
                ImagePathData(this).setPath("")
                var imageStream: InputStream? = null
                try {
                    imageStream =
                        this.contentResolver?.openInputStream(
                            uri!!
                        )
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
                val imageBitmap = BitmapFactory.decodeStream(imageStream)
                val stream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                //val byteArray = stream.toByteArray()
                val selectedFile = File(getRealPathFromURI(uri!!))
                ImagePathData(this).setPath(selectedFile.toString())// To display selected image in image view
                gradesAdapter.addImageButton.text = getString(R.string.view_image)
            } catch (e : Exception) {
                e.printStackTrace()
                Toast.makeText(this, getString(R.string.error_selecting_image), Toast.LENGTH_SHORT).show()
                gradesAdapter.addImageButton.text = "Add Image"
            }
        }

    private fun getRealPathFromURI(contentURI: Uri): String {
        val result: String
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

    fun deleteAllGrades() {
        gradeFragment.deleteAll()
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
            gradesAdapter.addImageButton.text = getString(R.string.view_image)
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
        if (resources.getBoolean(R.bool.isTablet)) {
            val badge =
                findViewById<NavigationRailView>(R.id.bottomNav).getOrCreateBadge(R.id.settings)
            if (Version(this).loadVersion() != getString(R.string.build_number)) {
                badge.isVisible = true

                badge.backgroundColor = ContextCompat.getColor(this, R.color.redBadgeColor)
            } else {
                badge.isVisible = false
            }
        } else {
            val badge =
                findViewById<BottomNavigationView>(R.id.bottomNav).getOrCreateBadge(R.id.settings)
            if (Version(this).loadVersion() != getString(R.string.build_number)) {
                badge.isVisible = true

                badge.backgroundColor = ContextCompat.getColor(this, R.color.redBadgeColor)
            } else {
                badge.isVisible = false
            }
        }
    }

    private fun fetchTOSJsonBadge() {
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

                CurrentTOSVersion(this@MainActivity).setVersion(jsonObjectDetail.toString())
            }
        })
    }

    fun setViewPostCommunityBoardLoadIntoList() {
        viewPostCommunityBoardFragment.setTextView()
    }

    fun campusMapWidgetExit() {
        if (resources.getBoolean(R.bool.isTablet)) {
            val bottomNav = findViewById<NavigationRailView>(R.id.bottomNav)

            Handler(Looper.getMainLooper()).postDelayed({
                if (homeFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.home).isChecked = true
                } else if (classesFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.classes).isChecked = true
                } else if (assignmentFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.assignments).isChecked = true
                } else if (communityBoardFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.communityBoard).isChecked = true
                } else if (settingsFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                }
            }, 200)
        }
        else {
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

            Handler(Looper.getMainLooper()).postDelayed({
                if (homeFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.home).isChecked = true
                } else if (classesFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.classes).isChecked = true
                } else if (assignmentFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.assignments).isChecked = true
                } else if (communityBoardFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.communityBoard).isChecked = true
                } else if (settingsFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                }
            }, 200)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (resources.getBoolean(R.bool.isTablet)) {
            val bottomNav = findViewById<NavigationRailView>(R.id.bottomNav)

            Handler(Looper.getMainLooper()).postDelayed({
                if (homeFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.home).isChecked = true
                } else if (classesFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.classes).isChecked = true
                } else if (assignmentFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.assignments).isChecked = true
                } else if (communityBoardFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.communityBoard).isChecked = true
                } else if (settingsFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                }
            }, 200)
        }
        else {
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

            Handler(Looper.getMainLooper()).postDelayed({
                if (homeFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.home).isChecked = true
                } else if (classesFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.classes).isChecked = true
                } else if (assignmentFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.assignments).isChecked = true
                } else if (communityBoardFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.communityBoard).isChecked = true
                } else if (settingsFragment.isVisible) {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                }
            }, 200)
        }
    }

    fun setNavBarBackgroundColor() {
        val darkThemeData = DarkThemeData(this)
        val mainConstraint = findViewById<ConstraintLayout>(R.id.mainConstraint)
        if (resources.getBoolean(R.bool.isTablet)) {
            val bottomNav = findViewById<NavigationRailView>(R.id.bottomNav)
            when {
                darkThemeData.loadState() == 1 -> {
                    bottomNav.setBackgroundColor(
                        ContextCompat.getColor(
                            this,
                            R.color.bottomNavBarBackgroundDark
                        )
                    )
                    bottomNav.itemIconTintList = ColorStateList.valueOf(Color.WHITE)
                    mainConstraint.setBackgroundColor(Color.BLACK)
                }

                darkThemeData.loadState() == 0 -> {
                    bottomNav.setBackgroundColor(
                        ContextCompat.getColor(
                            this,
                            R.color.bottomNavBarBackground
                        )
                    )
                    bottomNav.itemIconTintList = ColorStateList.valueOf(Color.BLACK)
                    mainConstraint.setBackgroundColor(Color.WHITE)
                }

                darkThemeData.loadState() == 2 -> {
                    when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                        Configuration.UI_MODE_NIGHT_NO -> {
                            bottomNav.setBackgroundColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.bottomNavBarBackground
                                )
                            )
                            bottomNav.itemIconTintList = ColorStateList.valueOf(Color.BLACK)
                            mainConstraint.setBackgroundColor(Color.WHITE)
                        }

                        Configuration.UI_MODE_NIGHT_YES -> {
                            bottomNav.setBackgroundColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.bottomNavBarBackgroundDark
                                )
                            )
                            bottomNav.itemIconTintList = ColorStateList.valueOf(Color.WHITE)
                            mainConstraint.setBackgroundColor(Color.BLACK)
                        }

                        Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                            bottomNav.setBackgroundColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.bottomNavBarBackgroundDark
                                )
                            )
                            bottomNav.itemIconTintList = ColorStateList.valueOf(Color.WHITE)
                            mainConstraint.setBackgroundColor(Color.BLACK)
                        }
                    }
                }
            }
        } else {
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
            when {
            darkThemeData.loadState() == 1 -> {
                bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.bottomNavBarBackgroundDark))
                bottomNav.itemIconTintList = ColorStateList.valueOf(Color.WHITE)
                mainConstraint.setBackgroundColor(Color.BLACK)
            }
            darkThemeData.loadState() == 0 -> {
                bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.bottomNavBarBackground))
                bottomNav.itemIconTintList = ColorStateList.valueOf(Color.BLACK)
                mainConstraint.setBackgroundColor(Color.WHITE)
            }
            darkThemeData.loadState() == 2 -> {
                when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_NO -> {
                        bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.bottomNavBarBackground))
                        bottomNav.itemIconTintList = ColorStateList.valueOf(Color.BLACK)
                        mainConstraint.setBackgroundColor(Color.WHITE)
                    }
                    Configuration.UI_MODE_NIGHT_YES -> {
                        bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.bottomNavBarBackgroundDark))
                        bottomNav.itemIconTintList = ColorStateList.valueOf(Color.WHITE)
                        mainConstraint.setBackgroundColor(Color.BLACK)
                    }
                    Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                        bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.bottomNavBarBackgroundDark))
                        bottomNav.itemIconTintList = ColorStateList.valueOf(Color.WHITE)
                        mainConstraint.setBackgroundColor(Color.BLACK)
                    }
                }
            }
        }
        }
    }

    fun setCommunityBoardMenuText() {
        communityBoardFragment.setMenuText()
    }

    fun requestPermissions() {
        var list = listOf<String>()
        list = if (Build.VERSION.SDK_INT >= 33) {
            listOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            listOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        val managePermissions = ManagePermissions(this, list, 123)

        if (!managePermissions.checkPermissions(this)) {
            managePermissions.showAlert(this)
        }
    }

    fun checkPermissions(): Boolean {

        var list = listOf<String>()
        list = if (Build.VERSION.SDK_INT >= 33) {
            listOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            listOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        val managePermissions = ManagePermissions(this, list, 111)
        return managePermissions.checkPermissions(this)
    }
}