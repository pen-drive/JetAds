package com.example.consumer.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.consumer.R

@Composable
fun SwitchAds(isAdsEnable: Boolean, toggleAds: () -> Unit) {


    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                toggleAds()
            }) {
        Text(text = stringResource(R.string.ligar_desligar_ads))
        Switch(checked = isAdsEnable, onCheckedChange = {
            toggleAds()
        })
    }


}