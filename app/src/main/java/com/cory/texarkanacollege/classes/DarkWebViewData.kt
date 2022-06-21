package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class DarkWebViewData(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    //this saves the break preference
    fun setDarkWebView(state: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("darkWebView", state)
        editor.apply()
    }

    // this will load break state
    fun loadDarkWebView(): Boolean {
        val state = sharedPreferences.getBoolean("darkWebView", false)
        return (state)
    }

}