package com.cory.texarkanacollege.classes

import android.content.Context
import android.content.SharedPreferences

class CommunityBoardVisibileData(context: Context) {

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("file", Context.MODE_PRIVATE)

    fun setCommunityBoardVisible(state: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("community_board_visible", state)
        editor.apply()
    }

    fun loadCommunityBoardVisible(): Boolean {
        val state = sharedPreferences.getBoolean("community_board_visible", false)
        return (state)
    }
}