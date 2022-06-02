package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class ImagePathData(context: Context) {

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences("file", Context.MODE_PRIVATE)

    fun setPath(state: String) {
        val editor = sharedPreferences.edit()
        editor.putString("path", state)
        editor.apply()
    }

    fun loadPath(): String {
        val state = sharedPreferences.getString("path", "")
        return (state!!)
    }
}