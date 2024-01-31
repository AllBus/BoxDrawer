package com.kos.boxdrawe.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ZigZagInput(
    modifier: Modifier = Modifier,
    title:String,
    drawNames:Boolean = true,
    checked: MutableState<Boolean>,
    widthInput: NumericTextFieldState,
    deltaInput: NumericTextFieldState,
    heightInput: NumericTextFieldState
) {
    Column(modifier = modifier) {
        Label(title, singleLine = true)
        NumericUpDown(if (drawNames) "Длина паза" else "", "", widthInput)
        NumericUpDown(if (drawNames) "Дельта" else "", "", deltaInput)
        NumericUpDown(if (drawNames) "Толщина паза" else "", "", heightInput)
        RunCheckBox(checked.value, if (drawNames) "Есть" else "", { c -> checked.value = c })
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
            text = "Толщина",
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
