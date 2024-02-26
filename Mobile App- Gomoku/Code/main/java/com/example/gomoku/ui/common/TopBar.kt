package com.example.gomoku.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.gomoku.R
import com.example.gomoku.ui.common.theme.GomokuTheme

data class NavigationHandlers(
    val onBackRequested: (() -> Unit)? = null,
    val onInfoRequested: (() -> Unit)? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navigation: NavigationHandlers = NavigationHandlers(), title: String) {
    TopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = colorResource(id = R.color.main_color)),
        title = { Text(text = title) },
        navigationIcon = {
            if (navigation.onBackRequested != null) {
                IconButton(
                    onClick = navigation.onBackRequested,
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = stringResource(id = R.string.top_bar_go_back)
                    )
                }
            }
        },
        actions = {
            if (navigation.onInfoRequested != null) {
                IconButton(
                    onClick = navigation.onInfoRequested
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = stringResource(id = R.string.top_bar_navigate_to_credits)
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun TopBarPreviewInfoAndBack() {
    GomokuTheme {
        TopBar(
            navigation = NavigationHandlers(
                onBackRequested = { },
                onInfoRequested = {}
            ),
            title = stringResource(id = R.string.app_name)
        )
    }
}