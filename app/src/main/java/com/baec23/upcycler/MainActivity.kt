package com.baec23.upcycler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.baec23.upcycler.ui.App
import com.baec23.upcycler.ui.theme.UpcyclerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UpcyclerTheme {
                App()
            }
        }
    }
}


