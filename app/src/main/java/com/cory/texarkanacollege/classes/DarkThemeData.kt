package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import com.cory.texarkanacollege.R

class DarkThemeData(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    //this saves the break preference
    fun setState(state: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("darkThemeData", state)
        editor.apply()
    }

    // this will load break state
    fun loadState(): Int {
        val state = sharedPreferences.getInt("darkThemeData", 2)
        return (state)
    }

    fun dateDialogTheme(context: Context) : Int {
        if (loadState() == 0) {
            return R.style.datePickerLight
        }
        else if (loadState() == 1) {
            return R.style.datePickerDark
        }
        else if (loadState() == 2) {
            when (context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    return R.style.datePickerLight
                }
                Configuration.UI_MODE_NIGHT_YES -> {
                    return R.style.datePickerDark
                }
            }
        }
        return R.style.datePickerLight
    }
}