package com.baec23.upcycler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.baec23.upcycler.ui.app.AppEvent
import com.baec23.upcycler.ui.app.AppScreen
import com.baec23.upcycler.ui.theme.UpcyclerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val launcherViewModel: LauncherViewModel by viewModels()

    @Inject
    lateinit var appEventChannel: Channel<AppEvent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !launcherViewModel.isLoaded.value
            }
        }

        setContent {
            UpcyclerTheme {
                AppScreen(eventChannel = appEventChannel)
            }
        }
    }
}