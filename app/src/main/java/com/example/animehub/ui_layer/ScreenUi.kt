package com.example.animehub.ui_layer

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
import androidx.media3.ui.PlayerView
import com.example.animehub.Model.Anime
import com.example.animehub.R
import com.example.animehub.data.Resource
import kotlin.random.Random

@Composable
fun ScreenUi(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val viewModel: AnimeViewModel = hiltViewModel()
    val videoUris = listOf(
        Uri.parse("android.resource://${context.packageName}/${R.raw.video11}"),
        Uri.parse("android.resource://${context.packageName}/${R.raw.video12}"),
        Uri.parse("android.resource://${context.packageName}/${R.raw.video13}"),
        Uri.parse("android.resource://${context.packageName}/${R.raw.video14}"),
        Uri.parse("android.resource://${context.packageName}/${R.raw.video15}"),
        Uri.parse("android.resource://${context.packageName}/${R.raw.video16}"),
        Uri.parse("android.resource://${context.packageName}/${R.raw.video17}")
    )
    var startAnimation by remember { mutableStateOf(false) }
    var ShowCard by remember { mutableStateOf(false) }

    // Animating the offset of the Box
    val offsetY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 400.dp,
        animationSpec = tween(durationMillis = 1000)
    )

    val animeList by viewModel.animeList.collectAsState()
    Box(modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding()) {
        RandomBackgroundClips(videoUris = videoUris)
        if(!startAnimation){
            Column(modifier = Modifier.fillMaxWidth().padding(top = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Text(text = "Welcome to AnimeHub",
                    textAlign = TextAlign.Center,
                    fontSize = 35.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White)
                Text(text = "Check Your Favorite Name List",
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White)
            }
        }
        Column {
            Box(modifier = Modifier
                .wrapContentSize()
                .padding()
                .offset(y = offsetY)) {
                SearchButtonUi(paddingValues){
                        searchResult->
                    startAnimation = true
                    ShowCard = true
                    viewModel.fetchAnimeList(searchResult)
                }
            }
            if(ShowCard){
                Card( elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                    shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = offsetY),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )) {
                    Box {
                        Image(painter = painterResource(id = R.drawable.glassmorphismeffect), contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize())
                        when (animeList) {
                            is Resource.Loading -> {
                                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                                    CircularProgressIndicator()
                                }
                            }

                            is Resource.Success -> {
                                val animeData = animeList as Resource.Success<List<Anime>>
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2)
                                ) {
                                    items(animeData.data) { anime ->
                                        AnimeItem(anime)
                                    }
                                }
                            }

                            is Resource.Failure -> {
                                val e = animeList as Resource.Failure
                                if (e.error != "Search") {
                                    Toast.makeText(context, e.error, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun RandomBackgroundClips(videoUris: List<Uri>) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("video_prefs", Context.MODE_PRIVATE)
    val lastPlayedIndex = sharedPreferences.getInt("last_played_index", -1)
    val newVideoIndex = remember {
        var newIndex: Int
        do {
            newIndex = Random.nextInt(videoUris.size)
        } while (newIndex == lastPlayedIndex)
        newIndex
    }
    val randomVideoUri = remember {
        videoUris[Random.nextInt(videoUris.size)]
    }
    Log.d("VideoBackground", "Playing video: $randomVideoUri")
    val exoPlayer = remember {
        context.buildExoPlayer(randomVideoUri)
    }
    exoPlayer.addListener(object : Player.Listener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> Log.d("ExoPlayer", "Buffering")
                Player.STATE_READY -> Log.d("ExoPlayer", "Ready to play")
                Player.STATE_ENDED -> Log.d("ExoPlayer", "Playback ended")
                Player.STATE_IDLE -> Log.d("ExoPlayer", "Idle")
            }
        }
    })

    DisposableEffect(AndroidView(
        factory = { context.buildPlayerView(exoPlayer) },
        modifier = Modifier.fillMaxSize()
    )) {
        sharedPreferences.edit().putInt("last_played_index", newVideoIndex).apply()
        onDispose {
            exoPlayer.release()
        }
    }
}

private fun Context.buildExoPlayer(randomVideoUri: Uri) =
    ExoPlayer.Builder(this).build().apply {
        setMediaItem(MediaItem.fromUri(randomVideoUri))
        repeatMode = Player.REPEAT_MODE_ALL
        playWhenReady = true
        volume = 0f
        prepare()
    }

@OptIn(UnstableApi::class)
private fun Context.buildPlayerView(exoPlayer: ExoPlayer) =
    PlayerView(this).apply {
        player = exoPlayer
        layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        useController = false
        resizeMode = RESIZE_MODE_ZOOM
    }

@Composable
fun SearchButtonUi(paddingValues: PaddingValues,modifier: Modifier = Modifier, onClickUp: (ss:String) -> Unit) {
    var searchAnime by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    TextField(
        value = searchAnime,
        onValueChange = { searchAnime = it },
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .padding(top = paddingValues.calculateTopPadding()),
        placeholder = { Text(text = "Search Anime List")},
        trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = ShapeDefaults.Large,
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Color.White.copy(alpha = 0.30f),
            focusedContainerColor = Color.White.copy(0.90f),
            unfocusedPlaceholderColor = Color.Black.copy(0.90f),
            focusedPlaceholderColor = Color.Black.copy(0.50f),
            unfocusedTextColor = Color.Black,
            focusedTextColor = Color.Black
        ),
        singleLine = true,
        keyboardActions = KeyboardActions(
            onDone = {
                // Handle the search action here
                keyboardController?.hide()
                // You can use searchAnime value here
                if(searchAnime != ""){
                    onClickUp.invoke(searchAnime)
                }else{
                    Toast.makeText(context, "Enter Anime Name", Toast.LENGTH_SHORT).show()
                }
            }
        )
    )
}
