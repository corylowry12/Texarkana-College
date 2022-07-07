package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class RememberRecyclerViewVisibilityForAssignments(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    //this saves the break preference
    fun setState(state: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("RememberRecyclerViewVisibility", state)
        editor.apply()
    }

    // this will load break state
    fun loadState(): Boolean {
        val state = sharedPreferences.getBoolean("RememberRecyclerViewVisibility", false)
        return (state)
    }
}