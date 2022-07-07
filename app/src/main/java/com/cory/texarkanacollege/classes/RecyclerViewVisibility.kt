package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class RecyclerViewVisibility(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    //this saves the break preference
    fun setUpcoming(state: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("UpcomingVisibility", state)
        editor.apply()
    }

    // this will load break state
    fun loadUpcoming(): Boolean {
        val state = sharedPreferences.getBoolean("UpcomingVisibility", false)
        return (state)
    }

    //this saves the break preference
    fun setPastDue(state: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("PastDueVisibility", state)
        editor.apply()
    }

    // this will load break state
    fun loadPastDue(): Boolean {
        val state = sharedPreferences.getBoolean("PastDueVisibility", false)
        return (state)
    }

    //this saves the break preference
    fun setDone(state: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("DoneVisibility", state)
        editor.apply()
    }

    // this will load break state
    fun loadDone(): Boolean {
        val state = sharedPreferences.getBoolean("DoneVisibility", false)
        return (state)
    }
}