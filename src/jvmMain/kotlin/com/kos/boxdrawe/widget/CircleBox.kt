package com.kos.boxdrawe.widget

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import vectors.Vec2
import kotlin.math.PI

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CircleBox(
    onRotate: (current: Double, change: Double, start: Double) -> Unit
) {
    val pressedState = remember { mutableStateOf(false) }
    val thumbPosition = remember { mutableStateOf(0) }
    val thumbStartPosition = remember { mutableStateOf(0) }
    Box(modifier = Modifier.size(160.dp)) {
        val color = MaterialTheme.colors.primary
        val thumbColor = MaterialTheme.colors.secondary
        Canvas(
            modifier = Modifier.fillMaxSize().clipToBounds()
                .onPointerEvent(PointerEventType.Press) {
                    pressedState.value = true

                    val p = it.changes.first().position.toVec2()
                    val s = size.toVec2() / 2.0
                    calculateRotor(p, s, thumbPosition, thumbStartPosition, onRotate )

                }
                .onPointerEvent(PointerEventType.Move) {
                    if (pressedState.value) {
                        val p = it.changes.first().position.toVec2()
                        val s = size.toVec2() / 2.0
                        calculateRotor(p, s, thumbPosition, thumbStartPosition, onRotate )
                    }
                }
                .onPointerEvent(PointerEventType.Release) {
                    pressedState.value = false
                },

            ) {
            val c = size / 2f
            val radius = c.width - 4f
            val inRadius = c.width * 2 / 3
            val outRadius = c.width * 5 / 6
            val thumbRadius = (outRadius - inRadius) / 3
            this.withTransform({
                translate(c.width, c.height)
            }
            ) {
                drawCircle(color, radius, Offset.Zero, style = Stroke(1f))
                for (i in 0 until 24) {
                    rotate(i * 15f, Offset.Zero) {
                        drawLine(
                            color = color,
                            start = Offset(0f, inRadius),
                            end = Offset(0f, outRadius),
                            strokeWidth = 1f
                        )
                    }
                }
                if (pressedState.value) {
                    rotate(thumbPosition.value.toFloat() - 90f, Offset.Zero) {
                        drawCircle(
                            color = thumbColor,
                            radius = thumbRadius,
                            center = Offset(0f, outRadius),
                            style = Fill
                        )
                    }
                }
            }
        }
    }
}

private fun calculateRotor(
    p: Vec2,
    s:Vec2,
    thumbPosition: MutableState<Int>,
    thumbStartPosition: MutableState<Int>,
    onRotate: (current: Double, change: Double, start: Double) -> Unit,

) {
    val ps = (p - s)

    val a = ps.angle * 180 / PI

    if (a.isFinite()) {
        val d = if (ps.magnitude > s.x) {
            a.toInt()
        } else {
            (a / 15).toInt() * 15
        }

        val c2 = d - thumbPosition.value
        val c = if (c2 > 180f)
            c2 - 360f
        else
            if (c2 < -180f)
                c2 + 360f
            else
                c2
        thumbPosition.value = d
        onRotate(
            thumbPosition.value.toDouble(),
            c.toDouble(),
            thumbStartPosition.value.toDouble()
        )
    }
}

@Composable
@Preview
private fun PreviewCircleBox() {
    MaterialTheme {
        CircleBox({ _, _, _ -> })
    }
}