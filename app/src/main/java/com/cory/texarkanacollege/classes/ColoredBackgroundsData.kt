package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class ColoredBackgroundsData(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    //this saves the break preference
    fun setColoredBackgrounds(state: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("coloredBackgrounds", state)
        editor.apply()
    }

    // this will load break state
    fun loadColoredBackgrounds(): Boolean {
        val state = sharedPreferences.getBoolean("coloredBackgrounds", true)
        return (state)
    }
}