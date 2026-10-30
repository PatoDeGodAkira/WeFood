package com.wefood.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.wefood.app.ui.WeFoodApp
import com.wefood.app.ui.WeFoodTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeFoodTheme {
                WeFoodApp()
            }
        }
    }
}
