package com.openclassrooms.hexagonal.games.screen.settings

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
  private val sharedPreferences: SharedPreferences
) : ViewModel() {

  fun enableNotifications() {
    sharedPreferences.edit().putBoolean("notifications_enabled", true).apply()
  }

  fun disableNotifications() {
    sharedPreferences.edit().putBoolean("notifications_enabled", false).apply()
  }

  fun areNotificationsEnabled(): Boolean {
    return sharedPreferences.getBoolean("notifications_enabled", false)
  }
}
