package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class PinnedSwitchVisible(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    fun setPinnedSwitchVisible(state: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("pinned_switch_visible", state)
        editor.apply()
    }

    fun loadPinnedSwitchVisible(): Boolean {
        val state = sharedPreferences.getBoolean("pinned_switch_visible", false)
        return (state)
    }
}