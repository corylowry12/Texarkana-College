package com.cory.texarkanacollege.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cory.texarkanacollege.R
import com.cory.texarkanacollege.adapters.ViewCommunityBoardPostCommentsAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewCommunityBoardPostFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var viewCommunityBoardPostCommentsAdapter: ViewCommunityBoardPostCommentsAdapter
    private val dataList = ArrayList<HashMap<String, String>>()

    private lateinit var database: DatabaseReference
    private lateinit var gridLayoutManager: GridLayoutManager

    var childrenCount = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_community_board_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        database = Firebase.database.reference

        gridLayoutManager = GridLayoutManager(requireContext(), 1)

        val args = arguments
        val name = args?.getString("name", "")
        val title = args?.getString("title", "")
        val content = args?.getString("content", "")
        val imageURL = args?.getString("imageURL", "")
        val childPosition = args?.getString("childPosition", "")

        val imageView = activity?.findViewById<ImageView>(R.id.imageView)

        if (imageURL == "") {
            imageView!!.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                R.drawable.ic_baseline_hide_image_24
            ))
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        }
        else {
            Glide.with(requireContext())
                .load(imageURL)
                .centerCrop()
                .into(imageView!!)
        }

        val nameTextView = activity?.findViewById<TextView>(R.id.nameTextView)
        val titleTextView = activity?.findViewById<TextView>(R.id.postTitleTextView)
        val contentTextView = activity?.findViewById<TextView>(R.id.postContentTextView)

        nameTextView!!.text = "Name: " + name
        titleTextView!!.text = "Title: " + title
        contentTextView!!.text = "Content: " + content

        loadIntoList(childPosition.toString())

        val commentTextInputEditText = activity?.findViewById<TextInputEditText>(R.id.commentTextInputEditText)
        commentTextInputEditText!!.setOnEditorActionListener { view, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                Toast.makeText(requireContext(), "Done clicked", Toast.LENGTH_SHORT).show()
                database.child("posts").child((childPosition).toString())
                    .child("comments").child((childrenCount+1).toString()).child("title").setValue(commentTextInputEditText.text.toString())
                database.child("posts").child((childPosition).toString())
                    .child("comments").child((childrenCount+1).toString()).child("name").setValue(firebaseAuth.currentUser!!.displayName.toString())

                loadIntoList(childPosition.toString())
                return@setOnEditorActionListener true
            }
            Toast.makeText(requireContext(), i.toString(), Toast.LENGTH_SHORT).show()
            return@setOnEditorActionListener false
        }
    }


    fun loadIntoList(childPosition: String) {

        viewCommunityBoardPostCommentsAdapter = ViewCommunityBoardPostCommentsAdapter(requireContext(), dataList)

        database.child("posts/$childPosition/comments").orderByKey().limitToLast(1).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (i in snapshot.children) {
                    childrenCount = i.key.toString().toLong()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        database.child("posts/$childPosition/comments").orderByKey().addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                //sortedData.clear()

                for (i in snapshot.children) {
                    println("children " + i.toString())
                    val map = java.util.HashMap<String, String>()
                    map["title"] = snapshot.child(i.key.toString()).child("title").value.toString()
                    map["name"] = snapshot.child(i.key.toString()).child("name").value.toString()
                    dataList.add(map)

                    viewCommunityBoardPostCommentsAdapter = ViewCommunityBoardPostCommentsAdapter(requireContext(), dataList)

                }

                //val sortedDataList = dataList.sortedWith(compareBy ({ it["pinned"] }, {it["date"]})).reversed()

               // println("sorted data List " + sortedDataList)

                /*for (i in sortedDataList) {
                    sortedData.add(i)
                }*/

                val communityBoardPostViewRecyclerView =
                    activity?.findViewById<RecyclerView>(R.id.commentRecyclerView)
                communityBoardPostViewRecyclerView?.layoutManager = gridLayoutManager
                communityBoardPostViewRecyclerView?.adapter = viewCommunityBoardPostCommentsAdapter

                println("children count " + childrenCount)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}