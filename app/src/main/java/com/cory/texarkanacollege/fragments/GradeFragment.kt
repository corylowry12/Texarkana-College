package com.cory.texarkanacollege.fragments

import android.annotation.SuppressLint
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cory.texarkanacollege.MainActivity
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.adapters.GradesAdapter
import com.cory.texarkanacollege.classes.DarkThemeData
import com.cory.texarkanacollege.classes.ItemID
import com.cory.texarkanacollege.classes.ManagePermissions
import com.cory.texarkanacollege.database.GradesDBHelper
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.io.*
import java.security.Permissions
import java.text.SimpleDateFormat
import java.util.*


class GradeFragment : Fragment() {

    private lateinit var gradesAdapter: GradesAdapter
    private val dataList = ArrayList<HashMap<String, String>>()

    private lateinit var gridLayoutManager: GridLayoutManager

    private lateinit var managePermissions: ManagePermissions

    private val permissionRequestCode = 1

    private lateinit var addImage : Button

    lateinit var image: String

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
        return inflater.inflate(R.layout.fragment_grade, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.image = ""

        if (resources.getBoolean(R.bool.isTablet)) {
            gridLayoutManager = GridLayoutManager(requireContext(), 2)
        }
        else {
            gridLayoutManager = GridLayoutManager(requireContext(), 1)
        }

        gradesAdapter = GradesAdapter(requireContext(), dataList)
        (context as MainActivity).gradesAdapter = gradesAdapter

        loadIntoList()

        val topAppBar = view.findViewById<MaterialToolbar>(R.id.materialToolBarGrades)

        val args = arguments
        val className = args?.getString("className", "")

        topAppBar.title = "Grades/$className"

        topAppBar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        topAppBar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.addClass -> {

                   val dialog = BottomSheetDialog(requireContext())
                    val addGradeView = layoutInflater.inflate(R.layout.add_grade_bottom_sheet, null)
                    dialog.setCancelable(false)
                    dialog.setContentView(addGradeView)
                    if (resources.getBoolean(R.bool.isTablet)) {
                        val bottomSheet =
                            dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        bottomSheetBehavior.skipCollapsed = true
                        bottomSheetBehavior.isHideable = false
                        bottomSheetBehavior.isDraggable = false
                    }
                    val addGradeButton = dialog.findViewById<Button>(R.id.addGradeButton)
                    val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)
                    val nameEditText = dialog.findViewById<TextInputEditText>(R.id.name)
                    val gradeEditText = dialog.findViewById<TextInputEditText>(R.id.grade)
                    val weightEditText = dialog.findViewById<TextInputEditText>(R.id.weight)
                    addImage = dialog.findViewById<Button>(R.id.addImage)!!

                    addGradeButton!!.setOnClickListener {
                        if (nameEditText!!.text.toString() == "") {
                            Toast.makeText(context, "Name is required", Toast.LENGTH_SHORT).show()
                        }
                        else if (gradeEditText!!.text.toString() == "") {
                            Toast.makeText(context, "Grade is required", Toast.LENGTH_SHORT).show()
                        }
                        else if (gradeEditText.text.toString().toDouble() > 100.0) {
                            Toast.makeText(context, "Grade can not be greater than 100", Toast.LENGTH_SHORT).show()
                        }
                        else if (weightEditText!!.text.toString() == "") {
                            Toast.makeText(context, "Weight is required", Toast.LENGTH_SHORT).show()
                        }
                        else if (weightEditText.text.toString().toDouble() > 100) {
                            Toast.makeText(context, "Weight can not be greater than 100", Toast.LENGTH_SHORT).show()
                        }
                        else {

                            val name = nameEditText.text.toString().trim()
                            var grade = gradeEditText.text.toString().trim()
                            var weight = weightEditText.text.toString().trim()

                            if (grade.contains(".")) {
                                if (grade.indexOf(".") == (grade.length - 1)) {
                                    grade = grade.replace(".", "")
                                }
                            }

                            if (weight.contains(".")) {
                                if (weight.indexOf(".") == (weight.length - 1)) {
                                    weight = weight.replace(".", "")
                                }
                            }

                            addGrade(name, grade, weight, image)

                            dialog.dismiss()
                        }
                    }

                    addImage.setOnClickListener {
                        if (addImage.text == "View Image") {
                            addImage.setOnLongClickListener {
                                addImage.text = "Add Image"
                                Toast.makeText(requireContext(), "Image Removed", Toast.LENGTH_SHORT).show()
                                image = ""
                                return@setOnLongClickListener true
                            }
                            val viewImageDialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogStyle).create()
                            val layout = layoutInflater.inflate(R.layout.view_image_layout, null)
                            val imageView = layout.findViewById<ImageView>(R.id.viewImageImageView)

                            val okButton = layout.findViewById<Button>(R.id.okButton)
                            Glide.with(requireContext())
                                .load(image)
                                .centerCrop()
                                .into(imageView)

                            okButton.setOnClickListener {
                                viewImageDialog.dismiss()
                            }

                            viewImageDialog.setView(layout)
                            viewImageDialog.show()
                        }
                        else {
                            if (addImage.text == "View Image") {
                                addImage.setOnLongClickListener {
                                    addImage.text = "Add Image"
                                    Toast.makeText(
                                        requireContext(),
                                        "Image Removed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    image = ""
                                    return@setOnLongClickListener true
                                }
                            } else {
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

                                managePermissions =
                                    ManagePermissions(
                                        requireActivity(),
                                        list,
                                        permissionRequestCode
                                    )

                                if (managePermissions.checkPermissions(requireContext())) {

                                    val chooseImageDialog =
                                        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogStyle).create()
                                    val layout =
                                        layoutInflater.inflate(R.layout.choose_image_dialog, null)
                                    chooseImageDialog.setView(layout)

                                    val chooseImageButton =
                                        layout.findViewById<Button>(R.id.chooseImage)
                                    val takePhotoButton =
                                        layout.findViewById<Button>(R.id.takePhoto)
                                    val cancelButtonImageDialog =
                                        layout.findViewById<Button>(R.id.cancelImageDialog)

                                    chooseImageButton.setOnClickListener {

                                        val pickerIntent = Intent(Intent.ACTION_PICK)
                                        pickerIntent.type = "image/*"

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            showImagePickerAndroid13.launch(
                                                PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }
                                        else {
                                            showImagePicker.launch(pickerIntent)
                                        }

                                        chooseImageDialog.dismiss()
                                    }
                                    takePhotoButton.setOnClickListener {
                                        if (managePermissions.checkPermissions(requireContext())) {

                                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                                                var photFile: File? = null

                                                try {
                                                    photFile = createImageFile()
                                                } catch (e: IOException) {
                                                    e.printStackTrace()
                                                }

                                                if (photFile != null) {
                                                    val photoUri = FileProvider.getUriForFile(
                                                        requireContext(),
                                                        "com.cory.texarkanacollege.FileProvider",
                                                        photFile
                                                    )
                                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                                                    showCamera.launch(intent)
                                                }
                                            }
                                            chooseImageDialog.dismiss()
                                        }
                                        else {
                                            managePermissions.showAlert(requireContext())
                                        }
                                    }
                                    cancelButtonImageDialog.setOnClickListener {
                                        chooseImageDialog.dismiss()
                                    }
                                    chooseImageDialog.show()
                                } else {
                                    managePermissions.showAlert(requireContext())

                                }
                            }
                        }
                    }

                    cancelButton!!.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()

                    true
                }
                else -> false
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

    val showImagePicker = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            // doSomeOperations();
            val data = result.data
            val selectedImage =
                Objects.requireNonNull(data)!!.data
            var imageStream: InputStream? = null
            try {
                imageStream =
                    activity?.contentResolver?.openInputStream(
                        selectedImage!!
                    )
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            val imageBitmap = BitmapFactory.decodeStream(imageStream)
            val stream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
           // val byteArray = stream.toByteArray()
            val selectedFile = File(getRealPathFromURI(selectedImage!!))
            this.image = selectedFile.toString()// To display selected image in image view
            addImage.text = "View Image"
        }
    }

    val showImagePickerAndroid13 = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

            var imageStream: InputStream? = null
            try {
                imageStream =
                    activity?.contentResolver?.openInputStream(
                        uri!!
                    )
                val imageBitmap = BitmapFactory.decodeStream(imageStream)
                val stream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                // val byteArray = stream.toByteArray()
                val selectedFile = File(getRealPathFromURI(uri!!))
                this.image = selectedFile.toString()
                addImage.text = "View Image"
            }
            catch (e: Exception) {
                e.printStackTrace()
                addImage.text = "Add Image"
            }
        }

    fun getRealPathFromURI(contentURI: Uri) : String {
        var result = ""
        val cursor = requireActivity().contentResolver?.query(contentURI, null, null, null, null)
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

    val showCamera = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {

            val ei = ExifInterface(currentPhotoPath)
            val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

            val m = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> {
                    m.postRotate(90f)
                }
                ExifInterface.ORIENTATION_ROTATE_180 -> {
                    m.postRotate(180f)
                }
                ExifInterface.ORIENTATION_ROTATE_270 -> {
                    m.postRotate(270f)
                }
            }

            val originalBitmap = BitmapFactory.decodeFile(currentPhotoPath)

           val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
               .format(System.currentTimeMillis())
            val storageDir = File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/TexarkanaCollege/")

            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
            val image = File.createTempFile(timeStamp, ".jpeg", storageDir)

            val f = File(image.toString())
            val fileOutputStream = FileOutputStream(f)
            val rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, m, true)
            val bitmap = rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            MediaScannerConnection.scanFile(requireContext(), arrayOf(image.toString()), null, null)

            this.image = image.toString()
            addImage.text = "View Image"
        }
    }

    var currentPhotoPath = ""

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val storageDir: File = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".png", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    @SuppressLint("Range")
    fun addGrade(
        name: String,
        grade: String,
        weight: String,
        image: String
    ) {
        val dbHandler = GradesDBHelper(requireContext(), null)

        val sdf = SimpleDateFormat("MMM/dd/yyyy hh:mm:ss", Locale.ENGLISH)
        val currentDate = sdf.format(Date())

        dbHandler.insertRow(ItemID(requireContext()).loadPosition().toString(), name, grade, weight, currentDate.toString(), image)
        loadIntoList()
        this.image = ""
    }

    @SuppressLint("Range")
    fun loadIntoList() {

        val dbHandler = GradesDBHelper(requireContext(), null)

        textViewVisibility()

        dataList.clear()

        val cursor = dbHandler.getGrades(ItemID(requireContext()).loadPosition().toString())
        cursor.moveToFirst()

        while (!cursor.isAfterLast) {
            val map = HashMap<String, String>()
            map["primary"] = cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_ID))
            map["id"] = cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_CLASS_ID))
            map["name"] = cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_NAME))
            map["grade"] = cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_GRADE))
            map["weight"] = cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_WEIGHT))
            map["date"] = cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_DATE))
            if (cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_IMAGE)) != null) {
                map["image"] =
                    cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_IMAGE))

            }
            else {
                map["image"] = ""

            }
            dataList.add(map)

            cursor.moveToNext()
        }

        val recyclerView = activity?.findViewById<RecyclerView>(R.id.gradesRecyclerView)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.adapter = gradesAdapter

    }

    fun deleteAll() {
        val animation = AlphaAnimation(1f, 0f)
        animation.duration = 500
        val recyclerView = view?.findViewById<RecyclerView>(R.id.gradesRecyclerView)

        recyclerView?.startAnimation(animation)

        textViewVisibility()

        Handler(Looper.getMainLooper()).postDelayed({
            loadIntoList()
        }, 500)
    }

    fun textViewVisibility() {
        val dbHandler = GradesDBHelper(requireActivity().applicationContext, null)

        if (dbHandler.getCount(ItemID(requireContext()).loadPosition().toString()).toString().toInt() > 0) {
            val noGradesStoredTextView = activity?.findViewById<TextView>(R.id.noGradesStoredTextView)
            noGradesStoredTextView?.visibility = View.GONE
        } else {
            val noGradesStoredTextView = activity?.findViewById<TextView>(R.id.noGradesStoredTextView)
            noGradesStoredTextView?.visibility = View.VISIBLE
        }
    }
}