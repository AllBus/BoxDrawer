package com.kos.boxdrawe.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.presentation.ZigZagState

@Composable
fun ZigZagInput(
    modifier: Modifier = Modifier,
    title:String,
    drawNames:Boolean = true,
    zigState: ZigZagState,
) {
    val Wchecked = remember { zigState.enable }
    val WwidthInput = remember { zigState.width }
    val WdeltaInput = remember { zigState.delta }
    val WheightInput = remember { zigState.height }

    Column(modifier = modifier) {
        Label(title, singleLine = true)
        NumericUpDown(if (drawNames) "Длина паза" else "", "", WwidthInput)
        NumericUpDown(if (drawNames) "Дельта" else "", "", WdeltaInput)
        NumericUpDown(if (drawNames) "Толщина паза" else "", "", WheightInput)
        RunCheckBox(Wchecked.value, if (drawNames) "Есть" else "", { c ->
            Wchecked.value = c
            zigState.redrawBox()
        })
    }
}

@Composable
fun ZigZagLabel(
    modifier: Modifier = Modifier,
){
    Column(modifier = modifier,
        horizontalAlignment = Alignment.End) {
        Label("", singleLine = true)
        Text(
            modifier = Modifier.padding(vertical = 6.dp),
            text = "Длина",
            fontSize = LocalTextStyle.current.fontSize,

            softWrap = false,
            textAlign = TextAlign.End,
        )
        Text(
            modifier = Modifier.padding(vertical = 6.dp),
            text = "Дельта",
            fontSize = LocalTextStyle.current.fontSize,
            softWrap = false,
            textAlign = TextAlign.End,
        )
        Text(
            modifier = Modifier.padding(vertical = 6.dp),
            text = "Ширина",
            fontSize = LocalTextStyle.current.fontSize,
            softWrap = false,
            textAlign = TextAlign.End,
        )
        Text(
            modifier = Modifier.padding(vertical = 4.dp),
            text =  "Есть",
            fontSize = LocalTextStyle.current.fontSize,
            softWrap = false,
            textAlign = TextAlign.End,
        )
    }
}
