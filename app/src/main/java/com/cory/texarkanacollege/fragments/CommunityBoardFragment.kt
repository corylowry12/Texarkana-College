package com.cory.texarkanacollege.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
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
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.suke.widget.SwitchButton
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CommunityBoardFragment : Fragment() {

    private lateinit var communityBoardAdapter: CommunityBoardAdapter
    private val dataList = ArrayList<HashMap<String, String>>()
    private val sortedData = ArrayList<HashMap<String, String>>()

    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var firebaseStorage : Firebase

    private lateinit var gridLayoutManager: GridLayoutManager

    private lateinit var database: DatabaseReference

    var childArray = arrayListOf<String>()

    var childrenCount = 0L

    var imagePath = ""
    lateinit var image : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

                Toast.makeText(requireContext(), "Sucessfull", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    val getSignInData = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
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
            val selectedFile = File(getRealPathFromURI(selectedImage!!))
            this.imagePath = Date().toString()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.app_client_id))
                .requestEmail()
                .build()
            mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), mGoogleSignInOptions)
            val signInIntent: Intent = mGoogleSignInClient.signInIntent
            getSignInData.launch(signInIntent)
        }

        database = Firebase.database.reference
        val storage = FirebaseStorage.getInstance()

        gridLayoutManager = GridLayoutManager(requireContext(), 1)
        communityBoardAdapter = CommunityBoardAdapter(requireContext(), dataList)

        loadIntoList()

        val swipeRefreshLayout = requireView().findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayoutCommunityBoard)
        swipeRefreshLayout.setOnRefreshListener {
            loadIntoList()
            swipeRefreshLayout.isRefreshing = false
        }

        val toolBar = requireView().findViewById<MaterialToolbar>(R.id.materialToolBarCommunityBoard)

        toolBar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        toolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.addClass -> {
                    val dialog = BottomSheetDialog(requireContext())
                    val addGradeView = layoutInflater.inflate(R.layout.add_post_bottom_sheet, null)
                    dialog.setCancelable(false)
                    dialog.setContentView(addGradeView)
                    val addGradeButton = dialog.findViewById<Button>(R.id.addGradeButton)
                    val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)
                    val nameEditText = dialog.findViewById<TextInputEditText>(R.id.name)
                    val titleEditText = dialog.findViewById<TextInputEditText>(R.id.grade)
                    val postEditText = dialog.findViewById<TextInputEditText>(R.id.weight)
                    val pinnedSwitch = dialog.findViewById<SwitchButton>(R.id.pinnedSwitch)
                    val urgentSwitch = dialog.findViewById<SwitchButton>(R.id.urgenSwitch)
                    val addImageButton = dialog.findViewById<Button>(R.id.addImage)

                    addImageButton?.setOnClickListener {
                        val pickerIntent = Intent(Intent.ACTION_PICK)
                        pickerIntent.type = "image/*"

                        showImagePicker.launch(pickerIntent)
                    }

                    addGradeButton?.setOnClickListener {
                        if (nameEditText!!.text.toString() != "" && titleEditText!!.text.toString() != "" && postEditText!!.text.toString() != "") {
                            if (this::image.isInitialized) {
                                val ref = storage.reference.child("images/$imagePath")
                                ref.putFile(image).addOnSuccessListener {
                                    database.child("posts").child((childrenCount + 1).toString())
                                        .child("title").setValue(titleEditText.text.toString())
                                    database.child("posts").child((childrenCount + 1).toString())
                                        .child("name").setValue(nameEditText.text.toString())
                                    database.child("posts").child((childrenCount + 1).toString())
                                        .child("content").setValue(postEditText.text.toString())

                                    val formatter = SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
                                    val dateFormatted = formatter.format(Date())

                                    database.child("posts").child((childrenCount + 1).toString())
                                        .child("date").setValue(dateFormatted)
                                    database.child("posts").child((childrenCount + 1).toString())
                                        .child("pinned").setValue(pinnedSwitch!!.isChecked)
                                    database.child("posts").child((childrenCount + 1).toString())
                                        .child("urgent").setValue(urgentSwitch!!.isChecked)
                                    database.child("posts").child((childrenCount + 1).toString())
                                        .child("uid").setValue(firebaseAuth.currentUser!!.uid)
                                    var result = it.metadata!!.reference!!.downloadUrl
                                    result.addOnSuccessListener { downloadLink ->
                                        database.child("posts")
                                            .child((childrenCount + 1).toString())
                                            .child("images").setValue(downloadLink.toString())
                                    }

                                    println("database changed")
                                    dialog.dismiss()
                                    loadIntoList()
                                }
                            }
                            else {
                                    database.child("posts").child((childrenCount + 1).toString())
                                        .child("title").setValue(titleEditText.text.toString())
                                    database.child("posts").child((childrenCount + 1).toString())
                                        .child("name").setValue(nameEditText.text.toString())
                                    database.child("posts").child((childrenCount + 1).toString())
                                        .child("content").setValue(postEditText.text.toString())

                                    val formatter = SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH)
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

                                    dialog.dismiss()
                                    loadIntoList()
                                }
                        }
                    }

                    cancelButton?.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()

                    true
                }
                else -> false
            }
        }
    }

    fun loadIntoList() {

        database.child("posts").orderByKey().limitToLast(1).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (i in snapshot.children) {
                    childrenCount = i.key.toString().toLong()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        database.child("posts").orderByKey().addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                sortedData.clear()

                for (i in snapshot.children) {
                    println("children " + i.toString())
                    val map = java.util.HashMap<String, String>()
                    map["name"] = snapshot.child(i.key.toString()).child("name").value.toString()
                    map["title"] = snapshot.child(i.key.toString()).child("title").value.toString()
                    map["content"] = snapshot.child(i.key.toString()).child("content").value.toString()
                    map["date"] = snapshot.child(i.key.toString()).child("date").value.toString()
                    map["pinned"] = snapshot.child(i.key.toString()).child("pinned").value.toString()
                    map["urgent"] = snapshot.child(i.key.toString()).child("urgent").value.toString()
                    map["uid"] = snapshot.child(i.key.toString()).child("uid").value.toString()
                    map["imageURL"] = snapshot.child(i.key.toString()).child("images").value.toString()
                    map["childPosition"] = i.key.toString()
                    dataList.add(map)

                    communityBoardAdapter = CommunityBoardAdapter(requireContext(), dataList)

                }

                val sortedDataList = dataList.sortedWith(compareBy ({ it["pinned"] }, {it["date"]})).reversed()

                println("sorted data List " + sortedDataList)

                for (i in sortedDataList) {
                    sortedData.add(i)
                }

                val communityBoardRecyclerView =
                    activity?.findViewById<RecyclerView>(R.id.communityBoardRecyclerView)
                communityBoardRecyclerView?.layoutManager = gridLayoutManager
                communityBoardRecyclerView?.adapter = CommunityBoardAdapter(requireContext(), sortedData)

                println("children count " + childrenCount)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}