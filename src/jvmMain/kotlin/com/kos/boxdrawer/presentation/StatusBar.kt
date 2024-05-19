package com.kos.boxdrawer.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import calcZoom
import com.kos.boxdrawe.themes.ThemeColors

@Composable
fun StatusBar(
    displayScale: MutableFloatState,
    pos: MutableState<Offset>,
    stateText: State<String>
) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        Spacer(
            modifier = Modifier.weight(1f)
        )
        Text(
            stateText.value,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            color = ThemeColors.displayLabelColor,
            modifier = Modifier.padding(8.dp)
        )
        Spacer(
            modifier = Modifier.weight(1f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "%.3f".format(displayScale.value),
                textAlign = TextAlign.End,
                fontSize = 12.sp,
                modifier = Modifier.defaultMinSize(120.dp),
                color = ThemeColors.displayLabelColor
            )
            Column(
                modifier = Modifier.width(300.dp).wrapContentHeight(),
            ) {
                Slider(
                    modifier = Modifier.wrapContentHeight(),
                    onValueChange = {
                        displayScale.value = Math.pow(1.2, (it - 20).toDouble()).toFloat()
                    },
                    value = calcZoom(displayScale.value) + 20, ///log(displayScale.value.toDouble()).toFloat(),
                    valueRange = 1f..60f
                )
            }
            TextButton(onClick = {
                pos.value = Offset.Zero
            }) {
                Icon(
                    Icons.Rounded.Home,
                    null
                )
            }
        }
    }
}