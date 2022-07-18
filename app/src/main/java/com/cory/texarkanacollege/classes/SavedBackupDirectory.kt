package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class SavedBackupDirectory(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    //this saves the break preference
    fun setBackupDirectory(state: String) {
        val editor = sharedPreferences.edit()
        editor.putString("backupDirectory", state)
        editor.apply()
    }

    // this will load break state
    fun loadBackupDirectory(): String {
        val state = sharedPreferences.getString("backupDirectory", "")
        return (state!!)
    }
}