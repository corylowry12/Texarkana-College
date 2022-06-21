package com.cory.texarkanacollege.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.blue
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.palette.graphics.Palette
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cory.texarkanacollege.database.GradesDBHelper
import com.cory.texarkanacollege.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.api.ResourceProto.resource
import com.ortiz.touchview.TouchImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ImageViewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_view, container, false)
    }

    @SuppressLint("Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewImageMaterialToolbar = requireActivity().findViewById<MaterialToolbar>(R.id.viewImageToolBar)
        viewImageMaterialToolbar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        val args = arguments?.getInt("image")
        val id = arguments?.getInt("id")

        val dbHandler = GradesDBHelper(requireContext(), null)

        val cursor = dbHandler.getImage(args.toString(), id.toString())
        cursor.moveToFirst()

            val imageMap = HashMap<String, String>()

                imageMap["image"] =
                    cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_IMAGE))

                val imageView = view.findViewById<TouchImageView>(R.id.imageView)

        val circularProgressDrawable = CircularProgressDrawable(requireContext())
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        GlobalScope.launch(Dispatchers.Main) {
            Glide.with(requireContext())
                .load(imageMap["image"].toString())
                .centerCrop()
                .placeholder(circularProgressDrawable)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Toast.makeText(requireContext(), "Error loading image", Toast.LENGTH_SHORT)
                            .show()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {

                        Palette.Builder(resource!!.toBitmap(1920, 1080)).generate { palette ->
                            val vSwatch = palette?.dominantSwatch?.rgb
                            val color =
                                Color.rgb(
                                    vSwatch!!.red,
                                    vSwatch.green,
                                    vSwatch.blue
                                )
                            try {

                                val imageViewConstraint =
                                    requireActivity().findViewById<ConstraintLayout>(R.id.imageViewConstraint)
                                imageViewConstraint.setBackgroundColor(color)

                            } catch (e: NullPointerException) {
                                e.printStackTrace()
                            }
                        }

                        return false
                    }

                })
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView)
        }

        imageView.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.supportFragmentManager?.popBackStack()
                }
            })
    }
}