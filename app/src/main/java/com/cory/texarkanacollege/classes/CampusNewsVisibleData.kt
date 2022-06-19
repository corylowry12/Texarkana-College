package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class CampusNewsVisibleData(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    fun setCampusNewsVisible(state: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("campus_news_visible", state)
        editor.apply()
    }

    fun loadCampusNewsVisible(): Boolean {
        val state = sharedPreferences.getBoolean("campus_news_visible", true)
        return (state)
    }
}