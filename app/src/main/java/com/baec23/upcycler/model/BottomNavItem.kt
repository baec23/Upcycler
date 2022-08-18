package com.baec23.upcycler.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.baec23.upcycler.navigation.Screen

data class BottomNavItem(
    val name: String,
    val screen: Screen,
    val icon: ImageVector,
)
