package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class CommentLikeCounter(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    fun setCounterVisibility(state: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("comment_like_counter_visible", state)
        editor.apply()
    }

    fun loadCounterVisibility(): Boolean {
        val state = sharedPreferences.getBoolean("comment_like_counter_visible", false)
        return (state)
    }
}