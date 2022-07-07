package com.cory.texarkanacollege

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.cory.texarkanacollege.classes.DarkThemeData
import com.cory.texarkanacollege.database.GradesDBHelper
import com.google.android.material.appbar.MaterialToolbar
import com.ortiz.touchview.TouchImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ViewImageActivity : AppCompatActivity() {
    @SuppressLint("Range")
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
        setContentView(R.layout.activity_view_image)

        val viewImageMaterialToolbar = findViewById<MaterialToolbar>(R.id.viewImageToolBar)
        viewImageMaterialToolbar.setNavigationOnClickListener {
            finish()
        }

        val args = intent.getIntExtra("image", 0)
        val id = intent.getIntExtra("id", 0)

        val dbHandler = GradesDBHelper(this, null)

        val cursor = dbHandler.getImage(args.toString(), id.toString())
        cursor.moveToFirst()

        val imageMap = HashMap<String, String>()

        imageMap["image"] =
            cursor.getString(cursor.getColumnIndex(GradesDBHelper.COLUMN_IMAGE))

        val imageView = findViewById<TouchImageView>(R.id.imageView)

        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        GlobalScope.launch(Dispatchers.Main) {
            Glide.with(this@ViewImageActivity)
                .load(imageMap["image"].toString())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(circularProgressDrawable)
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
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        finish()
    }
}