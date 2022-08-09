package com.baec23.upcycler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.baec23.upcycler.ui.LoginScreen
import com.baec23.upcycler.ui.theme.UpcyclerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            UpcyclerTheme {
                val bannerPainter = painterResource(id = R.drawable.upcycling_banner2)
                LoginScreen(bannerPainter = bannerPainter, bannerContentDescription = "Upcycler Banner")
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    UpcyclerTheme {
        Greeting("Android")
    }
}