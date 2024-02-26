package com.example.gomoku.ui.credits

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.core.net.toUri
import com.example.gomoku.R
import com.example.gomoku.TAG
import com.example.gomoku.domain.Developer
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Information
import com.example.gomoku.domain.Loaded
import com.example.gomoku.domain.Social
import com.example.gomoku.domain.getOrNull
import com.example.gomoku.domain.idle
import com.example.gomoku.ui.common.Loading
import com.example.gomoku.ui.common.loadScreenSizes
import com.example.gomoku.ui.common.theme.GomokuTheme
import com.example.gomoku.ui.lobby.BottomBarView
import com.example.gomoku.ui.lobby.SelectedActivity
import com.example.gomoku.ui.replay.ScreenSizes
import kotlin.math.min

/**
 * Root composable for the about screen, the one that displays information about the app.
 */
@Composable
fun CreditsScreen(
    onOpenUrlRequested: (Uri) -> Unit = { },
    onSendEmailRequested: (String) -> Unit = { },
    info: IOState<Information> = idle(),
    onRankingRequest: () -> Unit,
    onLobbyRequest: () -> Unit,
    onSavedGamesRequest: () -> Unit,
    onProfileRequest: () -> Unit,
) {
    GomokuTheme {
        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.background))
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            when (info) {
                is Loaded -> {

                    val screenSizes = loadScreenSizes()
                    // Developer info
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenSizes.height * 0.79f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        info.getOrNull()?.let {
                            for (dev in it.developers) {
                                DeveloperInfo(
                                    dev = dev,
                                    screenSizes.copy(height = screenSizes.height * 0.79f * 0.3f),
                                    onOpenUrlRequested = onOpenUrlRequested,
                                    onSendEmailRequested = onSendEmailRequested
                                )

                            }
                        }
                    }


                    BottomBarView(
                        onRankingRequest = onRankingRequest,
                        onSavedGamesRequest = onSavedGamesRequest,
                        onMiddleButtonRequest = onLobbyRequest,
                        onCreditsRequest = {},
                        onProfileRequest = onProfileRequest,
                        middleIcon = R.drawable.lobby_button_1,
                        middleDesc = "Lobby",
                        selectedActivity = SelectedActivity.CREDITS,
                        screenSizes = screenSizes.copy(height = screenSizes.height * 0.15f)
                    )
                }

                else -> {
                    Loading()
                }
            }
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun DeveloperInfo(
    dev: Developer,
    screenSizes: ScreenSizes,
    onOpenUrlRequested: (Uri) -> Unit = {},
    onSendEmailRequested: (String) -> Unit
) {
    GomokuTheme {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(screenSizes.height),
        ) {
            val context = LocalContext.current
            val drawableId = remember(dev.image) {
                context.resources.getIdentifier(
                    dev.image,
                    "drawable",
                    context.packageName
                )
            }
            Image(
                painter = painterResource(id = drawableId),
                contentDescription = null,
                modifier = Modifier
                    .width(screenSizes.width * 0.3f)
                    .height(screenSizes.height * 0.65f)

            )
            Column(
                modifier = Modifier
                    .height(screenSizes.height * 0.6f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Name
                Text(
                    text = dev.name.removeSurrounding("'", "'"),
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.karasha)),
                        color = Color.White
                    )
                )
                // Number
                Text(
                    text = dev.number,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.genjyuu)),
                        color = Color.White
                    )
                )
                // Social Networks
                Socials(
                    onOpenUrlRequested = onOpenUrlRequested,
                    onSendEmailRequested = onSendEmailRequested,
                    screenSizes = screenSizes.copy(height = screenSizes.height * 0.6f),
                    socials = dev.socials,
                    email = dev.email
                )

            }

        }
    }
}

@Composable
private fun Socials(
    onOpenUrlRequested: (Uri) -> Unit = { },
    onSendEmailRequested: (String) -> Unit,
    screenSizes: ScreenSizes,
    socials: List<Social>,
    email: String
) {
    val countPerRow = 3
    repeat(socials.size / countPerRow + 1) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = screenSizes.width * 0.05f)
        ) {
            val start = it * countPerRow
            val end = min(a = (it + 1) * countPerRow, b = socials.size)
            socials.subList(fromIndex = start, toIndex = end).forEach {

                Social(
                    imageSrc = it.image,
                    screenSizes = screenSizes.copy(width = screenSizes.width * 0.15f),
                    onClick = { onOpenUrlRequested(it.uri.toUri()) }
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_email),
                contentDescription = null,
                modifier = Modifier
                    .size(screenSizes.width * 0.15f)
                    .clickable { onSendEmailRequested(email) }
            )

        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
private fun Social(imageSrc: String, screenSizes: ScreenSizes, onClick: () -> Unit) {
    val context = LocalContext.current
    val drawableId = remember(imageSrc) {
        Log.v(TAG, "Fetching image: $imageSrc")
        context.resources.getIdentifier(
            imageSrc,
            "drawable",
            context.packageName
        )
    }
    Image(
        painter = painterResource(id = drawableId),
        contentDescription = null,
        modifier = Modifier
            .size(screenSizes.width)
            .clickable { onClick() }
    )
}
