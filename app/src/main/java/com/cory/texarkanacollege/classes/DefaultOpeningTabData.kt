package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class DefaultOpeningTabData(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    fun setDefaultTab(state: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("default_opening_tab", state)
        editor.apply()
    }

    fun loadDefaultTab(): Int {
        val state = sharedPreferences.getInt("default_opening_tab", 0)
        return (state)
    }
}