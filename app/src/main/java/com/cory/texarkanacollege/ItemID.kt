package com.cory.texarkanacollege

import android.content.Context
import android.content.SharedPreferences

class ItemID(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    fun setPosition(state: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("position", state)
        editor.apply()
    }

    fun loadPosition(): Int {
        val state = sharedPreferences.getInt("position", -1)
        return (state)
    }
}