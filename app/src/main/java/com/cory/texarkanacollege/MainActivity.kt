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
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cory.texarkanacollege.classes.*
import com.cory.texarkanacollege.database.AssignmentsDBHelper
import com.cory.texarkanacollege.fragments.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigationrail.NavigationRailView
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.inappmessaging.FirebaseInAppMessaging
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

    private lateinit var appUpdateManager : AppUpdateManager

    var path = ""

    val homeFragment = HomeFragment()
    val classesFragment = ClassesFragment()
    val settingsFragment = SettingsFragment()
    var gradeFragment = GradeFragment()
    val assignmentFragment = AssignmentFragment()
    var campusNewsFragment = CampusNewsFragment()
    val communityBoardFragment = CommunityBoardFragment()
    var viewPostCommunityBoardFragment = ViewCommunityBoardPostFragment()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val extras = intent.extras

        if (extras != null) {
            if (resources.getBoolean(R.bool.isTablet)) {
                val bottomNav = findViewById<NavigationRailView>(R.id.bottomNav)
                if (extras.getString("widget") == "Widget") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                } else if (extras.getString("view_classes") == "view_classes") {
                    bottomNav.menu.findItem(R.id.classes).isChecked = true
                } else if (extras.getString("view_assignments") == "view_assignments") {
                    bottomNav.menu.findItem(R.id.assignments).isChecked = true
                } else if (extras.getString("view_map") == "view_map") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                } else {
                    bottomNav.menu.findItem(R.id.home).isChecked = true
                }
            } else {
                val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
                if (extras.getString("widget") == "Widget") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                } else if (extras.getString("view_classes") == "view_classes") {
                    bottomNav.menu.findItem(R.id.classes).isChecked = true
                } else if (extras.getString("view_assignments") == "view_assignments") {
                    bottomNav.menu.findItem(R.id.assignments).isChecked = true
                } else if (extras.getString("view_map") == "view_map") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                } else {
                    bottomNav.menu.findItem(R.id.home).isChecked = true
                }
            }

            if (extras.getString("widget") == "Widget") {
                replaceFragment(CampusMapFragment())
            } else if (extras.getString("view_classes") == "view_classes") {
                replaceFragment(classesFragment)
            } else if (extras.getString("view_assignments") == "view_assignments") {
                replaceFragment(assignmentFragment)
            } else if (extras.getString("view_map") == "view_map") {
                replaceFragment(CampusMapFragment())
            } else if (intent.action == Intent.ACTION_VIEW) {
                val args = Bundle()
                args.putString("deepLink", intent.dataString)
                homeFragment.arguments = args
                replaceFragment(homeFragment)
            }
        }
    }

    private val listener = InstallStateUpdatedListener { installState ->
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            val materialAlertDialogBuilder =
                MaterialAlertDialogBuilder(this, R.style.AlertDialogStyle)
            materialAlertDialogBuilder.setCancelable(false)
            materialAlertDialogBuilder.setTitle("Update Downloaded")
            materialAlertDialogBuilder.setMessage("App Update Downloaded, click restart to install")
            materialAlertDialogBuilder.setPositiveButton("Restart") { _, _ ->
                appUpdateManager.completeUpdate()
                val intent =
                    packageManager.getLaunchIntentForPackage(packageName)
                intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            materialAlertDialogBuilder.show()
        }
        else if (installState.installStatus() == InstallStatus.INSTALLED) {
            unregister()
        }
    }

    private fun unregister() {
        appUpdateManager.unregisterListener(listener)
    }

    private fun checkUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateManager.registerListener(listener)
        appUpdateInfoTask.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && it.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE
                )
            ) {
                appUpdateManager.startUpdateFlowForResult(it, AppUpdateType.IMMEDIATE, this, 123)
                appUpdateManager.completeUpdate()
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

        val dbHandler = AssignmentsDBHelper(this, null)
        val cursor = dbHandler.getAllRow(this)
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

        checkUpdate()

        val classesIntent = Intent(this, MainActivity::class.java)
        classesIntent.action = Intent.ACTION_VIEW
        classesIntent.putExtra("view_classes", "view_classes")

        val assignmentIntent = Intent(this, MainActivity::class.java)
        assignmentIntent.action = Intent.ACTION_VIEW
        assignmentIntent.putExtra("view_assignments", "view_assignments")

        val mapIntent = Intent(this, MainActivity::class.java)
        mapIntent.action = Intent.ACTION_VIEW
        mapIntent.putExtra("view_map", "view_map")

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
            .setIcon(Icon.createWithResource(this, R.drawable.ic_baseline_assignment_24_shortcut))
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


        setSmallSettingsBadge()
        fetchTOSJsonBadge()

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
            val communityBoardInBottomNav = remoteConfig.getBoolean("show_community_board_in_bottom_nav")
            BottomNavWithCommunityBoard(this).setState(communityBoardInBottomNav)
        }

        this.cacheDir.deleteRecursively()

        val extras = intent.extras

        if (extras != null) {
            if (resources.getBoolean(R.bool.isTablet)) {
                val bottomNav = findViewById<NavigationRailView>(R.id.bottomNav)
                if (extras.getString("widget") == "Widget") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                } else if (extras.getString("view_classes") == "view_classes") {
                    bottomNav.menu.findItem(R.id.classes).isChecked = true
                } else if (extras.getString("view_assignments") == "view_assignments") {
                    bottomNav.menu.findItem(R.id.assignments).isChecked = true
                } else if (extras.getString("view_map") == "view_map") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                } else {
                    bottomNav.menu.findItem(R.id.home).isChecked = true
                }
            } else {
                val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
                if (extras.getString("widget") == "Widget") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                } else if (extras.getString("view_classes") == "view_classes") {
                    bottomNav.menu.findItem(R.id.classes).isChecked = true
                } else if (extras.getString("view_assignments") == "view_assignments") {
                    bottomNav.menu.findItem(R.id.assignments).isChecked = true
                } else if (extras.getString("view_map") == "view_map") {
                    bottomNav.menu.findItem(R.id.settings).isChecked = true
                } else {
                    bottomNav.menu.findItem(R.id.home).isChecked = true
                }
            }

            if (extras.getString("widget") == "Widget") {
                replaceFragment(CampusMapFragment())
            } else if (extras.getString("view_classes") == "view_classes") {
                replaceFragment(classesFragment)
            } else if (extras.getString("view_assignments") == "view_assignments") {
                replaceFragment(assignmentFragment)
            } else if (extras.getString("view_map") == "view_map") {
                replaceFragment(CampusMapFragment())
            } else if (intent.action == Intent.ACTION_VIEW) {
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
            if (BottomNavWithCommunityBoard(this).loadState() && CommunityBoardVisibileData(this).loadCommunityBoardVisible()) {
                bottomNav.inflateMenu(R.menu.bottom_nav_menu_with_community_board)
                BottomNavContainsCommunityBoard(this).setState(true)
            }
            else {
                bottomNav.inflateMenu(R.menu.bottom_nav_menu)
                BottomNavContainsCommunityBoard(this).setState(false)
            }
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
            if (BottomNavWithCommunityBoard(this).loadState() && CommunityBoardVisibileData(this).loadCommunityBoardVisible()) {
                bottomNav.inflateMenu(R.menu.bottom_nav_menu_with_community_board)
                BottomNavContainsCommunityBoard(this).setState(true)
            }
            else {
                bottomNav.inflateMenu(R.menu.bottom_nav_menu)
                BottomNavContainsCommunityBoard(this).setState(false)
            }
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

    private fun getRealPathFromURI(contentURI: Uri): String {
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
        if (resources.getBoolean(R.bool.isTablet)) {
            val badge =
                findViewById<NavigationRailView>(R.id.bottomNav).getOrCreateBadge(R.id.settings)
            if (Version(this).loadVersion() != getString(R.string.versionNumber)) {
                badge.isVisible = true

                badge.backgroundColor = ContextCompat.getColor(this, R.color.redBadgeColor)
            } else {
                badge.isVisible = false
            }
        } else {
            val badge =
                findViewById<BottomNavigationView>(R.id.bottomNav).getOrCreateBadge(R.id.settings)
            if (Version(this).loadVersion() != getString(R.string.versionNumber)) {
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
        if (resources.getBoolean(R.bool.isTablet)) {
            val bottomNav = findViewById<NavigationRailView>(R.id.bottomNav)
            when {
                darkThemeData.loadState() == 1 -> {
                    bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.bottomNavBarBackgroundDark))
                    bottomNav.itemIconTintList = ColorStateList.valueOf(Color.WHITE)
                }
                darkThemeData.loadState() == 0 -> {
                    bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.bottomNavBarBackground))
                    bottomNav.itemIconTintList = ColorStateList.valueOf(Color.BLACK)
                }
                darkThemeData.loadState() == 2 -> {
                    when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                        Configuration.UI_MODE_NIGHT_NO -> {
                            bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.bottomNavBarBackground))
                            bottomNav.itemIconTintList = ColorStateList.valueOf(Color.BLACK)
                        }
                        Configuration.UI_MODE_NIGHT_YES -> {
                            bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.bottomNavBarBackgroundDark))
                            bottomNav.itemIconTintList = ColorStateList.valueOf(Color.WHITE)
                        }
                        Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                            bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.bottomNavBarBackgroundDark))
                            bottomNav.itemIconTintList = ColorStateList.valueOf(Color.WHITE)
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
            }
            darkThemeData.loadState() == 0 -> {
                bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.bottomNavBarBackground))
                bottomNav.itemIconTintList = ColorStateList.valueOf(Color.BLACK)
            }
            darkThemeData.loadState() == 2 -> {
                when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_NO -> {
                        bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.bottomNavBarBackground))
                        bottomNav.itemIconTintList = ColorStateList.valueOf(Color.BLACK)
                    }
                    Configuration.UI_MODE_NIGHT_YES -> {
                        bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.bottomNavBarBackgroundDark))
                        bottomNav.itemIconTintList = ColorStateList.valueOf(Color.WHITE)
                    }
                    Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                        bottomNav.setBackgroundColor(ContextCompat.getColor(this, R.color.bottomNavBarBackgroundDark))
                        bottomNav.itemIconTintList = ColorStateList.valueOf(Color.WHITE)
                    }
                }
            }
        }
        }
    }

    fun setCommunityBoardMenuText() {
        communityBoardFragment.setMenuText()
    }

    override fun onStop() {
        super.onStop()

        appUpdateManager.unregisterListener(listener)
    }
}