package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class ClassIcons(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    //this saves the break preference
    fun setClassIcons(state: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("classIcons", state)
        editor.apply()
    }

    // this will load break state
    fun loadClassIcons(): Boolean {
        val state = sharedPreferences.getBoolean("classIcons", true)
        return (state)
    }
}