package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class Version(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    fun setVersion(state: String) {
        val editor = sharedPreferences.edit()
        editor.putString("version", state)
        editor.apply()
    }

    fun loadVersion(): String {
        val state = sharedPreferences.getString("version", "")
        return (state!!)
    }
}