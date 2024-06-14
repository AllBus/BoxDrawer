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
import com.kos.boxdrawer.generated.resources.Res
import com.kos.boxdrawer.generated.resources.boxPazDelta
import com.kos.boxdrawer.generated.resources.boxPazLabel
import com.kos.boxdrawer.generated.resources.boxPazLength
import com.kos.boxdrawer.generated.resources.boxPazWeight
import org.jetbrains.compose.resources.stringResource

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
  //      Label(title, singleLine = true)
        RunCheckBox(Wchecked.value, title) { c ->
            Wchecked.value = c
            zigState.redrawBox()
        }
        NumericUpDown( "", "", WwidthInput)
        NumericUpDown("", "", WdeltaInput)
        NumericUpDown( "", "", WheightInput)
    }
}

@Composable
fun ZigZagLabel(
    modifier: Modifier = Modifier,
){
    Column(modifier = modifier.padding(end = 2.dp),
        horizontalAlignment = Alignment.End) {
        Label(stringResource(Res.string.boxPazLabel), singleLine = true)
        Text(
            modifier = Modifier.padding(vertical = 4.dp),
            text = stringResource(Res.string.boxPazLength),
            fontSize = LocalTextStyle.current.fontSize,

            softWrap = false,
            textAlign = TextAlign.End,
        )
        Text(
            modifier = Modifier.padding(vertical = 4.dp),
            text = stringResource(Res.string.boxPazDelta),
            fontSize = LocalTextStyle.current.fontSize,
            softWrap = false,
            textAlign = TextAlign.End,
        )
        Text(
            modifier = Modifier.padding(vertical = 4.dp),
            text = stringResource(Res.string.boxPazWeight),
            fontSize = LocalTextStyle.current.fontSize,
            softWrap = false,
            textAlign = TextAlign.End,
        )
    }
}
