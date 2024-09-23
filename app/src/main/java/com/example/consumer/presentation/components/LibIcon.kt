package com.example.consumer.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.consumer.R


@Composable
fun LibIcon(modifier: Modifier = Modifier) {

    Image(
        painterResource(R.drawable.jetads),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}