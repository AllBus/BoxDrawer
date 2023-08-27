package com.kos.boxdrawe.widget

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.onClick
import androidx.compose.runtime.Composable
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RunCheckBox(
    checked: Boolean,
    title:String,
    onCheckedChange: (Boolean) -> Unit,

) = Row(){
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = Modifier.align(Alignment.CenterVertically),
    )

    Text(
        text = title,
        modifier = Modifier.align(Alignment.CenterVertically).onClick(
            onClick = { onCheckedChange(!checked) }
        ),
        textAlign = TextAlign.Center,
        softWrap = false,
    )
}


@Composable
@Preview
fun RunCheckBox(){
    MaterialTheme {
        RunCheckBox(true,"Нарисовать деталь",{})
    }
}