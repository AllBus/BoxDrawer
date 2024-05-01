package com.kos.boxdrawe.widget

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun RunButton(
    title:String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = Button(
    onClick = onClick,
    modifier = Modifier.defaultMinSize(
        minWidth = 160.dp
    ).composed { modifier }
) {
    Text(
        text = title,
        modifier = Modifier,
        textAlign = TextAlign.Center,
    )

}


@Composable
@Preview
fun RunButtonPreview(){
    MaterialTheme {
        RunButton("Нарисовать\nдеталь",){}
    }
}