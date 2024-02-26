package com.example.gomoku.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gomoku.R
import com.example.gomoku.domain.Rank
import com.example.gomoku.domain.Stats
import com.example.gomoku.ui.replay.ScreenSizes

@Composable
fun StatsNames(screenSizes: ScreenSizes) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenSizes.height),
        verticalAlignment = CenterVertically
    ) {

        WriteColumn(name = "Position", modifier = Modifier.width(screenSizes.width * 0.15f))
        WriteColumn(name = "Name", modifier = Modifier.width(screenSizes.width * 0.4f))
        WriteColumn(name = "W/D", modifier = Modifier.width(screenSizes.width * 0.15f))
        WriteColumn(name = "WinRate", modifier = Modifier.width(screenSizes.width * 0.15f))
        WriteColumn(name = "Elo", modifier = Modifier.width(screenSizes.width * 0.15f))
    }
}

@Composable
fun WriteColumn(
    name: String,
    modifier: Modifier,
    horizontal: Arrangement.Horizontal = Arrangement.Center
) {
    Row(
        modifier = modifier.border(1.dp, colorResource(id = R.color.background)),
        verticalAlignment = CenterVertically,
        horizontalArrangement = horizontal
    ) {
        Text(
            text = name,
            fontWeight = FontWeight.Bold
        )
    }

}

@Composable
fun RankingList(stats: List<Stats>, rank: Rank?, player: String?, screenSizes: ScreenSizes) {
    val listState = rememberLazyListState()
    val playerSelected = stats.find { it.player == player }
    LaunchedEffect(playerSelected) {
        playerSelected.let { player ->

            val playerIndex = stats.sortedByDescending { it.elo }.indexOf(player)

            if (playerIndex != -1) {
                listState.scrollToItem(playerIndex)
                listState.animateScrollToItem(playerIndex)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenSizes.height),
        state = listState
    ) {
        if (stats.isEmpty()) {
            item {
                Text(text = "Reloading the Ranking", style = TextStyle(fontSize = 40.sp))
            }
        }
        if (rank == null) {
            val statsOrder = stats.sortedByDescending { it.elo }
            items(statsOrder.size) { index ->
                StatsRows(stat = statsOrder[index], place = index + 1, player = player, screenSizes)
                Spacer(modifier = Modifier.height(screenSizes.height * 0.01f))
            }
        } else {
            val statsOrder = stats.sortedByDescending { it.elo }.filter { it.rank == rank }
            items(statsOrder.size) { index ->
                StatsRows(stat = statsOrder[index], place = index + 1, player, screenSizes)
                Spacer(modifier = Modifier.height(screenSizes.height * 0.01f))
            }
        }
    }
}

@Composable
fun StatsRows(stat: Stats, place: Int, player: String?, screenSizes: ScreenSizes) {
    val color = if (stat.player == player) {
        Color(0xFFC0CFFF)
    } else {
        Color(0xFFE0FFFF)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenSizes.height * 0.1f)
            .background(color, shape = RoundedCornerShape(screenSizes.height * 0.01f))
            .border(2.dp, Color(0xFFB0E2FF)),
        verticalAlignment = CenterVertically
    ) {

        Position(place,
            Modifier
                .width(screenSizes.width * 0.15f))
        Players(stat.player,
            Modifier
                .width(screenSizes.width * 0.4f))
        VictoriesDefeats(
            stat.victories,
            stat.defeats,
            Modifier
                .width(screenSizes.width * 0.15f)
        )
        WinRate(stat.winRate,
            Modifier
                .width(screenSizes.width * 0.15f))
        Elo(stat.elo.toInt(),
            Modifier
                .width(screenSizes.width * 0.15f))
    }
}


@Composable
fun PositionColor(place: Int, color: Color) {
    Text(
        text = "${place}º",
        fontWeight = FontWeight.Bold,
        color = color,
        style = TextStyle(fontSize = 20.sp)
    )
}

@Composable
fun Position(place: Int, modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (place) {
            1 -> PositionColor(place, Color(0xFFFFD700))
            2 -> PositionColor(place, color = Color(0xFFC0C0C0))
            3 -> PositionColor(place, color = Color(0xFFCD7F32))
            else -> PositionColor(place, color = Color.Black)
        }
    }

}

@Composable
fun Players(name: String, modifier: Modifier) {
    Column(modifier = modifier) {
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            style = TextStyle(fontSize = 15.sp)
        )
    }
}

@Composable
fun VictoriesDefeats(wins: Int, defeats: Int, modifier: Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = CenterVertically
    ) {
        Text(
            text = wins.toString(),
            color = Color(0xFF006400)
        )

        Text(text = "/")

        Text(
            text = defeats.toString(),
            color = Color(0xFF8B0000),
            style = TextStyle(fontSize = 15.sp)
        )
    }
}

@Composable
fun WinRate(winRate: Int, modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${winRate}%",
            style = TextStyle(fontSize = 15.sp)
        )
    }
}

@Composable
fun Elo(elo: Int, modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = elo.toString(),
            style = TextStyle(fontSize = 15.sp)
        )
    }
}