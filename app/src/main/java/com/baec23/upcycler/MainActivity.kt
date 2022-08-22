package com.baec23.upcycler

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.baec23.upcycler.ui.app.AppEvent
import com.baec23.upcycler.ui.app.AppScreen
import com.baec23.upcycler.ui.theme.UpcyclerTheme
import com.baec23.upcycler.util.TAG
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
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
                val navHostController = rememberNavController()
                AppScreen(eventChannel = appEventChannel)
            }
        }
    }
}