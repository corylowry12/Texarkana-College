package com.cory.texarkanacollege

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.cory.texarkanacollege.classes.DarkThemeData
import com.cory.texarkanacollege.database.GradesDBHelper
import com.google.android.material.appbar.MaterialToolbar
import com.google.api.ResourceProto.resource
import com.ortiz.touchview.TouchImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class ViewImageActivity : AppCompatActivity() {
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        val viewImageMaterialToolbar = findViewById<MaterialToolbar>(R.id.viewImageToolBar)
        viewImageMaterialToolbar.setNavigationOnClickListener {
            finishAfterTransition()
        }

        GlobalScope.launch(Dispatchers.Main) {
        val args = intent.getIntExtra("image", 0)
        val id = intent.getIntExtra("id", 0)

        val dbHandler = GradesDBHelper(this@ViewImageActivity, null)

        val cursor = dbHandler.getImage(args.toString(), id.toString())
        cursor.moveToFirst()

        val imageMap = HashMap<String, String>()

        imageMap["image"] =
            cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_IMAGE))

        val imageView = findViewById<TouchImageView>(R.id.imageView)

        imageView.setOnClickListener {
            this@ViewImageActivity.finishAfterTransition()
        }

            val imagePath = File(imageMap["image"].toString())
            val bitmapSrc = BitmapFactory.decodeFile(imageMap["image"])

            if (bitmapSrc.width >= 9000 && bitmapSrc.height >= 9000) {
            val bitmapScaled = Bitmap.createScaledBitmap(bitmapSrc, bitmapSrc.width / 6, bitmapSrc.height / 6, true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val ei = ExifInterface(imagePath)

                val orientation = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )

                val m = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> {
                        m.postRotate(90f)
                    }
                    ExifInterface.ORIENTATION_ROTATE_180 -> {
                        m.postRotate(180f)
                    }
                    ExifInterface.ORIENTATION_ROTATE_270 -> {
                        m.postRotate(270f)
                    }
                }
                val bitmap =
                    Bitmap.createBitmap(
                        bitmapScaled,
                        0,
                        0,
                        bitmapScaled.width,
                        bitmapScaled.height,
                        m,
                        true
                    )

                imageView.setImageBitmap(bitmap)

                Palette.Builder(bitmap).generate { palette ->
                    val vSwatch = palette?.dominantSwatch?.rgb
                    val color =
                        Color.rgb(
                            vSwatch!!.red,
                            vSwatch.green,
                            vSwatch.blue
                        )

                    val invertedColor = Color.rgb(
                        255 - vSwatch.red, 255 - vSwatch.green,
                        255 - vSwatch.blue
                    )
                    try {
                        val imageViewConstraint =
                            findViewById<ConstraintLayout>(R.id.imageViewConstraint)
                        imageViewConstraint.setBackgroundColor(color)
                        viewImageMaterialToolbar.setNavigationIconTint(invertedColor)

                        this@ViewImageActivity.window.navigationBarColor = color

                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    }
                }
            }
            else {
                val bitmap = Bitmap.createBitmap(
                    bitmapSrc,
                    0,
                    0,
                    bitmapSrc.width,
                    bitmapSrc.height,
                    Matrix(),
                    true
                )

                imageView.setImageBitmap(bitmap)

                Palette.Builder(bitmap).generate { palette ->
                    val vSwatch = palette?.dominantSwatch?.rgb
                    val color =
                        Color.rgb(
                            vSwatch!!.red,
                            vSwatch.green,
                            vSwatch.blue
                        )

                    val invertedColor = Color.rgb(
                        255 - vSwatch.red, 255 - vSwatch.green,
                        255 - vSwatch.blue
                    )
                    try {
                        val imageViewConstraint =
                            findViewById<ConstraintLayout>(R.id.imageViewConstraint)
                        imageViewConstraint.setBackgroundColor(color)
                        viewImageMaterialToolbar.setNavigationIconTint(invertedColor)

                        this@ViewImageActivity.window.navigationBarColor = color

                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    }
                }
            }
            }
            else if (bitmapSrc.width > 3000 && bitmapSrc.height > 3000) {
                val bitmapScaled = Bitmap.createScaledBitmap(bitmapSrc, bitmapSrc.width / 2, bitmapSrc.height / 2, true)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val ei = ExifInterface(imagePath)

                    val orientation = ei.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED
                    )

                    val m = Matrix()
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> {
                            m.postRotate(90f)
                        }
                        ExifInterface.ORIENTATION_ROTATE_180 -> {
                            m.postRotate(180f)
                        }
                        ExifInterface.ORIENTATION_ROTATE_270 -> {
                            m.postRotate(270f)
                        }
                    }
                    val bitmap =
                        Bitmap.createBitmap(
                            bitmapScaled,
                            0,
                            0,
                            bitmapScaled.width,
                            bitmapScaled.height,
                            m,
                            true
                        )

                    imageView.setImageBitmap(bitmap)

                    Palette.Builder(bitmap).generate { palette ->
                        val vSwatch = palette?.dominantSwatch?.rgb
                        val color =
                            Color.rgb(
                                vSwatch!!.red,
                                vSwatch.green,
                                vSwatch.blue
                            )

                        val invertedColor = Color.rgb(
                            255 - vSwatch.red, 255 - vSwatch.green,
                            255 - vSwatch.blue
                        )
                        try {
                            val imageViewConstraint =
                                findViewById<ConstraintLayout>(R.id.imageViewConstraint)
                            imageViewConstraint.setBackgroundColor(color)
                            viewImageMaterialToolbar.setNavigationIconTint(invertedColor)

                            this@ViewImageActivity.window.navigationBarColor = color

                        } catch (e: NullPointerException) {
                            e.printStackTrace()
                        }
                    }
                }
                else {
                    val bitmap = Bitmap.createBitmap(
                        bitmapSrc,
                        0,
                        0,
                        bitmapSrc.width,
                        bitmapSrc.height,
                        Matrix(),
                        true
                    )

                    imageView.setImageBitmap(bitmap)

                    Palette.Builder(bitmap).generate { palette ->
                        val vSwatch = palette?.dominantSwatch?.rgb
                        val color =
                            Color.rgb(
                                vSwatch!!.red,
                                vSwatch.green,
                                vSwatch.blue
                            )

                        val invertedColor = Color.rgb(
                            255 - vSwatch.red, 255 - vSwatch.green,
                            255 - vSwatch.blue
                        )
                        try {
                            val imageViewConstraint =
                                findViewById<ConstraintLayout>(R.id.imageViewConstraint)
                            imageViewConstraint.setBackgroundColor(color)
                            viewImageMaterialToolbar.setNavigationIconTint(invertedColor)

                            this@ViewImageActivity.window.navigationBarColor = color

                        } catch (e: NullPointerException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val ei = ExifInterface(imagePath)

                    val orientation = ei.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED
                    )

                    val m = Matrix()
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> {
                            m.postRotate(90f)
                        }
                        ExifInterface.ORIENTATION_ROTATE_180 -> {
                            m.postRotate(180f)
                        }
                        ExifInterface.ORIENTATION_ROTATE_270 -> {
                            m.postRotate(270f)
                        }
                    }
                    val bitmap =
                        Bitmap.createBitmap(
                            bitmapSrc,
                            0,
                            0,
                            bitmapSrc.width,
                            bitmapSrc.height,
                            m,
                            true
                        )

                    imageView.setImageBitmap(bitmap)

                    Palette.Builder(bitmap).generate { palette ->
                        val vSwatch = palette?.dominantSwatch?.rgb
                        val color =
                            Color.rgb(
                                vSwatch!!.red,
                                vSwatch.green,
                                vSwatch.blue
                            )

                        val invertedColor = Color.rgb(
                            255 - vSwatch.red, 255 - vSwatch.green,
                            255 - vSwatch.blue
                        )
                        try {
                            val imageViewConstraint =
                                findViewById<ConstraintLayout>(R.id.imageViewConstraint)
                            imageViewConstraint.setBackgroundColor(color)
                            viewImageMaterialToolbar.setNavigationIconTint(invertedColor)

                            this@ViewImageActivity.window.navigationBarColor = color

                        } catch (e: NullPointerException) {
                            e.printStackTrace()
                        }
                    }
                }
                else {
                    val bitmap = Bitmap.createBitmap(
                        bitmapSrc,
                        0,
                        0,
                        bitmapSrc.width,
                        bitmapSrc.height,
                        Matrix(),
                        true
                    )

                    imageView.setImageBitmap(bitmap)

                    Palette.Builder(bitmap).generate { palette ->
                        val vSwatch = palette?.dominantSwatch?.rgb
                        val color =
                            Color.rgb(
                                vSwatch!!.red,
                                vSwatch.green,
                                vSwatch.blue
                            )

                        val invertedColor = Color.rgb(
                            255 - vSwatch.red, 255 - vSwatch.green,
                            255 - vSwatch.blue
                        )
                        try {
                            val imageViewConstraint =
                                findViewById<ConstraintLayout>(R.id.imageViewConstraint)
                            imageViewConstraint.setBackgroundColor(color)
                            viewImageMaterialToolbar.setNavigationIconTint(invertedColor)

                            this@ViewImageActivity.window.navigationBarColor = color

                        } catch (e: NullPointerException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

            /*Glide.with(this)
                .load(imageMap["image"].toString())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Toast.makeText(this@ViewImageActivity, "Error loading image", Toast.LENGTH_SHORT)
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
                                val imageViewConstraint = findViewById<ConstraintLayout>(R.id.imageViewConstraint)
                                imageViewConstraint.setBackgroundColor(color)
                                this@ViewImageActivity.window.statusBarColor = color

                            } catch (e: NullPointerException) {
                                e.printStackTrace()
                            }
                        }

                        return false
                    }

                })
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontTransform()
                .into(imageView)*/
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finishAfterTransition()
    }
}