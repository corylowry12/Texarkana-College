package com.cory.texarkanacollege.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.blue
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.ViewImageCommunityBoardPostIntent
import com.cory.texarkanacollege.adapters.ViewCommunityBoardPostCommentsAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
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
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ViewCommunityBoardPostFragment : Fragment() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var communityBoardPostViewRecyclerView : RecyclerView

    private lateinit var viewCommunityBoardPostCommentsAdapter: ViewCommunityBoardPostCommentsAdapter
    private val dataList = ArrayList<HashMap<String, String>>()
    private val sortedData = ArrayList<HashMap<String, String>>()
    var childPosition: String? = ""

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
                val materialToolbar = activity?.findViewById<MaterialToolbar>(R.id.viewPostToolbar)
                database.child("posts").child(childPosition.toString()).child("likes").orderByKey()
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (i in snapshot.children) {
                                if (i.key == firebaseAuth.currentUser?.uid) {
                                    if (i.child("liked").value == true) {
                                        materialToolbar?.menu?.findItem(R.id.likePost)?.icon =
                                            ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ic_baseline_favorite_24
                                            )

                                    } else {
                                        materialToolbar?.menu?.findItem(R.id.likePost)?.icon =
                                            ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ic_baseline_favorite_border_24
                                            )
                                        Toast.makeText(
                                            requireContext(),
                                            getString(R.string.press_like_again_to_like_the_post),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        communityBoardPostViewRecyclerView =
            requireActivity().findViewById<RecyclerView>(R.id.commentRecyclerView)
        communityBoardPostViewRecyclerView.visibility = View.GONE

        swipeRefreshLayout = view.findViewById(R.id.communityBoardPostSwipeRefreshLayout)
        swipeRefreshLayout.setColorSchemeResources(R.color.blue)

        firebaseAuth = FirebaseAuth.getInstance()

        database = Firebase.database.reference

        gridLayoutManager = GridLayoutManager(requireContext(), 1)


        loadItems()

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
                Toast.makeText(
                    requireContext(),
                    getString(R.string.press_submit_again_to_submit_comment),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                submitComment(
                    commentTextInputEditText.text.toString(),
                    childPosition.toString()
                )

                loadIntoList(childPosition.toString())
                commentTextInputEditText.setText("")
            }
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
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.press_submit_again_to_submit_comment),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    submitComment(
                        commentTextInputEditText.text.toString(),
                        childPosition.toString()
                    )

                    loadIntoList(childPosition.toString())
                }
                commentTextInputEditText.setText("")
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        swipeRefreshLayout.setOnRefreshListener {
            loadItems()
            loadIntoList(childPosition.toString())
        }

        val materialToolbar = activity?.findViewById<MaterialToolbar>(R.id.viewPostToolbar)
        materialToolbar?.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        database.child("posts").child(childPosition.toString()).child("likes").orderByKey()
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        if (i.key == firebaseAuth.currentUser?.uid) {
                            if (i.child("liked").value == true) {
                                materialToolbar?.menu?.findItem(R.id.likePost)?.icon =
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_baseline_favorite_24
                                    )

                            } else {
                                materialToolbar?.menu?.findItem(R.id.likePost)?.icon =
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_baseline_favorite_border_24
                                    )
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        materialToolbar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.likePost -> {
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
                        database.child("posts").child(childPosition.toString()).child("likes")
                            .orderByKey()
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    if (snapshot.children.count() == 0) {
                                        database.child("posts")
                                            .child((childPosition).toString())
                                            .child("likes")
                                            .child(firebaseAuth.currentUser!!.uid)
                                            .child("liked")
                                            .setValue(true)
                                        database.child("posts")
                                            .child((childPosition).toString())
                                            .child("likes")
                                            .child(firebaseAuth.currentUser!!.uid)
                                            .child("profilePicURL")
                                            .setValue(firebaseAuth.currentUser!!.photoUrl.toString())
                                        database.child("posts")
                                            .child((childPosition).toString())
                                            .child("likes")
                                            .child(firebaseAuth.currentUser!!.uid)
                                            .child("name")
                                            .setValue(firebaseAuth.currentUser!!.displayName)
                                        materialToolbar.menu?.findItem(R.id.likePost)?.icon =
                                            ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ic_baseline_favorite_24
                                            )
                                    } else {
                                        for (i in snapshot.children) {
                                            if (i.key.toString() == firebaseAuth.currentUser!!.uid) {
                                                if (i.child("liked").value == false) {
                                                    database.child("posts")
                                                        .child((childPosition).toString())
                                                        .child("likes")
                                                        .child(firebaseAuth.currentUser!!.uid)
                                                        .child("liked")
                                                        .setValue(true)
                                                    database.child("posts")
                                                        .child((childPosition).toString())
                                                        .child("likes")
                                                        .child(firebaseAuth.currentUser!!.uid)
                                                        .child("profilePicURL")
                                                        .setValue(firebaseAuth.currentUser!!.photoUrl.toString())
                                                    database.child("posts")
                                                        .child((childPosition).toString())
                                                        .child("likes")
                                                        .child(firebaseAuth.currentUser!!.uid)
                                                        .child("name")
                                                        .setValue(firebaseAuth.currentUser!!.displayName)
                                                    materialToolbar.menu?.findItem(R.id.likePost)?.icon =
                                                        ContextCompat.getDrawable(
                                                            requireContext(),
                                                            R.drawable.ic_baseline_favorite_24
                                                        )
                                                } else {
                                                    database.child("posts")
                                                        .child((childPosition).toString())
                                                        .child("likes")
                                                        .child(firebaseAuth.currentUser!!.uid)
                                                        .child("liked")
                                                        .setValue(false)
                                                    database.child("posts")
                                                        .child((childPosition).toString())
                                                        .child("likes")
                                                        .child(firebaseAuth.currentUser!!.uid)
                                                        .child("profilePicURL")
                                                        .setValue(firebaseAuth.currentUser!!.photoUrl.toString())
                                                    database.child("posts")
                                                        .child((childPosition).toString())
                                                        .child("likes")
                                                        .child(firebaseAuth.currentUser!!.uid)
                                                        .child("name")
                                                        .setValue(firebaseAuth.currentUser!!.displayName)
                                                    materialToolbar.menu?.findItem(R.id.likePost)?.icon =
                                                        ContextCompat.getDrawable(
                                                            requireContext(),
                                                            R.drawable.ic_baseline_favorite_border_24
                                                        )
                                                }
                                                break
                                            } else {
                                                database.child("posts")
                                                    .child((childPosition).toString())
                                                    .child("likes")
                                                    .child(firebaseAuth.currentUser!!.uid)
                                                    .child("liked")
                                                    .setValue(true)
                                                database.child("posts")
                                                    .child((childPosition).toString())
                                                    .child("likes")
                                                    .child(firebaseAuth.currentUser!!.uid)
                                                    .child("profilePicURL")
                                                    .setValue(firebaseAuth.currentUser!!.photoUrl.toString())
                                                database.child("posts")
                                                    .child((childPosition).toString())
                                                    .child("likes")
                                                    .child(firebaseAuth.currentUser!!.uid)
                                                    .child("name")
                                                    .setValue(firebaseAuth.currentUser!!.displayName)
                                                materialToolbar.menu?.findItem(R.id.likePost)?.icon =
                                                    ContextCompat.getDrawable(
                                                        requireContext(),
                                                        R.drawable.ic_baseline_favorite_24
                                                    )
                                                break
                                            }
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })
                    }
                    true
                }
                else -> false
            }

        }
    }

    private fun loadItems() {

        val args = arguments
        val name = args?.getString("name", "")
        val title = args?.getString("title", "")
        val content = args?.getString("content", "")
        val imageURL = args?.getString("imageURL", "")
        childPosition = args?.getString("childPosition", "")
        val date = args?.getString("date", "")
        val profilePicURL = args?.getString("profilePicURL", "")
        val email = args?.getString("email", "")
        val pinned = args?.getString("pinned", "")

        val pinnedChip = requireActivity().findViewById<Chip>(R.id.communityBoardPostPinnedChip)
        if (pinned == "true") {
            pinnedChip.visibility = View.VISIBLE
        }
        else {
            pinnedChip.visibility = View.GONE
        }

        val dateChip = activity?.findViewById<Chip>(R.id.dateChip)
        dateChip?.text = date

        val circularProgressDrawable = CircularProgressDrawable(requireContext())
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.blue))
        circularProgressDrawable.start()

        val circularProgressDrawableImage = CircularProgressDrawable(requireContext())
        circularProgressDrawableImage.strokeWidth = 5f
        circularProgressDrawableImage.centerRadius = 30f
        circularProgressDrawableImage.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.blue))
        circularProgressDrawableImage.start()

        val profileImageView =
            requireActivity().findViewById<CircleImageView>(R.id.profilePicViewPost)

        Glide.with(requireContext())
            .load(profilePicURL)
            .placeholder(circularProgressDrawable)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
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
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        imageView?.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        imageView?.visibility = View.VISIBLE


                        return false
                    }
                })
                .into(imageView!!)
        }

        imageView.setOnClickListener {
            try {
                val intent = Intent(requireContext(), ViewImageCommunityBoardPostIntent::class.java)
                val bs = ByteArrayOutputStream()
                val b: Bitmap = imageView.drawable.toBitmap()
                b.compress(Bitmap.CompressFormat.PNG, 100, bs)
                intent.putExtra("image", bs.toByteArray())
                startActivity(intent)
            }
            catch (e: Exception) {
                Toast.makeText(requireContext(), "There was an error", Toast.LENGTH_SHORT).show()
            }
        }

        val nameTextView = activity?.findViewById<TextView>(R.id.nameTextView)
        val titleTextView = activity?.findViewById<TextView>(R.id.postTitleTextView)
        val contentTextView = activity?.findViewById<TextView>(R.id.postContentTextView)
        val emailTextView = activity?.findViewById<TextView>(R.id.emailTextView)

        nameTextView!!.text = name
        titleTextView!!.text = "Title: " + title
        contentTextView!!.text = content
        emailTextView!!.text = email

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
            database.child("posts").child(childPosition).child("comments")
                .child((childrenCount + 1).toString()).child("uid")
                .setValue(firebaseAuth.currentUser!!.uid)
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

    fun setTextView() {
        loadIntoList(childPosition.toString())
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
                        activity?.findViewById<RecyclerView>(R.id.commentRecyclerView)!!.visibility =
                            View.GONE
                        activity?.findViewById<TextView>(R.id.noCommentsTextView)!!.visibility =
                            View.VISIBLE
                    } else {
                        activity?.findViewById<RecyclerView>(R.id.commentRecyclerView)!!.visibility =
                            View.VISIBLE
                        activity?.findViewById<TextView>(R.id.noCommentsTextView)!!.visibility =
                            View.GONE
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

                        communityBoardPostViewRecyclerView.layoutManager = gridLayoutManager
                        communityBoardPostViewRecyclerView.adapter =
                            viewCommunityBoardPostCommentsAdapter
                        communityBoardPostViewRecyclerView.visibility = View.VISIBLE

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