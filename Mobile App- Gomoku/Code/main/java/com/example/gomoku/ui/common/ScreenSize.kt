package com.example.gomoku.ui.common

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.gomoku.ui.replay.ScreenSizes


@Composable
fun loadScreenSizes(): ScreenSizes {

    val screenSize = LocalDensity.current.run {
        ScreenSizes(
            width = LocalConfiguration.current.screenWidthDp.dp,
            height = LocalConfiguration.current.screenHeightDp.dp
        )
    }
    Log.v("LoadScreenSizes", "size: $screenSize")
    return screenSize
}