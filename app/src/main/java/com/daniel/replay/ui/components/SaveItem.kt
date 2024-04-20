package com.daniel.replay.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniel.replay.ui.theme.OnSurface


@Composable
fun SaveItem(saveName: String, saveTime: String, pkgName: String, onDeleteSave: () -> Unit, onRestoreSave: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color(0xFFd6e3ff))) {
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF6f797a)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color(0xFFd6e3ff))
        ){
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = saveName,
                    fontSize = 16.sp,
                    color = OnSurface
                )
                Text(
                    text = saveTime,
                    fontSize = 11.sp,
                    color = Color(0xFF3f4849)
                )
            }
            Button(
                modifier = Modifier.fillMaxHeight(),
                shape = RectangleShape,
                onClick = onRestoreSave,
                colors = ButtonDefaults.buttonColors(Color(0xFF091b36))
            ){
                Text(
                    text = "Restore"
                )
            }
            Button(
                modifier = Modifier.fillMaxHeight(),
                shape = RectangleShape,
                onClick = onDeleteSave,
                colors = ButtonDefaults.buttonColors(Color(0xFFBA000D))
            ){
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null,
                )
            }
        }
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF6f797a)
        )
    }
}