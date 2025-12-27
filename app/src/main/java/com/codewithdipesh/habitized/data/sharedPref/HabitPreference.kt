package com.codewithdipesh.habitized.data.sharedPref

import android.content.Context
import com.codewithdipesh.habitized.presentation.timerscreen.Theme
import androidx.core.content.edit

class HabitPreference(context : Context){
    private val sharedPreferences = context.getSharedPreferences("habit_preferences", Context.MODE_PRIVATE)
    private val THEME_KEY = "THEME"
    private val INTRO_KEY = "INTRO"
    private val LAST_BACKUP_DATE_KEY = "LAST_BACKUP_DATE"
    private val AUTO_BACKUP_ENABLED_KEY = "AUTO_BACKUP_ENABLED"

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

    // Backup related preferences
    fun getLastBackupDate(): String? {
        return sharedPreferences.getString(LAST_BACKUP_DATE_KEY, null)
    }

    fun updateLastBackupDate(date: String) {
        sharedPreferences.edit() { putString(LAST_BACKUP_DATE_KEY, date) }
    }

    fun isAutoBackupEnabled(): Boolean {
        return sharedPreferences.getBoolean(AUTO_BACKUP_ENABLED_KEY, false)
    }

    fun setAutoBackupEnabled(enabled: Boolean) {
        sharedPreferences.edit() { putBoolean(AUTO_BACKUP_ENABLED_KEY, enabled) }
    }
}