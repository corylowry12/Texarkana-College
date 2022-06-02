package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class CurrentPhotoPathData(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    fun setPhotoPath(state: String) {
        val editor = sharedPreferences.edit()
        editor.putString("photoPath", state)
        editor.apply()
    }

    fun loadPhotoPath(): String {
        val state = sharedPreferences.getString("photoPath", "")
        return (state!!)
    }
}