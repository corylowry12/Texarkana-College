package com.cory.texarkanacollege.fragments

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.adapters.ViewCommunityBoardPostCommentsAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
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
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ViewCommunityBoardPostFragment : Fragment() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var viewCommunityBoardPostCommentsAdapter: ViewCommunityBoardPostCommentsAdapter
    private val dataList = ArrayList<HashMap<String, String>>()
    private val sortedData = ArrayList<HashMap<String, String>>()

    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var database: DatabaseReference
    private lateinit var gridLayoutManager: GridLayoutManager

    var childrenCount = 0L

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {

                Toast.makeText(
                    requireContext(),
                    "You have successfully been logged in",
                    Toast.LENGTH_SHORT
                ).show()
                activity?.findViewById<MaterialToolbar>(R.id.materialToolBarCommunityBoard)?.menu?.findItem(
                    R.id.signOut
                )?.title = "Sign Out"
            } else if (it.exception is FirebaseAuthInvalidUserException) {
                Toast.makeText(requireContext(), "Sorry, you have been banned", Toast.LENGTH_SHORT)
                    .show()
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
            Toast.makeText(
                requireContext(),
                "Error: " + e.statusCode.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_community_board_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val materialToolbar = activity?.findViewById<MaterialToolbar>(R.id.viewPostToolbar)
        materialToolbar?.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        swipeRefreshLayout = view.findViewById(R.id.communityBoardPostSwipeRefreshLayout)

        firebaseAuth = FirebaseAuth.getInstance()

        database = Firebase.database.reference

        gridLayoutManager = GridLayoutManager(requireContext(), 1)

        val args = arguments
        val name = args?.getString("name", "")
        val title = args?.getString("title", "")
        val content = args?.getString("content", "")
        val imageURL = args?.getString("imageURL", "")
        val childPosition = args?.getString("childPosition", "")
        val date = args?.getString("date", "")
        val profilePicURL = args?.getString("profilePicURL", "")
        val email = args?.getString("email", "")

        val dateChip = activity?.findViewById<Chip>(R.id.dateChip)
        dateChip?.text = date

        val circularProgressDrawable = CircularProgressDrawable(requireContext())
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        val circularProgressDrawableImage = CircularProgressDrawable(requireContext())
        circularProgressDrawableImage.strokeWidth = 5f
        circularProgressDrawableImage.centerRadius = 30f
        circularProgressDrawableImage.start()

        val profileImageView =
            requireActivity().findViewById<CircleImageView>(R.id.profilePicViewPost)

        Glide.with(requireContext())
            .load(profilePicURL)
            .error(R.drawable.ic_baseline_broken_image_24)
            .placeholder(circularProgressDrawable)
            .skipMemoryCache(true)
            .into(profileImageView!!)

        val imageView = activity?.findViewById<ImageView>(R.id.imageView)

        if (imageURL == "" || imageURL == null) {
            imageView!!.visibility = View.GONE
        } else {
            Glide.with(requireContext())
                .load(imageURL)
                .error(R.drawable.ic_baseline_broken_image_24)
                .fitCenter()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(circularProgressDrawableImage)
                .into(imageView!!)
        }

        val nameTextView = activity?.findViewById<TextView>(R.id.nameTextView)
        val titleTextView = activity?.findViewById<TextView>(R.id.postTitleTextView)
        val contentTextView = activity?.findViewById<TextView>(R.id.postContentTextView)
        val emailTextView = activity?.findViewById<TextView>(R.id.emailTextView)

        nameTextView!!.text = name
        titleTextView!!.text = "Title: " + title
        contentTextView!!.text = content
        emailTextView!!.text = email

        loadIntoList(childPosition.toString())

        val commentTextInputEditText =
            activity?.findViewById<TextInputEditText>(R.id.commentTextInputEditText)
        val submitButton = activity?.findViewById<Button>(R.id.submitButton)
        submitButton?.setOnClickListener {
            hideKeyboard(commentTextInputEditText!!)
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
                submitComment(
                    commentTextInputEditText.text.toString(),
                    childPosition.toString()
                )

                loadIntoList(childPosition.toString())
            }
            commentTextInputEditText.setText("")
        }
        commentTextInputEditText!!.setOnEditorActionListener { _, i, _ ->
            hideKeyboard(commentTextInputEditText)
            if (i == EditorInfo.IME_ACTION_DONE) {
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
                    submitComment(
                        commentTextInputEditText.text.toString(),
                        childPosition.toString()
                    )

                    loadIntoList(childPosition.toString())
                }
                return@setOnEditorActionListener true
            }
            commentTextInputEditText.setText("")
            return@setOnEditorActionListener false
        }

        swipeRefreshLayout.setOnRefreshListener {
            loadIntoList(childPosition.toString())
        }
    }

    private fun submitComment(commentText: String, childPosition: String) {
        if (commentText == "") {
            Toast.makeText(
                requireContext(),
                "There was no comment in the text box, can't submit",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            database.child("posts").child((childPosition))
                .child("comments").child((childrenCount + 1).toString()).child("title")
                .setValue(commentText)
            database.child("posts").child((childPosition))
                .child("comments").child((childrenCount + 1).toString()).child("name")
                .setValue(firebaseAuth.currentUser!!.displayName.toString())
            database.child("posts").child((childPosition))
                .child("comments").child((childrenCount + 1).toString()).child("profile_pic")
                .setValue(firebaseAuth.currentUser!!.photoUrl.toString())
            database.child("posts").child(childPosition).child("comments").child((childrenCount + 1).toString()).child("uid").setValue(firebaseAuth.currentUser!!.uid)
            val formatter =
                SimpleDateFormat("MMM/dd/yyyy HH:mm aa", Locale.ENGLISH)
            val dateFormatted = formatter.format(Date())
            database.child("posts").child((childPosition))
                .child("comments").child((childrenCount + 1).toString()).child("date")
                .setValue(dateFormatted)
        }
    }

    private fun hideKeyboard(commentEditText: TextInputEditText) {
        val inputManager: InputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val focusedView = activity?.currentFocus

        if (focusedView != null) {
            inputManager.hideSoftInputFromWindow(
                focusedView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
            if (commentEditText.hasFocus()) {
                commentEditText.clearFocus()
            }
        }
    }


    fun loadIntoList(childPosition: String) {

        viewCommunityBoardPostCommentsAdapter =
            ViewCommunityBoardPostCommentsAdapter(requireContext(), dataList)

        database.child("posts/$childPosition/comments").orderByKey().limitToLast(1)
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

        database.child("posts/$childPosition/comments").orderByKey()
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dataList.clear()
                    sortedData.clear()

                    if (snapshot.children.count() == 0) {
                        swipeRefreshLayout.isRefreshing = false
                        activity?.findViewById<RecyclerView>(R.id.commentRecyclerView)!!.visibility = View.GONE
                        activity?.findViewById<TextView>(R.id.noCommentsTextView)!!.visibility = View.VISIBLE
                    } else {
                        activity?.findViewById<RecyclerView>(R.id.commentRecyclerView)!!.visibility = View.VISIBLE
                        activity?.findViewById<TextView>(R.id.noCommentsTextView)!!.visibility = View.GONE
                        for (i in snapshot.children) {
                            println("children " + i.toString())
                            val map = java.util.HashMap<String, String>()
                            map["title"] =
                                snapshot.child(i.key.toString()).child("title").value.toString()
                            map["name"] =
                                snapshot.child(i.key.toString()).child("name").value.toString()
                            map["profilePicURL"] =
                                snapshot.child(i.key.toString())
                                    .child("profile_pic").value.toString()
                            map["date"] =
                                snapshot.child(i.key.toString()).child("date").value.toString()
                            map["uid"] =
                                snapshot.child(i.key.toString()).child("uid").value.toString()
                            map["childPosition"] = childPosition
                            map["commentPosition"] = i.key.toString()
                            dataList.add(map)

                            viewCommunityBoardPostCommentsAdapter =
                                ViewCommunityBoardPostCommentsAdapter(requireContext(), dataList)

                        }

                        val sortedDataList =
                            dataList.sortedWith(compareBy { it["date"] }).reversed()

                        println("sorted data List " + sortedDataList)

                        for (i in sortedDataList) {
                            sortedData.add(i)
                        }

                        val communityBoardPostViewRecyclerView =
                            activity?.findViewById<RecyclerView>(R.id.commentRecyclerView)
                        communityBoardPostViewRecyclerView?.layoutManager = gridLayoutManager
                        communityBoardPostViewRecyclerView?.adapter =
                            viewCommunityBoardPostCommentsAdapter

                        println("children count " + childrenCount)
                        swipeRefreshLayout.isRefreshing = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    swipeRefreshLayout.isRefreshing = false
                }
            })
    }
}