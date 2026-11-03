package com.machivictordev.blackbook.ui.util

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun SetStatusBarIcons(darkIcons: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        DisposableEffect(darkIcons) {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                val previousState = insetsController.isAppearanceLightStatusBars
                insetsController.isAppearanceLightStatusBars = darkIcons
                onDispose {
                    insetsController.isAppearanceLightStatusBars = previousState
                }
            } else {
                onDispose { }
            }
        }
    }
}
