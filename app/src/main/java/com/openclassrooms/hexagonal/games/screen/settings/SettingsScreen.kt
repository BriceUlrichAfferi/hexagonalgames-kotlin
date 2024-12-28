package com.openclassrooms.hexagonal.games.screen.settings

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.openclassrooms.hexagonal.games.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  modifier: Modifier = Modifier,
  viewModel: SettingsViewModel = hiltViewModel(),
  onBackClick: () -> Unit
) {
  var notificationsEnabled by remember { mutableStateOf(viewModel.areNotificationsEnabled()) }

  Scaffold(
    modifier = modifier,
    topBar = {
      TopAppBar(
        title = { Text(stringResource(id = R.string.action_settings)) },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(id = R.string.contentDescription_go_back)
            )
          }
        }
      )
    }
  ) { contentPadding ->
    Settings(
      modifier = Modifier.padding(contentPadding),
      notificationsEnabled = notificationsEnabled,
      onNotificationDisabledClicked = {
        viewModel.disableNotifications()
        notificationsEnabled = viewModel.areNotificationsEnabled()
      },
      onNotificationEnabledClicked = {
        viewModel.enableNotifications()
        notificationsEnabled = viewModel.areNotificationsEnabled()
      }
    )
  }
}






@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun Settings(
  modifier: Modifier = Modifier,
  notificationsEnabled: Boolean,
  onNotificationEnabledClicked: () -> Unit,
  onNotificationDisabledClicked: () -> Unit
) {
  val notificationsPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)
  } else {
    null
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceEvenly
  ) {
    Icon(
      modifier = Modifier.size(200.dp),
      painter = painterResource(id = R.drawable.ic_notifications),
      tint = MaterialTheme.colorScheme.onSurface,
      contentDescription = stringResource(id = R.string.contentDescription_notification_icon)
    )
    Text(
      text = if (notificationsEnabled) {
        stringResource(id = R.string.notifications_enabled)
      } else {
        stringResource(id = R.string.notifications_disabled)
      },
      style = MaterialTheme.typography.bodyLarge
    )

    Button(
      onClick = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          notificationsPermissionState?.launchPermissionRequest()
        }
        onNotificationEnabledClicked()
      },
      enabled = !notificationsEnabled
    ) {
      Text(text = stringResource(id = R.string.notification_enable))
    }

    Button(
      onClick = onNotificationDisabledClicked,
      enabled = notificationsEnabled
    ) {
      Text(text = stringResource(id = R.string.notification_disable))
    }
  }
}
