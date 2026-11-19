package com.Cabmeri3.Nasxil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.Cabmeri3.Nasxil.core.di.ServiceLocator
import com.Cabmeri3.Nasxil.core.theme.AppTheme
import com.Cabmeri3.Nasxil.presentation.app.AppRoot

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        ServiceLocator.init(applicationContext)
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AppRoot()
            }
        }
    }
}
