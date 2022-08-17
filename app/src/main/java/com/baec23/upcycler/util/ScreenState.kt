package com.baec23.upcycler.util

sealed class ScreenState {
    object Ready : ScreenState()
    object Busy : ScreenState()
}