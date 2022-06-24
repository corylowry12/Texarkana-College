package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class CategoryTextViewVisible(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    //this saves the break preference
    fun setCategoryTextView(state: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("categoryTextView", state)
        editor.apply()
    }

    // this will load break state
    fun loadCategoryTextView(): Boolean {
        val state = sharedPreferences.getBoolean("categoryTextView", true)
        return (state)
    }

}