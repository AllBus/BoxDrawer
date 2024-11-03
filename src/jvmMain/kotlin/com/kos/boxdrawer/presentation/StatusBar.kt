package com.kos.boxdrawer.presentation

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kos.boxdrawe.icons.IconCopy
import com.kos.boxdrawe.themes.ThemeColors
import java.lang.Math.pow
import kotlin.math.abs



@Composable
fun StatusBar(
    displayScale: MutableFloatState,
    stateText: State<String>,
    onHomeClick: () -> Unit,
    onCalculatorClick: () -> Unit,
) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        TextButton(onClick = onCalculatorClick) {
            Icon(
                IconCopy.rememberCalculate(),
                null,
            )
        }
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
                modifier = Modifier.defaultMinSize(minWidth = 60.dp).clickable {
                    displayScale.value = 2.0f
                },
                textAlign = TextAlign.End,
                fontSize = 12.sp,
                color = ThemeColors.displayLabelColor
            )

            Slider(
                modifier = Modifier.width(200.dp).wrapContentHeight(),
                onValueChange = { i ->
                    displayScale.value = ZoomUtils.indexToZoom(i.toInt()).toFloat()

                    //displayScale.value = Math.pow(1.2, (it - 20).toDouble()).toFloat()
                },
                value = ZoomUtils.calcZoom(displayScale.value.toDouble()).toFloat() ,
                valueRange = 1f..90f
            )
            TextButton(onClick = onHomeClick) {
                Icon(
                    Icons.Rounded.Home,
                    null,
                )
            }
        }
    }
}