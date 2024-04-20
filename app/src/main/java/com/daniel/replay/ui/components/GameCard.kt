package com.daniel.replay.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.daniel.replay.ui.theme.OnSurface
import com.daniel.replay.ui.theme.PrimaryContainer
import com.daniel.replay.ui.theme.SecondayOnSurface


@Composable
fun GameCard(icon: Drawable, name: String, packageName: String, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable {
                onClick()
            },
        colors = CardDefaults.elevatedCardColors(PrimaryContainer)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ){
            Image(
                modifier = Modifier.padding(12.dp),
                bitmap = icon.toBitmap().asImageBitmap(),
                contentDescription = null
            )
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    color = OnSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = packageName,
                    color = SecondayOnSurface,
                    fontSize = 13.sp
                )
            }
        }
    }
}

