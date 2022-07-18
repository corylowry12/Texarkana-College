package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class ColoredClassGradeTextView(context: Context) {
    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences("file", Context.MODE_PRIVATE)

    //this saves the break preference
    fun setColoredClassTextView(state: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("coloredClassAverageTextView", state)
        editor.apply()
    }

    // this will load break state
    fun loadColoredClassTextView(): Int {
        val state = sharedPreferences.getInt("coloredClassAverageTextView", 0)
        return (state)
    }
}