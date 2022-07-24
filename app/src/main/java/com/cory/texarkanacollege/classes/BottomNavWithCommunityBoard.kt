package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class BottomNavWithCommunityBoard(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    //this saves the break preference
    fun setState(state: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("BottomNavWithCommunityBoard", state)
        editor.apply()
    }

    // this will load break state
    fun loadState(): Boolean {
        val state = sharedPreferences.getBoolean("BottomNavWithCommunityBoard", false)
        return (state)
    }
}