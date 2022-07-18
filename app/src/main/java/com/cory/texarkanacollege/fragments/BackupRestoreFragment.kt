package com.cory.texarkanacollege.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.classes.*
import com.cory.texarkanacollege.database.AssignmentsDBHelper
import com.cory.texarkanacollege.database.ClassesDBHelper
import com.cory.texarkanacollege.database.GradesDBHelper
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class BackupRestoreFragment : Fragment() {

    var filePath = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_backup_restore, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topAppBar = view.findViewById<MaterialToolbar>(R.id.topAppBarBackupRestore)
        topAppBar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        val chooseDirectoryConstraint =
            view.findViewById<ConstraintLayout>(R.id.constraintChooseDirectory)
        chooseDirectoryConstraint.setOnClickListener {
            getFilePath()
        }

        val backupNowConstraint = view.findViewById<ConstraintLayout>(R.id.constraintBackup)
        backupNowConstraint.setOnClickListener {

                val path =
                    File(
                        SavedBackupDirectory(requireContext()).loadBackupDirectory()
                    )

                val list = listOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )

                val managePermissions =
                    ManagePermissions(
                        requireActivity(),
                        list,
                        123
                    )


                if (managePermissions.checkPermissions(requireContext())) {
                        var outputStream : OutputStream
                        val backupObjects = JSONObject()
                        val classesJSONArray = JSONArray()
                        val gradesJSONArray = JSONArray()
                        val assignmentsJSONArray = JSONArray()

                        if (!path.exists()) {
                            path.mkdirs()
                        }

                        val current = LocalDateTime.now()

                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm")
                        val formatted = current.format(formatter)

                     Toast.makeText(requireContext(), path.toString(), Toast.LENGTH_SHORT).show()

                    val backupFile = Environment.DIRECTORY_DOWNLOADS+"/Texarkana College backups/tc_backup_$formatted.txt"

                    if (Build.VERSION.SDK_INT < 31) {
                        File(backupFile).parentFile!!.mkdirs()
                        File(backupFile).createNewFile()
                    }

                        val backup = JSONObject()

                        backup.put(
                            "CategoryTextViewVisible",
                            "${CategoryTextViewVisible(requireContext()).loadCategoryTextView()}"
                        )
                        backup.put("ClassIcons", "${ClassIcons(requireContext()).loadClassIcons()}")
                        backup.put(
                            "ColoredBackgroundsData",
                            "${ColoredBackgroundsData(requireContext()).loadColoredBackgrounds()}"
                        )
                        backup.put(
                            "ColoredClassGradeTextView",
                            "${ColoredClassGradeTextView(requireContext()).loadColoredClassTextView()}"
                        )
                        backup.put(
                            "DarkThemeData",
                            "${DarkThemeData(requireContext()).loadState()}"
                        )
                        backup.put(
                            "DarkWebViewData",
                            "${DarkWebViewData(requireContext()).loadDarkWebView()}"
                        )
                        backup.put(
                            "DefaultCategoryData",
                            "${DefaultCategoryData(requireContext()).loadDefaultCategory()}"
                        )
                        backup.put(
                            "DefaultOpeningTabData",
                            "${DefaultOpeningTabData(requireContext()).loadDefaultTab()}"
                        )
                        backup.put(
                            "GradesColoredTextView",
                            "${GradesColoredTextView(requireContext()).loadGradeColoredTextView()}"
                        )
                        backup.put(
                            "GradesIcons",
                            "${GradesIcons(requireContext()).loadGradeIcons()}"
                        )
                        backup.put(
                            "RememberRecyclerViewVisibilityForAssignments",
                            "${RememberRecyclerViewVisibilityForAssignments(requireContext()).loadState()}"
                        )

                        val classesDBHandler =
                            ClassesDBHelper(requireActivity().applicationContext, null)


                        val classesCursor = classesDBHandler.getAllRow()
                        classesCursor?.moveToFirst()

                        while (!classesCursor!!.isAfterLast) {
                            val classes = JSONObject()
                            classes.put(
                                "id",
                                classesCursor.getString(classesCursor.getColumnIndex(ClassesDBHelper.COLUMN_ID))
                            )
                            classes.put(
                                "className",
                                classesCursor.getString(classesCursor.getColumnIndex(ClassesDBHelper.COLUMN_CLASS_NAME))
                            )
                            classes.put(
                                "classTime",
                                classesCursor.getString(classesCursor.getColumnIndex(ClassesDBHelper.COLUMN_CLASS_TIME))
                            )
                            classesJSONArray.put(classesCursor.position, classes)

                            classesCursor.moveToNext()
                        }

                        val gradesDBHandler = GradesDBHelper(requireContext(), null)

                        val gradesCursor = gradesDBHandler.getAll()
                        gradesCursor.moveToFirst()

                        while (!gradesCursor.isAfterLast) {
                            val grades = JSONObject()
                            grades.put(
                                "id",
                                gradesCursor.getString(gradesCursor.getColumnIndex(GradesDBHelper.COLUMN_CLASS_ID))
                            )
                            grades.put(
                                "name",
                                gradesCursor.getString(gradesCursor.getColumnIndex(GradesDBHelper.COLUMN_NAME))
                            )
                            grades.put(
                                "grade",
                                gradesCursor.getString(gradesCursor.getColumnIndex(GradesDBHelper.COLUMN_GRADE))
                            )
                            grades.put(
                                "weight",
                                gradesCursor.getString(gradesCursor.getColumnIndex(GradesDBHelper.COLUMN_WEIGHT))
                            )
                            grades.put(
                                "date",
                                gradesCursor.getString(gradesCursor.getColumnIndex(GradesDBHelper.COLUMN_DATE))
                            )
                            if (gradesCursor.getString(gradesCursor.getColumnIndex(GradesDBHelper.COLUMN_IMAGE)) != null) {
                                grades.put(
                                    "image",
                                    gradesCursor.getString(
                                        gradesCursor.getColumnIndex(
                                            GradesDBHelper.COLUMN_IMAGE
                                        )
                                    )
                                )
                            } else {
                                grades.put("image", "")
                            }
                            gradesJSONArray.put(gradesCursor.position, grades)
                            gradesCursor.moveToNext()
                        }

                        val assignmentsDBHandler = AssignmentsDBHelper(requireContext(), null)

                        val assignmentCursor = assignmentsDBHandler.getAllRow()
                        assignmentCursor!!.moveToFirst()

                        while (!assignmentCursor.isAfterLast) {
                            val assignments = JSONObject()
                            assignments.put(
                                "assignmentName",
                                assignmentCursor.getString(
                                    assignmentCursor.getColumnIndex(
                                        AssignmentsDBHelper.COLUMN_ASSIGNMENT_NAME
                                    )
                                )
                            )
                            assignments.put(
                                "className",
                                assignmentCursor.getString(
                                    assignmentCursor.getColumnIndex(
                                        AssignmentsDBHelper.COLUMN_CLASS_NAME
                                    )
                                )
                            )
                            assignments.put(
                                "category",
                                assignmentCursor.getString(
                                    assignmentCursor.getColumnIndex(
                                        AssignmentsDBHelper.COLUMN_CATEGORY
                                    )
                                )
                            )
                            assignments.put(
                                "status",
                                assignmentCursor.getString(
                                    assignmentCursor.getColumnIndex(
                                        AssignmentsDBHelper.COLUMN_STATUS
                                    )
                                )
                            )
                            assignments.put(
                                "notes",
                                assignmentCursor.getString(
                                    assignmentCursor.getColumnIndex(
                                        AssignmentsDBHelper.COLUMN_NOTES
                                    )
                                )
                            )
                            assignments.put(
                                "dueDate",
                                assignmentCursor.getString(
                                    assignmentCursor.getColumnIndex(
                                        AssignmentsDBHelper.COLUMN_ASSIGNMENT_DUE_DATE
                                    )
                                )
                            )
                            assignmentsJSONArray.put(assignmentCursor.position, assignments)

                            assignmentCursor.moveToNext()
                        }

                        backupObjects.put("backup", backup)
                        backupObjects.put("classes", classesJSONArray)
                        backupObjects.put("grades", gradesJSONArray)
                        backupObjects.put("assignments", assignmentsJSONArray)

                    if (Build.VERSION.SDK_INT >= 31) {
                        val externalUri =
                            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)

                        val relativeLocation = Environment.DIRECTORY_DOWNLOADS + "/Texarkana College backups"

                        val contentValues = ContentValues()
                        contentValues.put(
                            MediaStore.Files.FileColumns.DISPLAY_NAME,
                            "tc_backup_$formatted.txt"
                        )
                        contentValues.put(
                            MediaStore.Files.FileColumns.MIME_TYPE,
                            "application/text"
                        )
                        contentValues.put(MediaStore.Files.FileColumns.TITLE, "tc_backup_$formatted")
                        contentValues.put(
                            MediaStore.Files.FileColumns.DATE_ADDED,
                            System.currentTimeMillis() / 1000
                        )
                        contentValues.put(
                            MediaStore.Files.FileColumns.RELATIVE_PATH,
                            relativeLocation
                        )
                        contentValues.put(
                            MediaStore.Files.FileColumns.DATE_TAKEN,
                            System.currentTimeMillis()
                        )
                        val fileUri: Uri =
                            requireActivity().contentResolver.insert(externalUri, contentValues)!!
                        outputStream = requireActivity().contentResolver.openOutputStream(fileUri)!!
                        outputStream.write(backupObjects.toString(2).toByteArray())
                        outputStream.close()
                    }
                    else {
                        PrintWriter(FileWriter(backupFile))
                            .use { it.write(backupObjects.toString(2)) }
                    }
                        Toast.makeText(
                            requireContext(),
                            "Backup created successfully in your Downloads folder",
                            Toast.LENGTH_SHORT
                        ).show()
                } else {
                    managePermissions.showAlert(requireContext())
                }

        }

        val restoreConstraint = view.findViewById<ConstraintLayout>(R.id.constraintRestore)
        restoreConstraint.setOnClickListener {
            getRestoreFilePath()
        }
    }

    private var launcherForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data.also { uri -> filePath = uri.toString() }
                val uri: Uri = result.data!!.data!!

                val (_, needed) = uri.path!!.split(":")
                val directoryPath = "/storage/emulated/0/$needed"
                SavedBackupDirectory(requireContext()).setBackupDirectory(directoryPath)
            }
        }

    private var getFilePathResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data.also { uri -> filePath = uri.toString() }
                val uri: Uri = result.data!!.data!!

                try {
                    // val (_, needed) = uri.path!!.split(":")
                    //val directoryPath = "/storage/emulated/0/$needed"
                    restoreBackup(uri)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        requireContext(),
                        "There was an error loading file",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    fun restoreBackup(restoreFilePath: Uri) {

        try {
            /*val path =
                File(
                    restoreFilePath
                ) as File*/

            //if (path.exists()) {
            // val input = FileInputStream(restoreFilePath)
            val input = requireContext().contentResolver.openInputStream(restoreFilePath)!!
            val size = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            input.close()
            val json = String(buffer)

            val jsonContact = JSONObject(json)
            val jsonArrayBackup: JSONObject = jsonContact.getJSONObject("backup")

            CategoryTextViewVisible(requireContext()).setCategoryTextView(
                jsonArrayBackup.get("CategoryTextViewVisible").toString().toBoolean()
            )
            ClassIcons(requireContext()).setClassIcons(
                jsonArrayBackup.get("ClassIcons").toString().toBoolean()
            )
            ColoredBackgroundsData(requireContext()).setColoredBackgrounds(
                jsonArrayBackup.get("ColoredBackgroundsData").toString().toBoolean()
            )
            ColoredClassGradeTextView(requireContext()).setColoredClassTextView(
                jsonArrayBackup.get(
                    "ColoredClassGradeTextView"
                ).toString().toInt()
            )
            DarkThemeData(requireContext()).setState(
                jsonArrayBackup.get("DarkThemeData").toString().toInt()
            )
            DarkWebViewData(requireContext()).setDarkWebView(
                jsonArrayBackup.get("DarkWebViewData").toString().toBoolean()
            )
            DefaultCategoryData(requireContext()).setDefaultCategory(
                jsonArrayBackup.get("DefaultCategoryData").toString().toInt()
            )
            DefaultOpeningTabData(requireContext()).setDefaultTab(
                jsonArrayBackup.get("DefaultOpeningTabData").toString().toInt()
            )
            GradesColoredTextView(requireContext()).setGradeColoredTextView(
                jsonArrayBackup.get(
                    "GradesColoredTextView"
                ).toString().toBoolean()
            )
            GradesIcons(requireContext()).setGradeIcons(
                jsonArrayBackup.get("GradesIcons").toString().toBoolean()
            )
            RememberRecyclerViewVisibilityForAssignments(requireContext()).setState(
                jsonArrayBackup.get("RememberRecyclerViewVisibilityForAssignments").toString()
                    .toBoolean()
            )

            val materialAlertDialogBuilder =
                MaterialAlertDialogBuilder(
                    requireContext(),
                    R.style.AlertDialogStyle
                ).create()
            materialAlertDialogBuilder.setCancelable(false)

            val layout =
                LayoutInflater.from(context)
                    .inflate(R.layout.restore_dialog_layout, null)
            materialAlertDialogBuilder.setView(layout)

            val overwriteButton = layout.findViewById<Button>(R.id.overwriteButton)
            val dontOverwriteButton = layout.findViewById<Button>(R.id.dontOverwriteButton)

            overwriteButton.setOnClickListener {

                val jsonArrayClasses: JSONArray = jsonContact.getJSONArray("classes")

                val jsonArrayClassesSize = jsonArrayClasses.length()
                ClassesDBHelper(
                    requireContext(),
                    null
                ).deleteAll()
                for (i in 0 until jsonArrayClassesSize) {
                    val jsonObjectDetail: JSONObject = jsonArrayClasses.getJSONObject(i)

                    ClassesDBHelper(
                        requireContext(),
                        null
                    ).insertRestoreRow(
                        jsonObjectDetail.get("id").toString(),
                        jsonObjectDetail.get("className").toString(),
                        jsonObjectDetail.get("classTime").toString()
                    )

                }

                val jsonArrayAssignments: JSONArray = jsonContact.getJSONArray("assignments")

                val jsonArrayAssignmentsSize = jsonArrayAssignments.length()
                AssignmentsDBHelper(requireContext(), null).deleteAll()
                for (i in 0 until jsonArrayAssignmentsSize) {
                    val jsonObjectDetail: JSONObject = jsonArrayAssignments.getJSONObject(i)

                    AssignmentsDBHelper(requireContext(), null).insertRow(
                        jsonObjectDetail.get("assignmentName").toString(),
                        jsonObjectDetail.get("dueDate").toString(),
                        jsonObjectDetail.get("notes").toString(),
                        jsonObjectDetail.get("status").toString(),
                        jsonObjectDetail.get("className").toString(),
                        jsonObjectDetail.get("category").toString()
                    )
                }

                val jsonArrayGrades: JSONArray = jsonContact.getJSONArray("grades")

                val jsonArrayGradesSize = jsonArrayGrades.length()
                GradesDBHelper(requireContext(), null).deleteAll()
                for (i in 0 until jsonArrayGradesSize) {
                    val jsonObjectDetail: JSONObject = jsonArrayGrades.getJSONObject(i)

                    GradesDBHelper(requireContext(), null).insertRow(
                        jsonObjectDetail.get("id").toString(),
                        jsonObjectDetail.get("name").toString(),
                        jsonObjectDetail.get("grade").toString(),
                        jsonObjectDetail.get("weight").toString(),
                        jsonObjectDetail.get("date").toString(),
                        jsonObjectDetail.get("image").toString()
                    )
                }
                Toast.makeText(requireContext(), getString(R.string.backup_restored_successfully), Toast.LENGTH_SHORT)
                    .show()
                materialAlertDialogBuilder.dismiss()
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent =
                        requireContext().packageManager.getLaunchIntentForPackage(requireContext().packageName)
                    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    activity?.finish()
                }, 500)
            }
            dontOverwriteButton.setOnClickListener {
                if (ClassesDBHelper(requireContext(), null).getCount() == 0) {
                    val jsonArrayClasses: JSONArray = jsonContact.getJSONArray("classes")

                    val jsonArrayClassesSize = jsonArrayClasses.length()
                    for (i in 0 until jsonArrayClassesSize) {
                        val jsonObjectDetail: JSONObject = jsonArrayClasses.getJSONObject(i)

                        ClassesDBHelper(
                            requireContext(),
                            null
                        ).insertRestoreRow(
                            jsonObjectDetail.get("id").toString(),
                            jsonObjectDetail.get("className").toString(),
                            jsonObjectDetail.get("classTime").toString()
                        )

                    }
                }
                if (AssignmentsDBHelper(requireContext(), null).getCount() == 0) {
                val jsonArrayAssignments: JSONArray = jsonContact.getJSONArray("assignments")

                val jsonArrayAssignmentsSize = jsonArrayAssignments.length()
                for (i in 0 until jsonArrayAssignmentsSize) {
                    val jsonObjectDetail: JSONObject = jsonArrayAssignments.getJSONObject(i)

                    AssignmentsDBHelper(requireContext(), null).insertRow(
                        jsonObjectDetail.get("assignmentName").toString(),
                        jsonObjectDetail.get("dueDate").toString(),
                        jsonObjectDetail.get("notes").toString(),
                        jsonObjectDetail.get("status").toString(),
                        jsonObjectDetail.get("className").toString(),
                        jsonObjectDetail.get("category").toString()
                    )
                }
                }

                if (GradesDBHelper(requireContext(), null).getAll().count == 0) {
                    val jsonArrayGrades: JSONArray = jsonContact.getJSONArray("grades")

                    val jsonArrayGradesSize = jsonArrayGrades.length()
                    for (i in 0 until jsonArrayGradesSize) {
                        val jsonObjectDetail: JSONObject = jsonArrayGrades.getJSONObject(i)

                        GradesDBHelper(requireContext(), null).insertRow(
                            jsonObjectDetail.get("id").toString(),
                            jsonObjectDetail.get("name").toString(),
                            jsonObjectDetail.get("grade").toString(),
                            jsonObjectDetail.get("weight").toString(),
                            jsonObjectDetail.get("date").toString(),
                            jsonObjectDetail.get("image").toString()
                        )
                    }
                }
                materialAlertDialogBuilder.dismiss()
                Toast.makeText(requireContext(), getString(R.string.backup_restored_successfully), Toast.LENGTH_SHORT)
                    .show()
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent =
                        requireContext().packageManager.getLaunchIntentForPackage(requireContext().packageName)
                    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    activity?.finish()
                }, 500)
            }
            materialAlertDialogBuilder.show()


            /* } else {
                 Toast.makeText(requireContext(), "Error: File not Found", Toast.LENGTH_SHORT).show()
             }*/
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "There was some error while restoring backup",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getRestoreFilePath() {
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        getFilePathResult.launch(intent)
    }

    private fun getFilePath() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {

            addCategory(Intent.CATEGORY_DEFAULT)
        }
        launcherForResult.launch(intent)
    }
}