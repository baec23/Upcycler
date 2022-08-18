package com.baec23.upcycler.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object DateConverter {
    @SuppressLint("SimpleDateFormat")
    fun convertTimestampToDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
        val date = Date(timestamp)
        return sdf.format(date)
    }
}