package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class GradesIcons(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    //this saves the break preference
    fun setGradeIcons(state: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("gradesIcons", state)
        editor.apply()
    }

    // this will load break state
    fun loadGradeIcons(): Boolean {
        val state = sharedPreferences.getBoolean("gradesIcons", true)
        return (state)
    }
}