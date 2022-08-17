package com.baec23.upcycler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.baec23.upcycler.ui.AppScreen
import com.baec23.upcycler.ui.AppEvent
import com.baec23.upcycler.ui.theme.UpcyclerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var appEventChannel: Channel<AppEvent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UpcyclerTheme {
                AppScreen(eventChannel = appEventChannel)
            }
        }
    }
}


