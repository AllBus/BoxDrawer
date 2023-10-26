package com.kos.boxdrawe.widget.display

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import com.kos.boxdrawe.drawer.drawFigures
import figure.IFigure

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun DisplayTortoise(displayScale: Float, figures: IFigure) {
    val posX = rememberSaveable("DisplayTortoiseX") { mutableStateOf(0f) }
    val posY = rememberSaveable("DisplayTortoiseY") { mutableStateOf(0f) }

    Canvas(modifier = Modifier.fillMaxSize().clipToBounds().onDrag(
        onDrag = { offset ->
            posX.value = posX.value + offset.x
            posY.value = posY.value + offset.y
        }
    ),
        onDraw = {
            val c = size / 2f

            this.translate(posX.value, posY.value) {
                this.scale(scale = Math.log(displayScale.toDouble()).toFloat()) {
                    this.translate(c.width + posX.value, c.height + posY.value) {
                        this.drawFigures(figures)

                    }
                }
            }

        })
}