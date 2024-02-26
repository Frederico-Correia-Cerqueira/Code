package com.example.gomoku.ui.lobby

import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gomoku.R
import com.example.gomoku.domain.IOState
import com.example.gomoku.domain.Loaded
import com.example.gomoku.ui.common.loadScreenSizes
import com.example.gomoku.domain.Rank
import com.example.gomoku.domain.Stats
import com.example.gomoku.tags.LobbyTags
import com.example.gomoku.ui.common.DefinitionsPopUp
import com.example.gomoku.ui.common.Loading
import com.example.gomoku.ui.replay.ScreenSizes

@Composable
fun LobbyScreen(
    onDefinitionsRequest: () -> Unit,
    onRankingRequest: () -> Unit,
    onSavedGamesRequest: () -> Unit,
    onPlayRequest: () -> Unit,
    onCreditsRequest: () -> Unit,
    onProfileRequest: () -> Unit,
    onLogoutRequest: () -> Unit,
    enableToLogout: Boolean,
    onGameSelect: (Options) -> Unit,
    onGameDetails: (Options) -> Unit,
    playerStats: IOState<Stats>,
    selected: Options,
    definitionPopup: Boolean,
    displayDetails: String?
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(colorResource(id = R.color.background))
    ) {
        when (playerStats) {
            is Loaded -> {
                Log.v("LobbyScreen", "LoadState: ${playerStats.value}")
                val statsResult: Stats =
                    playerStats.value.getOrNull() ?: error("Stats is null")
                Log.v("LobbyScreen", "StatsResult: $statsResult")
                val screenSizes = loadScreenSizes()
                TopBarView(
                    playerName = statsResult.player,
                    screenSizes = screenSizes.copy(height = screenSizes.height * 0.1f),
                ) { onDefinitionsRequest() }

                MiddleBarView(
                    onGameSelect = onGameSelect,
                    onGameDetails = onGameDetails,
                    selected = { selected },
                    displayDetails = displayDetails,
                    playerRank = statsResult.rank,
                    playerElo = statsResult.elo,
                    screenSizes = screenSizes.copy(height = screenSizes.height * 0.69f)
                )

                BottomBarView(
                    onRankingRequest = { onRankingRequest() },
                    onSavedGamesRequest = { onSavedGamesRequest() },
                    onMiddleButtonRequest = { onPlayRequest() },
                    onCreditsRequest = { onCreditsRequest() },
                    onProfileRequest = { onProfileRequest() },
                    middleIcon = R.drawable.play_button_1,
                    middleDesc = "Play",
                    selectedActivity = SelectedActivity.LOBBY,
                    screenSizes = screenSizes.copy(height = screenSizes.height * 0.15f)
                )
                if(definitionPopup)
                    DefinitionsPopUp(
                        screenSizes = screenSizes,
                        enableToLogout = enableToLogout,
                        onBackRequest = { onDefinitionsRequest() },
                        onLogoutRequest = { onLogoutRequest() },
                    )
            }
            else -> {
                Loading()
            }
        }
    }
}

enum class SelectedActivity { RANKING, SAVED_GAMES, LOBBY, CREDITS, PROFILE}

@Composable
fun NavigationButton(
    image: Int,
    desc: String,
    size: Dp,
    selected: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    val color = if (selected) colorResource(id = R.color.background) else Color.Transparent
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(size)
            .clip(RoundedCornerShape(bottomStart = size * 0.25f, bottomEnd = size * 0.25f))
            .background(color)
            .testTag(tag),
        contentAlignment = Center
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = desc,
            modifier = Modifier
                .size(size)
                .clickable { onClick() }
        )
    }
}

@Composable
fun TopBarView(
    playerName: String,
    screenSizes: ScreenSizes,
    onDefinitionsRequest: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenSizes.height)
            .padding(screenSizes.width * 0.05f),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        /**
         * On Left side of the top bar we have the definitions button
         * On Middle of top bar we have the player name
         * On Right side of the top bar we have the player coins (icon coins and number of coins)
         */
        NavigationButton(
            image = R.drawable.settings_button_1,
            desc = "Settings",
            selected = false,
            size = screenSizes.height * 0.5f,
            tag = LobbyTags.SETTINGS_REQUEST_TAG
        ) { onDefinitionsRequest() }

        Text(
            text = playerName,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.karasha)),
                color = Color.White
            ),
            fontSize = 20.sp,
            color = Color.White
        )
    }
}

