package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class DefaultCategoryData(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    fun setDefaultCategory(state: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("default_category", state)
        editor.apply()
    }

    fun loadDefaultCategory(): Int {
        val state = sharedPreferences.getInt("default_category", 2)
        return (state)
    }
}