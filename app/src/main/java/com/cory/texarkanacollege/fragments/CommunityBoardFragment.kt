package com.cory.texarkanacollege.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toFile
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.adapters.CommunityBoardAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.suke.widget.SwitchButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class CommunityBoardFragment : Fragment() {

    private val client = OkHttpClient()

    private lateinit var communityBoardAdapter: CommunityBoardAdapter
    private val dataList = ArrayList<HashMap<String, String>>()
    private val sortedData = ArrayList<HashMap<String, String>>()

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var gridLayoutManager: GridLayoutManager

    private lateinit var database: DatabaseReference

    var childrenCount = 0L

    var imagePath = ""
    lateinit var image: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community_board, container, false)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {

                Toast.makeText(requireContext(), "You have successfully been logged in", Toast.LENGTH_SHORT).show()
                activity?.findViewById<MaterialToolbar>(R.id.materialToolBarCommunityBoard)?.menu?.findItem(R.id.signOut)?.title = "Sign Out"
            } else if (it.exception is FirebaseAuthInvalidUserException) {
                Toast.makeText(requireContext(), "Sorry, you have been banned", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val getSignInData = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        try {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(result.data)

            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error: " + e.statusCode.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    val showImagePicker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {

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
            image = selectedImage!!
            val imageBitmap = BitmapFactory.decodeStream(imageStream)
            val stream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()
            val selectedFile = File(getRealPathFromURI(selectedImage))
            this.imagePath = Date().toString()
            Toast.makeText(requireContext(), "Image Added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRealPathFromURI(contentURI: Uri): String {
        var result = ""
        val cursor = requireActivity().contentResolver?.query(contentURI, null, null, null, null)
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

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        database = Firebase.database.reference
        val storage = FirebaseStorage.getInstance()

        gridLayoutManager = GridLayoutManager(requireContext(), 1)
        communityBoardAdapter = CommunityBoardAdapter(requireContext(), dataList)

        loadIntoList()

        val swipeRefreshLayout =
            requireView().findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayoutCommunityBoard)
        swipeRefreshLayout.setOnRefreshListener {
            loadIntoList()
            swipeRefreshLayout.isRefreshing = false
        }

        val toolBar =
            requireView().findViewById<MaterialToolbar>(R.id.materialToolBarCommunityBoard)

        toolBar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        if (firebaseAuth.currentUser != null) {
            toolBar.menu.findItem(R.id.signOut).title = "Sign Out"
        }
        else {
            toolBar.menu.findItem(R.id.signOut).title = "Sign In"
        }

        toolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.termsOfService -> {
                    val dialog = BottomSheetDialog(requireContext())
                    val addGradeView =
                        layoutInflater.inflate(R.layout.about_community_board_bottom_sheet, null)
                    val closeImageButton =
                        addGradeView.findViewById<ImageButton>(R.id.closeImageButton)
                    dialog.setCancelable(true)
                    dialog.setContentView(addGradeView)
                    val textView = addGradeView.findViewById<TextView>(R.id.tos)
                    val progressBar = addGradeView.findViewById<ProgressBar>(R.id.tosProgressBar)

                    val request = Request.Builder()
                        .url("https://raw.githubusercontent.com/corylowry12/Texarkana-College/main/community_board_tos.json")
                        .build()

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            GlobalScope.launch(Dispatchers.Main) {
                                val alert = MaterialAlertDialogBuilder(
                                    requireContext()
                                )
                                alert.setTitle("Error")
                                alert.setMessage("There was an error. Check your data connection.")
                                alert.setPositiveButton("OK") { _, _ ->
                                    activity?.supportFragmentManager?.popBackStack()
                                }
                                alert.show()
                            }
                        }

                        override fun onResponse(call: Call, response: Response) {

                            val strResponse = response.body()!!.string()

                            val jsonContact = JSONObject(strResponse)

                            val jsonObjectDetail = jsonContact.getString("title")
                            val title = jsonObjectDetail.replace("\n", "\\\n")
                            val text = title.replace("\u2022", "\\\u2022")
                            val final = text.replace("\\", "")

                            GlobalScope.launch(Dispatchers.Main) {
                                progressBar.visibility = View.GONE
                                textView.text = final
                            }
                        }
                    })

                    closeImageButton.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                    return@setOnMenuItemClickListener true
                }
                R.id.addClass -> {
                    firebaseAuth.currentUser?.reload()
                    val user = firebaseAuth.currentUser
                    if (user == null) {
                        mGoogleSignInOptions =
                            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(getString(R.string.app_client_id))
                                .requestEmail()
                                .build()
                        mGoogleSignInClient =
                            GoogleSignIn.getClient(requireContext(), mGoogleSignInOptions)
                        val signInIntent: Intent = mGoogleSignInClient.signInIntent
                        getSignInData.launch(signInIntent)
                    } else {
                        val dialog = BottomSheetDialog(requireContext())

                        val addGradeView =
                            layoutInflater.inflate(R.layout.add_post_bottom_sheet, null)
                        dialog.setCancelable(false)
                        dialog.setContentView(addGradeView)
                        val addGradeButton = dialog.findViewById<Button>(R.id.addGradeButton)
                        val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)
                        val titleEditText = dialog.findViewById<TextInputEditText>(R.id.grade)
                        val postEditText = dialog.findViewById<TextInputEditText>(R.id.weight)
                        val pinnedSwitch = dialog.findViewById<SwitchButton>(R.id.pinnedSwitch)
                        val urgentSwitch = dialog.findViewById<SwitchButton>(R.id.urgenSwitch)
                        val addImageButton = dialog.findViewById<Button>(R.id.addImage)
                        val urgentSwitchConstraintLayout = dialog.findViewById<ConstraintLayout>(R.id.urgentSwitchConstraintLayout)

                        urgentSwitchConstraintLayout?.setOnClickListener {
                            urgentSwitch?.isChecked = !urgentSwitch!!.isChecked
                        }

                        addImageButton?.setOnLongClickListener {
                            if (this::image.isInitialized && image != Uri.EMPTY) {
                                image = Uri.EMPTY
                                Toast.makeText(requireContext(), "Image Removed", Toast.LENGTH_SHORT).show()
                                 return@setOnLongClickListener true
                            }
                            return@setOnLongClickListener false
                        }

                        addImageButton?.setOnClickListener {
                            val pickerIntent = Intent(Intent.ACTION_PICK)
                            pickerIntent.type = "image/*"

                            showImagePicker.launch(pickerIntent)
                        }

                        addGradeButton?.setOnClickListener {
                         if (titleEditText!!.text.toString() == "") {
                                Toast.makeText(
                                    requireContext(),
                                    "Please enter a title for your post",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (postEditText!!.text.toString() == "") {
                                Toast.makeText(
                                    requireContext(),
                                    "Please enter content for your post",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (titleEditText.text.toString() != "" && postEditText.text.toString() != "") {
                                if (this::image.isInitialized && image != Uri.EMPTY) {
                                    val ref = storage.reference.child("images/$imagePath")
                                    val uploadTask = ref.putFile(image)
                                    val layout = layoutInflater.inflate(
                                        R.layout.uploading_image_dialog,
                                        null
                                    )
                                    val uploadingImageDialog = MaterialAlertDialogBuilder(
                                        requireContext(), R.style.AlertDialogStyle
                                    )
                                    uploadingImageDialog.setCancelable(false)
                                    val progressBar =
                                        layout.findViewById<ProgressBar>(R.id.uploadingImageProgressBar)
                                    val uploadProgressTextView =
                                        layout.findViewById<TextView>(R.id.currentProgress)
                                    progressBar.max = 100
                                    uploadingImageDialog.setView(layout)
                                    uploadingImageDialog.setNegativeButton(getString(R.string.cancel)) { d, _ ->
                                        uploadTask.cancel()
                                        d.dismiss()
                                    }
                                    val uploadingD = uploadingImageDialog.create()
                                    uploadingD.show()

                                    uploadTask.addOnProgressListener { totalProgress ->
                                        val currentProgress =
                                            (100.0 * totalProgress.bytesTransferred) / totalProgress.totalByteCount
                                        progressBar.progress = currentProgress.toInt()
                                        uploadProgressTextView.text =
                                            "Upload Progress: ${currentProgress.toInt()}%"
                                        println("progress is: $currentProgress")
                                    }
                                    uploadTask.addOnSuccessListener {
                                        val result = it.metadata!!.reference!!.downloadUrl
                                        result.addOnSuccessListener { downloadLink ->
                                            database.child("posts")
                                                .child((childrenCount + 1).toString())
                                                .child("images").setValue(downloadLink.toString())
                                            database.child("posts")
                                                .child((childrenCount + 1).toString())
                                                .child("imagePath").setValue("images/$imagePath")

                                            database.child("posts")
                                                .child((childrenCount + 1).toString())
                                                .child("email")
                                                .setValue(firebaseAuth.currentUser!!.email)
                                            database.child("posts")
                                                .child((childrenCount + 1).toString())
                                                .child("profile_photo")
                                                .setValue(firebaseAuth.currentUser!!.photoUrl.toString())
                                            database.child("posts")
                                                .child((childrenCount + 1).toString())
                                                .child("title")
                                                .setValue(titleEditText.text.toString().trim())
                                            database.child("posts")
                                                .child((childrenCount + 1).toString())
                                                .child("name")
                                                .setValue(firebaseAuth.currentUser!!.displayName.toString())
                                            database.child("posts")
                                                .child((childrenCount + 1).toString())
                                                .child("content")
                                                .setValue(postEditText.text.toString().trim())

                                            val formatter =
                                                SimpleDateFormat(
                                                    "MMM/dd/yyyy hh:mm aa",
                                                    Locale.ENGLISH
                                                )
                                            val dateFormatted = formatter.format(Date())

                                            database.child("posts")
                                                .child((childrenCount + 1).toString())
                                                .child("date").setValue(dateFormatted)
                                            database.child("posts")
                                                .child((childrenCount + 1).toString())
                                                .child("pinned").setValue(pinnedSwitch!!.isChecked)
                                            database.child("posts")
                                                .child((childrenCount + 1).toString())
                                                .child("urgent").setValue(urgentSwitch!!.isChecked)
                                            database.child("posts")
                                                .child((childrenCount + 1).toString())
                                                .child("uid")
                                                .setValue(firebaseAuth.currentUser!!.uid)

                                            println("database changed")
                                            dialog.dismiss()
                                            loadIntoList()
                                            uploadingD.dismiss()

                                            image = Uri.EMPTY
                                        }
                                    }
                                } else {
                                    database.child("posts").child((childrenCount + 1).toString())
                                        .child("profile_photo")
                                        .setValue(firebaseAuth.currentUser!!.photoUrl.toString())
                                    database.child("posts").child((childrenCount + 1).toString())
                                        .child("title").setValue(titleEditText.text.toString())
                                    database.child("posts").child((childrenCount + 1).toString())
                                        .child("name")
                                        .setValue(firebaseAuth.currentUser!!.displayName.toString())
                                    database.child("posts").child((childrenCount + 1).toString())
                                        .child("content").setValue(postEditText.text.toString())

                                    database.child("posts")
                                        .child((childrenCount + 1).toString())
                                        .child("email")
                                        .setValue(firebaseAuth.currentUser!!.email)

                                    val formatter =
                                        SimpleDateFormat("MMM/dd/yyyy hh:mm aa", Locale.ENGLISH)
                                    val dateFormatted = formatter.format(Date())

                                    database.child("posts").child((childrenCount + 1).toString())
                                        .child("date").setValue(dateFormatted)
                                    database.child("posts").child((childrenCount + 1).toString())
                                        .child("pinned").setValue(pinnedSwitch!!.isChecked)
                                    database.child("posts").child((childrenCount + 1).toString())
                                        .child("urgent").setValue(urgentSwitch!!.isChecked)
                                    database.child("posts").child((childrenCount + 1).toString())
                                        .child("uid").setValue(firebaseAuth.currentUser!!.uid)
                                    database.child("posts")
                                        .child((childrenCount + 1).toString())
                                        .child("images").setValue("")

                                    image = Uri.EMPTY

                                    dialog.dismiss()
                                    loadIntoList()
                                }
                            }
                        }

                        cancelButton?.setOnClickListener {
                            dialog.dismiss()
                        }

                        dialog.show()
                    }
                    true
                }
                R.id.signOut -> {
                    if (firebaseAuth.currentUser != null) {
                        firebaseAuth.signOut()
                        Toast.makeText(requireContext(), "You are now signed out", Toast.LENGTH_SHORT).show()
                        toolBar.menu.findItem(R.id.signOut).title = "Sign In"
                    }
                    else {
                        mGoogleSignInOptions =
                            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(getString(R.string.app_client_id))
                                .requestEmail()
                                .build()
                        mGoogleSignInClient =
                            GoogleSignIn.getClient(requireContext(), mGoogleSignInOptions)
                        val signInIntent: Intent = mGoogleSignInClient.signInIntent
                        getSignInData.launch(signInIntent)
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun loadIntoList() {
        try {
            dataList.clear()
            sortedData.clear()
            val loadAllMaterialDialog = MaterialAlertDialogBuilder(
                requireContext(), R.style.AlertDialogStyle
            )
            loadAllMaterialDialog.setCancelable(false)
            val layout = layoutInflater.inflate(R.layout.fetching_community_board_dialog, null)
            loadAllMaterialDialog.setView(layout)
            loadAllMaterialDialog.setNegativeButton(getString(R.string.cancel)) { d, _ ->
                d.dismiss()
                activity?.supportFragmentManager?.popBackStack()
            }
            val loadAllD = loadAllMaterialDialog.create()
            loadAllD.show()

            val communityBoardRecyclerView =
                activity?.findViewById<RecyclerView>(R.id.communityBoardRecyclerView)

            database.child("posts").orderByKey().limitToLast(1)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        for (i in snapshot.children) {
                            childrenCount = i.key.toString().toLong()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            database.child("posts").orderByKey()
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        dataList.clear()
                        sortedData.clear()

                        var url = ""

                        for (i in snapshot.children) {
                            println("children " + i.toString())
                            /*try {

                                FirebaseStorage.getInstance().reference.child(
                                    snapshot.child(i.key.toString())
                                        .child("images").value.toString()
                                ).downloadUrl.addOnSuccessListener {
                                    println("url is " + it.toString())

                                    url = it.toString()

                                }.addOnFailureListener { exception ->
                                    println("url is " + exception.toString())

                                    url = ""
                                }.addOnCompleteListener {
                                    println("task completed")
                                    val map = java.util.HashMap<String, String>()
                                    map["images"] = snapshot.child(i.key.toString()).child("images").value.toString()
                                    map["profilePicURL"] = snapshot.child(i.key.toString())
                                        .child("profile_photo").value.toString()
                                    map["email"] =
                                        snapshot.child(i.key.toString())
                                            .child("email").value.toString()
                                    map["name"] =
                                        snapshot.child(i.key.toString())
                                            .child("name").value.toString()
                                    map["title"] =
                                        snapshot.child(i.key.toString())
                                            .child("title").value.toString()
                                    map["content"] =
                                        snapshot.child(i.key.toString())
                                            .child("content").value.toString()
                                    map["date"] =
                                        snapshot.child(i.key.toString())
                                            .child("date").value.toString()
                                    map["pinned"] =
                                        snapshot.child(i.key.toString())
                                            .child("pinned").value.toString()
                                    map["urgent"] =
                                        snapshot.child(i.key.toString())
                                            .child("urgent").value.toString()
                                    map["uid"] =
                                        snapshot.child(i.key.toString())
                                            .child("uid").value.toString()

                                    map["imageURL"] = url
                                    map["childPosition"] = i.key.toString()
                                    dataList.add(map)

                                    val sortedDataList =
                                        dataList.sortedWith(
                                            compareBy(
                                                { it["pinned"] },
                                                { it["urgent"] },
                                                { it["date"] })
                                        )
                                            .reversed()

                                    println("sorted data List " + sortedDataList)
                                    sortedData.clear()
                                    for (z in sortedDataList) {
                                        sortedData.add(z)
                                    }

                                    communityBoardAdapter =
                                        CommunityBoardAdapter(requireContext(), sortedData)

                                    communityBoardRecyclerView?.layoutManager = gridLayoutManager
                                    communityBoardRecyclerView?.adapter = communityBoardAdapter
                                }

                            } catch (e: IllegalArgumentException) {
                                val map = java.util.HashMap<String, String>()
                                map["images"] = snapshot.child(i.key.toString()).child("images").value.toString()
                                map["profilePicURL"] = snapshot.child(i.key.toString())
                                    .child("profile_photo").value.toString()
                                map["email"] =
                                    snapshot.child(i.key.toString()).child("email").value.toString()
                                map["name"] =
                                    snapshot.child(i.key.toString()).child("name").value.toString()
                                map["title"] =
                                    snapshot.child(i.key.toString()).child("title").value.toString()
                                map["content"] =
                                    snapshot.child(i.key.toString())
                                        .child("content").value.toString()
                                map["date"] =
                                    snapshot.child(i.key.toString()).child("date").value.toString()
                                map["pinned"] =
                                    snapshot.child(i.key.toString())
                                        .child("pinned").value.toString()
                                map["urgent"] =
                                    snapshot.child(i.key.toString())
                                        .child("urgent").value.toString()
                                map["uid"] =
                                    snapshot.child(i.key.toString()).child("uid").value.toString()

                                map["imageURL"] = ""
                                map["childPosition"] = i.key.toString()
                                dataList.add(map)

                                val sortedDataList =
                                    dataList.sortedWith(
                                        compareBy(
                                            { it["pinned"] },
                                            { it["urgent"] },
                                            { it["date"] })
                                    )
                                        .reversed()

                                println("sorted data List " + sortedDataList)
                                sortedData.clear()
                                for (z in sortedDataList) {
                                    sortedData.add(z)
                                }

                                communityBoardAdapter =
                                    CommunityBoardAdapter(requireContext(), sortedData)

                                communityBoardRecyclerView?.layoutManager = gridLayoutManager
                                communityBoardRecyclerView?.adapter = communityBoardAdapter
                            }*/

                            val map = HashMap<String, String>()
                            map["images"] = snapshot.child(i.key.toString()).child("imagePath").value.toString()
                            map["profilePicURL"] = snapshot.child(i.key.toString())
                                .child("profile_photo").value.toString()
                            map["email"] =
                                snapshot.child(i.key.toString()).child("email").value.toString()
                            map["name"] =
                                snapshot.child(i.key.toString()).child("name").value.toString()
                            map["title"] =
                                snapshot.child(i.key.toString()).child("title").value.toString()
                            map["content"] =
                                snapshot.child(i.key.toString())
                                    .child("content").value.toString()
                            map["date"] =
                                snapshot.child(i.key.toString()).child("date").value.toString()
                            map["pinned"] =
                                snapshot.child(i.key.toString())
                                    .child("pinned").value.toString()
                            map["urgent"] =
                                snapshot.child(i.key.toString())
                                    .child("urgent").value.toString()
                            map["uid"] =
                                snapshot.child(i.key.toString()).child("uid").value.toString()

                            map["imageURL"] = snapshot.child(i.key.toString()).child("images").value.toString()
                            map["childPosition"] = i.key.toString()
                            dataList.add(map)

                            val sortedDataList =
                                dataList.sortedWith(
                                    compareBy(
                                        { it["pinned"] },
                                        { it["urgent"] },
                                        { it["date"] })
                                )
                                    .reversed()

                            println("sorted data List " + sortedDataList)
                            sortedData.clear()
                            for (z in sortedDataList) {
                                sortedData.add(z)
                            }

                            communityBoardAdapter =
                                CommunityBoardAdapter(requireContext(), sortedData)

                            communityBoardRecyclerView?.layoutManager = gridLayoutManager
                            communityBoardRecyclerView?.adapter = communityBoardAdapter
                        }

                        val handler = Handler(Looper.getMainLooper())
                        val runnable = Runnable {
                            loadAllD.dismiss()
                        }
                        handler.postDelayed(runnable, 500)
                        println("children count " + childrenCount)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "There was an error", Toast.LENGTH_SHORT).show()
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
            .matches()
    }
}