@Composable
fun BottomBarView(
    onRankingRequest: () -> Unit,
    onSavedGamesRequest: () -> Unit,
    onMiddleButtonRequest: () -> Unit,
    onCreditsRequest: () -> Unit,
    onProfileRequest: () -> Unit,
    middleIcon: Int,
    middleDesc: String,
    selectedActivity: SelectedActivity,
    screenSizes: ScreenSizes
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.bottombar_background))
            .height(screenSizes.height)
            .padding(horizontal = screenSizes.height * 0.03f),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        NavigationButton(
            image = R.drawable.ranking_button_1,
            desc = "Ranking",
            selected = selectedActivity == SelectedActivity.RANKING,
            size = screenSizes.height * 0.5f,
            tag = LobbyTags.RANKING_REQUEST_TAG
        ) { onRankingRequest() }

        NavigationButton(
            image = R.drawable.save_buttom_1,
            desc = "SavedGames",
            selected = selectedActivity == SelectedActivity.SAVED_GAMES,
            size = screenSizes.height * 0.5f,
            tag = LobbyTags.SAVED_GAMES_REQUEST_TAG
        ) { onSavedGamesRequest() }

        NavigationButton(
            image = middleIcon,
            desc = middleDesc,
            size = screenSizes.height * 0.7f,
            selected = false,
            tag = LobbyTags.PLAY_REQUEST_TAG
        ) { onMiddleButtonRequest() }

        NavigationButton(
            image = R.drawable.credits_button_1,
            desc = "Credits",
            selected = selectedActivity == SelectedActivity.CREDITS,
            size = screenSizes.height * 0.5f,
            tag = LobbyTags.CREDITS_REQUEST_TAG
        ) { onCreditsRequest() }

        NavigationButton(
            image = R.drawable.profile_button_1,
            desc = "PlayerProfile",
            selected = selectedActivity == SelectedActivity.PROFILE,
            size = screenSizes.height * 0.5f,
            tag = LobbyTags.PROFILE_REQUEST_TAG
        ) { onProfileRequest() }
    }

}

@Composable
fun BoxGame(option: Options, tag: String, detailTag: String, selected: Boolean, screenSizes: ScreenSizes, onClickBox: () -> Unit, onClickDetails: () -> Unit) {

    val animatedProgress by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 16.sp.value,
        targetValue = 20.sp.value,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = Modifier
            .width(screenSizes.width)
            .height(screenSizes.width)
            .background(Color.Transparent)
    ) {

        // Image Background
        Image(
            painter = painterResource(id = R.drawable.box),
            contentDescription = "BoxGame",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(screenSizes.width * 0.25f))
                .clickable { onClickBox() }
                .testTag(tag)
        )

        // Image Check Details
        Image(
            painter = painterResource(id = R.drawable.check_details),
            contentDescription = "DetailGame",
            modifier = Modifier
                .padding(start = screenSizes.width * 0.06f, top = screenSizes.width * 0.08f)
                .size(screenSizes.width * 0.2f)
                .clip(CircleShape)
                .clickable { onClickDetails() }
                .testTag(detailTag)
        )

        Text(
            text = option.toString(),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize =  if(selected) animatedProgress.sp else 17.sp,
                fontFamily = FontFamily(Font(R.font.karasha)),
                color = Color.White
            ),
            modifier = Modifier
                .padding(end = screenSizes.width * 0.06f)
                .align(Center)

        )

    }
}

@Composable
fun MiddleBarView(
    onGameSelect: (Options) -> Unit,
    onGameDetails: (Options) -> Unit,
    selected: () -> Options,
    displayDetails: String?,
    playerRank: Rank,
    playerElo: Double,
    screenSizes: ScreenSizes
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenSizes.height)
            .background(colorResource(id = R.color.background))
    ) {
        Image(
            painter = painterResource(id = R.drawable.tree_1),
            contentDescription = "BackgroundTree",
            modifier = Modifier.fillMaxSize(),
            alignment = BottomCenter
        )
        DisplayRank(
            playerRank = playerRank,
            playerElo = playerElo,
            padding = screenSizes.height * 0.05f,
            screenSizes = screenSizes.copy(height = screenSizes.height * 0.4f)
        )
        DisplayGameDescription(displayDetails =displayDetails, screenSizes = screenSizes)
        GameOptions(onClickBox = onGameSelect, onClickDetails = onGameDetails, selected = selected, screenSizes = screenSizes.copy(height = screenSizes.height * 0.25f))
    }
}

