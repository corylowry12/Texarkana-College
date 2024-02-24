package com.cory.texarkanacollege

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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
import com.cory.texarkanacollege.classes.DarkThemeData
import com.google.android.material.appbar.MaterialToolbar
import com.ortiz.touchview.TouchImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ViewImageCommunityBoardPostIntent : AppCompatActivity() {

    lateinit var bitmap : Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val darkThemeData = DarkThemeData(this)
        when {
            darkThemeData.loadState() == 1 -> {
                setTheme(R.style.Dark)
            }
            darkThemeData.loadState() == 0 -> {
                setTheme(R.style.Theme_MyApplication)
            }
            darkThemeData.loadState() == 2 -> {
                when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_NO -> {
                        setTheme(R.style.Theme_MyApplication)
                    }
                    Configuration.UI_MODE_NIGHT_YES -> {
                        setTheme(R.style.Dark)
                    }
                    Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                        setTheme(R.style.Dark)
                    }
                }
            }
        }
        setContentView(R.layout.activity_view_image_community_board_post_intent)

        val viewImageMaterialToolbar = findViewById<MaterialToolbar>(R.id.viewImageCommunityBoardPostToolBar)
        viewImageMaterialToolbar.setNavigationOnClickListener {
            finishAfterTransition()
        }

        //val image = BitmapFactory.decodeByteArray(intent.getByteArrayExtra("image"), 0, (intent.getByteArrayExtra("image")!!.size).toInt())
        val imageView = findViewById<TouchImageView>(R.id.communityBoardPostImageView)

        val imageURL = intent.getStringExtra("image")

        imageView.setOnClickListener {
            this.finishAfterTransition()
        }

       // imageView.setImageBitmap(bitmap)

            //val bitmap = Bitmap.createBitmap(image, 0, 0, image.width, image.height, Matrix(), true)

            //imageView.setImageBitmap(bitmap)
        val circularProgressDrawableImage = CircularProgressDrawable(this)
        circularProgressDrawableImage.strokeWidth = 5f
        circularProgressDrawableImage.centerRadius = 30f
        circularProgressDrawableImage.setColorSchemeColors(ContextCompat.getColor(this, R.color.blue))
        circularProgressDrawableImage.start()


        Glide.with(this)
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
                    //imageView?.visibility = View.GONE
                    Toast.makeText(this@ViewImageCommunityBoardPostIntent, "Failed loading image", Toast.LENGTH_SHORT).show()
                    finishAfterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    //imageView?.visibility = View.VISIBLE

                    Palette.Builder(resource!!.toBitmap(100,100)).generate { palette ->
                        val vSwatch = palette?.dominantSwatch?.rgb
                        val color =
                            Color.rgb(
                                vSwatch!!.red,
                                vSwatch.green,
                                vSwatch.blue
                            )

                        val invertedColor = Color.rgb(255 - vSwatch.red, 255 - vSwatch.green,
                            255 - vSwatch.blue)
                        try {
                            val imageViewConstraint = findViewById<ConstraintLayout>(R.id.imageViewConstraintViewPost)
                            imageViewConstraint.setBackgroundColor(color)
                            viewImageMaterialToolbar.setNavigationIconTint(invertedColor)

                            this@ViewImageCommunityBoardPostIntent.window.navigationBarColor = color

                        } catch (e: NullPointerException) {
                            e.printStackTrace()
                        }
                    }
                    return false
                }
            })
            .into(imageView!!)

            /**/
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finishAfterTransition()
    }
}