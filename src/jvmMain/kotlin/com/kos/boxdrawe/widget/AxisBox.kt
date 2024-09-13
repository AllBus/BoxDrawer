package com.kos.boxdrawe.widget

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
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.IntOffset
import vectors.Vec2

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AxisBox(
    modifier: Modifier,
    onGetStartValue: () -> Vec2,
    onMove: (current: Vec2, change: Vec2, start: Vec2) -> Unit
) {
    val pressedState = remember { mutableStateOf(false) }
    val thumbPosition = remember { mutableStateOf(IntOffset(0, 0)) }
    val thumbStartPosition = remember { mutableStateOf(IntOffset(0, 0)) }
    val thumbPlusValue = remember { mutableStateOf<Vec2>(Vec2.Zero) }

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
                    calculateMoveXY(
                        p,
                        s,
                        thumbPosition,
                        thumbStartPosition,
                        thumbPlusValue.value,
                        onMove,
                        false
                    )

                }
                .onPointerEvent(PointerEventType.Move) {
                    if (pressedState.value) {
                        val p = it.changes.first().position.toVec2()
                        val s = size.toVec2() / 2.0
                        calculateMoveXY(
                            p,
                            s,
                            thumbPosition,
                            thumbStartPosition,
                            thumbPlusValue.value,
                            onMove,
                            true
                        )
                    }
                }
                .onPointerEvent(PointerEventType.Release) {
                    pressedState.value = false
                },

            ) {
            val c = size / 2f
            val delta = 8f
            val wid = c.width / 3
            val thumbRadius = c.width / 6


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
                    drawLine(
                        color = color,
                        start = Offset(i * delta, -wid),
                        end = Offset(i * delta, wid),
                        strokeWidth = 1f
                    )
                }
                if (pressedState.value) {

                    drawCircle(
                        color = thumbColor,
                        radius = thumbRadius,
                        center = Offset(
                            thumbPosition.value.x.toFloat() * delta,
                            thumbPosition.value.y.toFloat() * delta
                        ),
                        style = Fill
                    )

                }
            }
        }

    }
}


private fun calculateMoveXY(
    p: Vec2,
    s: Vec2,
    thumbPosition: MutableState<IntOffset>,
    thumbStartPosition: MutableState<IntOffset>,
    plusValue:Vec2,
    onMove: (current: Vec2, change: Vec2, start: Vec2) -> Unit,
    isMove: Boolean,
) {
    val ps = (p - s)
    val d = ps / 8.0

    val c = if (isMove) {
        val c2 = d - Vec2( thumbPosition.value.x.toDouble(), thumbPosition.value.y.toDouble())
        c2
    } else {
        thumbStartPosition.value = IntOffset(d.x.toInt(), d.y.toInt())
        Vec2.Zero
    }

    thumbPosition.value = IntOffset(d.x.toInt(), d.y.toInt())

    val ci = thumbStartPosition.value+thumbPosition.value
    onMove(
        Vec2(ci.x.toDouble(), ci.y.toDouble())+plusValue,
        c,
        plusValue,
    )
}