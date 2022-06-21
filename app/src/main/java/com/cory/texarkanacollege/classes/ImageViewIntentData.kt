package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class ImageViewIntentData(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    //this saves the break preference
    fun setImageView(state: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("imageViewIntent", state)
        editor.apply()
    }

    // this will load break state
    fun loadImageView(): Boolean {
        val state = sharedPreferences.getBoolean("imageViewIntent", false)
        return (state)
    }

}