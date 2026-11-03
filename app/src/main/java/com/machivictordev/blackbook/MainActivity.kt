package com.machivictordev.blackbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.machivictordev.blackbook.ui.theme.BlackBookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as BlackBookApplication).container
        setContent {
            BlackBookTheme {
                BlackBookNavHost(container)
            }
        }
    }
}