@Composable
fun DisplayGameDescription(displayDetails: String?, screenSizes: ScreenSizes) {
    if (displayDetails != null) {
        Row(
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(top = screenSizes.height * 0.85f)
                .fillMaxWidth()
                .height(screenSizes.height * 0.15f)
                .background(colorResource(id = R.color.box_background))
                .border(
                    width = 5.dp,
                    color = colorResource(id = R.color.bottombar_background)
                )
                .testTag(LobbyTags.GAME_DETAIL_TAG)
        ) {
            LazyColumn {
                item {
                    Text(
                        text = displayDetails,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.karasha)),
                            color = Color.White
                        ),
                        modifier = Modifier.padding(horizontal = screenSizes.width * 0.02f)
                    )
                }
            }

        }
    }
}

@Composable
fun GameOptions(
    onClickBox: (Options) -> Unit,
    onClickDetails: (Options) -> Unit,
    selected: () -> Options,
    screenSizes: ScreenSizes
) {
    Row(
        modifier = Modifier
            .padding(top = screenSizes.height * 2.3f)
            .fillMaxWidth()
            .height(screenSizes.height),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly

    ) {
        BoxGame(
            Options.NORMAL,
            tag = LobbyTags.GAME_NORMAL_REQUEST_TAG,
            detailTag = LobbyTags.GAME_NORMAL_DETAIL_REQUEST_TAG,
            selected = selected() == Options.NORMAL,
            screenSizes = screenSizes.copy(width = screenSizes.width * 0.3f),
            onClickBox = { onClickBox(Options.NORMAL) },
            onClickDetails = { onClickDetails(Options.NORMAL) }
        )
        BoxGame(Options.SWAP,
            selected = selected() == Options.SWAP,
            tag = LobbyTags.GAME_SWAP_REQUEST_TAG,
            detailTag = LobbyTags.GAME_SWAP_DETAIL_REQUEST_TAG,
            screenSizes = screenSizes.copy(width = screenSizes.width * 0.3f),
            onClickBox = { onClickBox(Options.SWAP) },
            onClickDetails = { onClickDetails(Options.SWAP) }
        )
        BoxGame(Options.SWAP_FIRST_MOVE,
            selected = selected() == Options.SWAP_FIRST_MOVE,
            tag = LobbyTags.GAME_SWAP_FIRST_MOVE_REQUEST_TAG,
            detailTag = LobbyTags.GAME_SWAP_FIRST_MOVE_DETAIL_REQUEST_TAG,
            screenSizes = screenSizes.copy(width = screenSizes.width * 0.3f),
            onClickBox = { onClickBox(Options.SWAP_FIRST_MOVE) },
            onClickDetails = { onClickDetails(Options.SWAP_FIRST_MOVE) }
        )
    }


}


@Composable
fun DisplayRank(playerRank: Rank, playerElo: Double, padding: Dp, screenSizes: ScreenSizes) {
    val image = when(playerRank) {
        Rank.UNRANKED -> R.drawable.unranked
        Rank.NOOB -> R.drawable.noob
        Rank.BEGINNER -> R.drawable.beginner
        Rank.INTERMEDIATE -> R.drawable.intermediate
        Rank.EXPERT -> R.drawable.expert
        Rank.PRO -> R.drawable.pro
    }

    Column(
        modifier = Modifier
            .padding(top = padding)
            .fillMaxWidth()
            .height(screenSizes.height),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = "DetailGame",
            modifier = Modifier
                .fillMaxWidth()
                .height(screenSizes.height * 0.7f)
        )

        Box (
            modifier = Modifier
                .fillMaxWidth()
                .height(screenSizes.height * 0.3f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.show_rank_details),
                contentDescription = "DetailGame",
                modifier = Modifier
                    .fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(
                    text = playerRank.toString(),
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.karasha)),
                        fontSize = 10.sp,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "$playerElo / ${playerRank.goal}",
                    fontSize = 10.sp,
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.arimo_variable)),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
