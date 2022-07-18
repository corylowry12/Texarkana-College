package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class GradesColoredTextView(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    //this saves the break preference
    fun setGradeColoredTextView(state: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("gradesColoredTextView", state)
        editor.apply()
    }

    // this will load break state
    fun loadGradeColoredTextView(): Boolean {
        val state = sharedPreferences.getBoolean("gradesColoredTextView", true)
        return (state)
    }
}