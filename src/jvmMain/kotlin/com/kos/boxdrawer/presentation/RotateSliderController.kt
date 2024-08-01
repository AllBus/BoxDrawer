package com.kos.boxdrawer.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RotateSliderController(
    modifier: Modifier,
    dropValueX: MutableState<Float>,
    dropValueY: MutableState<Float>,
    dropValueZ: MutableState<Float>,
    onRotateDisplay: () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Slider(
            modifier = Modifier.wrapContentHeight(),
            onValueChange = {
                dropValueX.value = it;
                onRotateDisplay()
            },
            value = dropValueX.value,
            valueRange = -360f..360f
        )

        Slider(
            modifier = Modifier.wrapContentHeight(),
            onValueChange = {
                dropValueY.value = it;
                onRotateDisplay()
            },
            value = dropValueY.value,
            valueRange = -360f..360f
        )
        Slider(
            modifier = Modifier.wrapContentHeight(),
            onValueChange = {
                dropValueZ.value = it;
                onRotateDisplay(
                )
            },
            value = dropValueZ.value,
            valueRange = -360f..360f
        )
    }
}