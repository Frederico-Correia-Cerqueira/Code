
package com.example.gomoku.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gomoku.R
import com.example.gomoku.ui.common.loadScreenSizes
import com.example.gomoku.ui.common.theme.GomokuTheme

/**
 * Root composable for the main screen, the one that displays the initial screen to be presented to the user.
 */
@Composable
fun HomeScreen(
    onGetStarted: () -> Unit
) {
    val screenSizes = loadScreenSizes()
    GomokuTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.home),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize()
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = screenSizes.height * 0.3f),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.karasha)),
                    color = Color.White,
                    fontSize = 30.sp
                )
            )
            // Button - Get Started!
            Button(
                onClick = { onGetStarted() },
                modifier = Modifier
                    .size(screenSizes.width * 0.3f)
                    .clip(CircleShape)
            ) {

                    Text(
                        "Get started",
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.karasha)),
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    )

            }
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    HomeScreen(onGetStarted = { })
}