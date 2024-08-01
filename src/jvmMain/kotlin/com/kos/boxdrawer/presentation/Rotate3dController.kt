package com.kos.boxdrawer.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import com.kos.boxdrawe.widget.toVec2
import vectors.Vec2
import kotlin.math.min

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Rotate3dController(
    modifier: Modifier,
    dropValueX: MutableState<Float>,
    dropValueY: MutableState<Float>,
    dropValueZ: MutableState<Float>,
    onRotateDisplay: () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        val starPosition = remember { mutableStateOf(Vec2.Zero) }
        val selectedType = remember { mutableIntStateOf(0) }
        val startX = remember { mutableStateOf(dropValueX.value) }
        val startY = remember { mutableStateOf(dropValueY.value) }
        val startZ = remember { mutableStateOf(dropValueZ.value) }
        Canvas(modifier = Modifier.size(64.dp).onPointerEvent(PointerEventType.Press) {

            val sz = this.size
            val posi = it.changes.first().position.toVec2()
            val pos = (posi - sz.center.toOffset().toVec2())
            val r = pos.magnitude
            selectedType.value = if (r > min(sz.width, sz.height) * 2 / 5 - 4) 2 else 1

//            val pt = when (it.button) {
//                PointerButton.Primary -> 1
//                PointerButton.Secondary -> 2
//                PointerButton.Tertiary -> 3
//                else -> 0
//            }

            starPosition.value = posi
            //selectedType.value = pt
            startX.value = dropValueX.value
            startY.value = dropValueY.value
            startZ.value = dropValueZ.value - Math.toDegrees(pos.angle).toFloat()
        }.onPointerEvent(PointerEventType.Release) {
            selectedType.value = 0
        }.onPointerEvent(PointerEventType.Move) {
            val sz = this.size
            val posi = it.changes.first().position.toVec2()
            val pos = posi - starPosition.value

            when (selectedType.value) {
                1 -> {
                    dropValueX.value = (startX.value + pos.y.toFloat()) % 360f
                    dropValueY.value = (startY.value + pos.x.toFloat()) % 360f
                    onRotateDisplay()
                }

                2 -> {
                    dropValueZ.value =
                        (startZ.value + Math.toDegrees((posi - sz.center.toOffset().toVec2()).angle)
                            .toFloat()) % 360f
                    onRotateDisplay()
                }
            }

        }) {
            val s = size.minDimension
            val stroke = Stroke(width = 2f)
            val r = s * 2 / 5

            withTransform({
                val m = Matrix()
                m.rotateX(dropValueX.value)
                m.rotateY(dropValueY.value)
                m.rotateZ(dropValueZ.value)
                translate(size.center.x, size.center.y)
                transform(m)
            }) {
                this.drawCircle(Color.Green, center = Offset.Zero, radius = r, style = stroke)
                if (dropValueY.value == 0.0f && dropValueX.value == 0.0f) {
                    this.drawLine(
                        Color.Red,
                        start = Offset(-r, 0f),
                        end = Offset(r, 0f),
                        strokeWidth = stroke.width
                    )

                    this.drawLine(
                        Color.Yellow,
                        start = Offset(0f, -r),
                        end = Offset(0f, r),
                        strokeWidth = stroke.width
                    )
                }
                withTransform({
                    val m = Matrix()
                    m.rotateY(90f)
                    transform(m)

                }) {
                    this.drawCircle(Color.Yellow, center = Offset.Zero, radius = r, style = stroke)

                }
                withTransform({
                    val m = Matrix()
                    m.rotateX(90f)
                    transform(m)
                }) {
                    this.drawCircle(Color.Red, center = Offset.Zero, radius = r, style = stroke)
                }

            }
        }

    }
}