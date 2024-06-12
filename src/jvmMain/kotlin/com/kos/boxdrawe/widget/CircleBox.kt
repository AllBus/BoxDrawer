package com.kos.boxdrawe.widget

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import vectors.Vec2
import kotlin.math.PI

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LineBox(
    modifier: Modifier,
    onGetStartValue: () -> Double,
    onMove: (current: Double, change: Double, start: Double) -> Unit
) {
    val pressedState = remember { mutableStateOf(false) }
    val thumbPosition = remember { mutableStateOf(0) }
    val thumbStartPosition = remember { mutableStateOf(0) }
    val thumbPlusValue = remember { mutableStateOf(0.0) }
    Box(modifier = modifier) {
        val color = MaterialTheme.colors.primary
        val thumbColor = MaterialTheme.colors.secondary
        Canvas(
            modifier = Modifier.fillMaxSize().clipToBounds()
                .onPointerEvent(PointerEventType.Press) {
                    pressedState.value = true

                    val p = it.changes.first().position.toVec2()
                    val s = size.toVec2() / 2.0
                    thumbPlusValue.value = onGetStartValue()
                    calculateMove(p, s, thumbPosition, thumbStartPosition, thumbPlusValue.value, onMove, false)

                }
                .onPointerEvent(PointerEventType.Move) {
                    if (pressedState.value) {
                        val p = it.changes.first().position.toVec2()
                        val s = size.toVec2() / 2.0
                        calculateMove(p, s, thumbPosition, thumbStartPosition, thumbPlusValue.value, onMove, true)
                    }
                }
                .onPointerEvent(PointerEventType.Release) {
                    pressedState.value = false
                },

            ) {
            val c = size / 2f
            val delta = 8f
            val wid = c.width * 2 / 3
            val thumbRadius = wid / 2


            this.withTransform({
                translate(c.width, c.height)
            }
            ) {

                for (i in -12..12) {
                    drawLine(
                        color = color,
                        start = Offset(-wid, i * delta),
                        end = Offset(wid, i * delta),
                        strokeWidth = 1f
                    )
                }
                if (pressedState.value) {

                    drawCircle(
                        color = thumbColor,
                        radius = thumbRadius,
                        center = Offset(0f, thumbPosition.value.toFloat() * delta),
                        style = Fill
                    )

                }
            }
        }

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CircleBox(
    modifier: Modifier,
    onRotate: (current: Double, change: Double, start: Double) -> Unit
) {
    val pressedState = remember { mutableStateOf(false) }
    val thumbPosition = remember { mutableStateOf(0) }
    val thumbStartPosition = remember { mutableStateOf(0) }
    Box(modifier = modifier) {
        val color = MaterialTheme.colors.primary
        val thumbColor = MaterialTheme.colors.secondary
        Canvas(
            modifier = Modifier.fillMaxSize().clipToBounds()
                .onPointerEvent(PointerEventType.Press) {
                    pressedState.value = true

                    val p = it.changes.first().position.toVec2()
                    val s = size.toVec2() / 2.0
                    calculateRotor(p, s, thumbPosition, thumbStartPosition, onRotate, false)

                }
                .onPointerEvent(PointerEventType.Move) {
                    if (pressedState.value) {
                        val p = it.changes.first().position.toVec2()
                        val s = size.toVec2() / 2.0
                        calculateRotor(p, s, thumbPosition, thumbStartPosition, onRotate, true)
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

private fun calculateMove(
    p: Vec2,
    s: Vec2,
    thumbPosition: MutableState<Int>,
    thumbStartPosition: MutableState<Int>,
    plusValue:Double,
    onMove: (current: Double, change: Double, start: Double) -> Unit,
    isMove: Boolean,
) {
    val ps = (p - s)
    val d = ps.y / 8.0

    val c = if (isMove) {
        val c2 = d - thumbPosition.value
        c2
    } else {
        thumbStartPosition.value = d.toInt()
        0f
    }

    thumbPosition.value = d.toInt()
    onMove(
        thumbStartPosition.value-thumbPosition.value.toDouble()+plusValue,
        c.toDouble(),
        plusValue,
    )
}

private fun calculateRotor(
    p: Vec2,
    s: Vec2,
    thumbPosition: MutableState<Int>,
    thumbStartPosition: MutableState<Int>,
    onRotate: (current: Double, change: Double, start: Double) -> Unit,
    isMove: Boolean,
) {
    val ps = (p - s)

    val a = ps.angle * 180 / PI

    if (a.isFinite()) {
        val d = if (ps.magnitude > s.x) {
            a.toInt()
        } else {
            (a / 15).toInt() * 15
        }

        val c = if (isMove) {
            val c2 = d - thumbPosition.value
            if (c2 > 180f)
                c2 - 360f
            else
                if (c2 < -180f)
                    c2 + 360f
                else
                    c2
        } else {
            thumbStartPosition.value = d
            0f
        }

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
        CircleBox(Modifier, { _, _, _ -> })
    }
}