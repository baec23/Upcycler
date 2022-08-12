package com.baec23.upcycler.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

object AppEventChannel {

    val eventChannel = MutableSharedFlow<AppEvent>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun fireEvent(event: AppEvent) {
        coroutineScope.launch {
            eventChannel.emit(event)
        }
    }
}