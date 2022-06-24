package com.cory.texarkanacollege

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
import com.google.android.material.appbar.MaterialToolbar
import com.ortiz.touchview.TouchImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ViewImageCommunityBoardPostIntent : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image_community_board_post_intent)

        val viewImageMaterialToolbar = findViewById<MaterialToolbar>(R.id.viewImageCommunityBoardPostToolBar)
        viewImageMaterialToolbar.setNavigationOnClickListener {
            finish()
        }

        val image = BitmapFactory.decodeByteArray(intent.getByteArrayExtra("image"), 0, intent.getByteArrayExtra("image")!!.size)
        val imageView = findViewById<TouchImageView>(R.id.communityBoardPostImageView)

        val circularProgressDrawableImage = CircularProgressDrawable(this)
        circularProgressDrawableImage.strokeWidth = 5f
        circularProgressDrawableImage.centerRadius = 30f
        circularProgressDrawableImage.start()

        GlobalScope.launch(Dispatchers.Main) {
            Glide.with(this@ViewImageCommunityBoardPostIntent)
                .load(image)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(circularProgressDrawableImage)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Toast.makeText(this@ViewImageCommunityBoardPostIntent, "Error loading image", Toast.LENGTH_SHORT)
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

                                val imageViewConstraint = findViewById<ConstraintLayout>(R.id.imageViewConstraintViewPost)
                                imageViewConstraint.setBackgroundColor(color)

                                imageView.setOnClickListener {
                                    finish()
                                }

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
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}