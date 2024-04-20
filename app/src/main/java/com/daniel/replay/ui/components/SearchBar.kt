package com.daniel.replay.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniel.replay.ui.theme.OnSurface

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchText: String,
    onValueChange: (String) -> Unit
) {


    TextField(
        modifier = modifier,
        value = searchText,
        onValueChange = onValueChange,
        maxLines = 1,
        shape = RoundedCornerShape(24.dp),
        leadingIcon = {
            Icon(Icons.Filled.Search, null, tint = OnSurface)
        },
        placeholder = {
            Text(
                text = "Search",
                color = OnSurface,
                fontSize = 18.sp
            )
        },
        colors = TextFieldDefaults.colors(
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor =  Color.Transparent,
            //Above code to hide the indicator of the text field
            focusedContainerColor = Color(0xFFD6E3FF),
            unfocusedContainerColor = Color(0xFFD6E3FF),
        ),
        textStyle = TextStyle(
            color = OnSurface,
            fontSize = 18.sp
        )
    )
}