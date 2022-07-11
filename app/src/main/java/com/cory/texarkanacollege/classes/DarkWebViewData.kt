package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences
import android.os.Build

class DarkWebViewData(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    //this saves the break preference
    fun setDarkWebView(state: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("darkWebView_data", state)
        editor.apply()
    }

    // this will load break state
    fun loadDarkWebView(): Boolean {
        var state = sharedPreferences.getBoolean("darkWebView_data", false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            state = sharedPreferences.getBoolean("darkWebView_data", true)
        }
        return (state)
    }

}