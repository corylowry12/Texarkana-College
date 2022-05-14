package com.cory.texarkanacollege

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class GradeFragment : Fragment() {

    private lateinit var gradesAdapter: GradesAdapter
    private val dataList = ArrayList<HashMap<String, String>>()
    private val imageDataList = ArrayList<HashMap<String, ByteArray>>()

    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var managePermissions: ManagePermissions

    private val permissionRequestCode = 1

    private val chooseImageRequestCode = 99

    private lateinit var image: ByteArray

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grade, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.image = ByteArray(0)

        linearLayoutManager = LinearLayoutManager(requireContext())

        gradesAdapter = GradesAdapter(requireContext(), dataList, imageDataList)

        loadIntoList()

        val topAppBar = activity?.findViewById<MaterialToolbar>(R.id.materialToolBarGrades)

        topAppBar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.addClass -> {
                    /*val grade = TextInputEditText(requireContext())
                    grade.setHint("Grade")
                    grade.setRawInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                    grade.keyListener = DigitsKeyListener.getInstance(false, true)

                    val frameLayout = LinearLayout(requireContext())
                    frameLayout.orientation = LinearLayout.VERTICAL
                    val params = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    params.topMargin = 30
                    params.bottomMargin = 30
                    params.leftMargin = 10
                    params.rightMargin = 10
                    grade.layoutParams = params
                    frameLayout.addView(grade)

                    val weight = TextInputEditText(requireContext())
                    weight.setHint("Weight")
                    weight.setRawInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                    weight.keyListener = DigitsKeyListener.getInstance(false, true)
                    weight.layoutParams = params
                    frameLayout.addView(weight)

                    val dialog = MaterialAlertDialogBuilder(requireContext())
                    dialog.setCancelable(false)
                    dialog.setView(frameLayout)
                    dialog.setPositiveButton("Add") { _, _ ->
                       val text = grade.text
                        val textString = text.toString()
                        if (grade.text != null && textString != "") {
                            if (text.toString().toDouble() > 100.0) {
                                Toast.makeText(
                                    requireContext(),
                                    "Grade must not be greater than 100",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                addGrade(requireContext(), "", grade.text.toString(), weight.text.toString())

                                loadIntoList()
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Grade is required",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    dialog.setNegativeButton("Cancel", null)
                    dialog.show()*/

                   val dialog = BottomSheetDialog(requireContext())
                    val addGradeView = layoutInflater.inflate(R.layout.add_grade_bottom_sheet, null)
                    dialog.setCancelable(false)
                    dialog.setContentView(addGradeView)
                    val addGradeButton = dialog.findViewById<Button>(R.id.addGradeButton)
                    val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)
                    val nameEditText = dialog.findViewById<TextInputEditText>(R.id.name)
                    val gradeEditText = dialog.findViewById<TextInputEditText>(R.id.grade)
                    val weightEditText = dialog.findViewById<TextInputEditText>(R.id.weight)
                    val addImage = dialog.findViewById<Button>(R.id.addImage)

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

                            val name = nameEditText.text.toString()
                            val grade = gradeEditText.text.toString()
                            val weight = weightEditText.text.toString()

                            addGrade(name, grade, weight, image)

                            dialog.dismiss()
                        }
                    }

                    addImage?.setOnClickListener {
                        if (addImage.text == "View Image") {
                            addImage.setOnLongClickListener {
                                addImage.text = "Add Image"
                                Toast.makeText(requireContext(), "Image Removed", Toast.LENGTH_SHORT).show()
                                return@setOnLongClickListener true
                            }
                            val viewImageDialog = MaterialAlertDialogBuilder(requireContext())
                            val layout = layoutInflater.inflate(R.layout.view_image_layout, null)
                            viewImageDialog.setPositiveButton("OK", null)
                            //layout.findViewById<ImageView>(R.id.viewImageImageView).setImageBitmap(image)

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
                                    return@setOnLongClickListener true
                                }
                            } else {
                                val list = listOf(android.Manifest.permission.CAMERA)

                                managePermissions =
                                    ManagePermissions(
                                        requireActivity(),
                                        list,
                                        permissionRequestCode
                                    )

                                if (managePermissions.checkPermissions(requireContext())) {

                                    val chooseImageDialog =
                                        MaterialAlertDialogBuilder(requireContext()).create()
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

                                        showImagePicker.launch(pickerIntent)

                                        addImage.text = "View Image"
                                        chooseImageDialog.dismiss()
                                    }
                                    takePhotoButton.setOnClickListener {
                                        if (managePermissions.checkPermissions(requireContext())) {
                                            val cameraIntent =
                                                Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                            showCamera.launch(cameraIntent)

                                            addImage.text = "View Image"
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
                                    Toast.makeText(
                                        requireContext(),
                                        "Permission Denied",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    managePermissions.showAlert(requireContext())

                                }
                            }
                        }
                    }

                    cancelButton!!.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()

                   /* val runnable = Runnable {
                        (context as MainActivity).showBottomSheet()
                    }
                    MainActivity().runOnUiThread(runnable)*/

                    true
                }
                else -> false
            }
        }
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
                    activity?.getContentResolver()?.openInputStream(
                        selectedImage!!
                    )
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            val imageBitmap = BitmapFactory.decodeStream(imageStream)
            val stream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 80, stream)
            val byteArray = stream.toByteArray()
            image = byteArray // To display selected image in image view
        }
    }

    val showCamera = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {

            val imageBitmap = result.data!!.extras!!.get("data") as Bitmap
            val stream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 80, stream)
            val byteArray = stream.toByteArray()
            image = byteArray
        }
    }

    @SuppressLint("Range")
    fun addGrade(
        name: String,
        grade: String,
        weight: String,
        image: ByteArray
    ) {
        val dbHandler = GradesDBHelper(requireContext(), null)

        val sdf = SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
        val currentDate = sdf.format(Date())
        dbHandler.insertRow(ItemID(requireContext()).loadPosition().toString(), name, grade, weight, currentDate.toString(), image)
        loadIntoList()
        this.image = ByteArray(0)
    }

    @SuppressLint("Range")
    fun loadIntoList() {
        val context = requireContext()
        val dbHandler = GradesDBHelper(context, null)

        if (dbHandler.getCount() > 0) {
            val noGradesStoredTextView = activity?.findViewById<TextView>(R.id.noGradesStoredTextView)
            noGradesStoredTextView?.visibility = View.GONE
        } else {
            val noGradesStoredTextView = activity?.findViewById<TextView>(R.id.noGradesStoredTextView)
            noGradesStoredTextView?.visibility = View.VISIBLE

        }

        dataList.clear()
        imageDataList.clear()
        val cursor = dbHandler.getGrades(ItemID(context).loadPosition().toString())
        cursor.moveToFirst()

        while (!cursor.isAfterLast) {
            val map = HashMap<String, String>()
            map["primary"] = cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_ID))
            map["id"] = cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_CLASS_ID))
            map["name"] = cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_NAME))
            map["grade"] = cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_GRADE))
            map["weight"] = cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_WEIGHT))
            map["date"] = cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_DATE))
            dataList.add(map)

            val imageMap = HashMap<String, ByteArray>()

            if (cursor.getBlob(cursor.getColumnIndex(GradesDBHelper.COLUMN_IMAGE)) != null) {
                imageMap["image"] =
                    cursor.getBlob(cursor.getColumnIndex(GradesDBHelper.COLUMN_IMAGE))

                imageDataList.add(imageMap)
            }
            else {
                imageMap["image"] = byteArrayOf()

                imageDataList.add(imageMap)
            }

            cursor.moveToNext()
        }

        val recyclerView = activity?.findViewById<RecyclerView>(R.id.gradesRecyclerView)
        recyclerView?.layoutManager = linearLayoutManager
        recyclerView?.adapter = gradesAdapter

    }

    /*override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            permissionRequestCode -> {
                val isPermissionGranted = managePermissions.processPermissionsResult(requestCode, permissions, grantResults)
                if(isPermissionGranted) {
                    Toast.makeText(requireContext(), "Permisison Granted", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }*/
}