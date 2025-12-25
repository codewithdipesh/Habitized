package com.codewithdipesh.habitized.data.sharedPref

import android.content.Context
import com.codewithdipesh.habitized.presentation.timerscreen.Theme
import androidx.core.content.edit

class HabitPreference(context : Context){
    private val sharedPreferences = context.getSharedPreferences("habit_preferences", Context.MODE_PRIVATE)
    private val THEME_KEY = "THEME"
    private val INTRO_KEY = "INTRO"

    fun getTheme(default : String = Theme.Normal.displayName) : String {
        val theme = sharedPreferences.getString(THEME_KEY,default)
        return if(theme != null){
             theme
        }else {
             default
        }
    }

    fun updateTheme(theme:String){
        sharedPreferences.edit() { putString(THEME_KEY, theme) }
    }

    fun getIntro(default : Boolean = true) : Boolean {
        return sharedPreferences.getBoolean(INTRO_KEY,default)
    }

    fun updateIntro(intro:Boolean){
        sharedPreferences.edit() { putBoolean(INTRO_KEY, intro) }
    }
}