package com.example.consumer.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.consumer.R


@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp), color = MaterialTheme.colors.surface, elevation = 8.dp

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            LibIcon(modifier = Modifier.size(240.dp))

            Text(
                text = stringResource(R.string.muito_obrigado_por_usar_esta_lib),
                fontWeight = FontWeight.ExtraBold,
                fontSize = MaterialTheme.typography.h4.fontSize
            )
            Text(
                text = stringResource(R.string.considere_contribuir_confira_o_readme_para_saber_como_voc_pode_ajudar),
                fontSize = MaterialTheme.typography.body1.fontSize
            )

            Text(text = stringResource(R.string.contribui_es_e_sugest_es_s_o_muito_bem_vidas_github))
        }
    }


}