package com.example.animehub.ui_layer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.animehub.Model.Anime

@Composable
fun AnimeItem(anime: Anime) {
    var expanded by remember { mutableStateOf(false) }
    val mm = Modifier
        .wrapContentHeight()
        .padding(10.dp)
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            expanded = !expanded
        }

    Card(modifier = if(expanded) mm else Modifier
        .padding(10.dp)
        .height(if (expanded) 0.dp else 270.dp)
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            expanded = !expanded
        }, colors = CardDefaults.cardColors(containerColor = Color.Black.copy(0.50f), contentColor = Color.White)) {
        Column() {
            Image(painter = rememberImagePainter(data = anime.images?.jpg?.large_image_url),
                contentDescription = "img",modifier = Modifier.align(Alignment.CenterHorizontally))

            val title = anime.title
            Text(text = "Title: $title")
            if(anime.episodes != null){
                Text(text = "Ep: ${anime.episodes.toString()}")
            }
            if(anime.duration != null){
                Text(text = "Duration: ${anime.duration}")
            }
            if(anime.rating != null){
                Text(text = "Rating : ${anime.rating}")
            }
            if(anime.year != null){
                Text(text = "Year : ${anime.year.toString()}")
            }
        }
    }
 }