package com.kos.boxdrawer.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.onClick
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material.ripple
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.boxdrawe.widget.toVec2
import kotlinx.coroutines.flow.map
import vectors.Vec2
import kotlin.math.min

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun Rotate3dController(
    modifier: Modifier,
    dropValueX: MutableFloatState,
    dropValueY: MutableFloatState,
    dropValueZ: MutableFloatState,
    onRotateDisplay: () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val starPosition = remember { mutableStateOf(Vec2.Zero) }
        val selectedType = remember { mutableIntStateOf(0) }
        val startX = remember { mutableStateOf(dropValueX.value) }
        val startY = remember { mutableStateOf(dropValueY.value) }
        val startZ = remember { mutableStateOf(dropValueZ.value) }
        val isMoved = remember { mutableStateOf(false) }
        Canvas(modifier = Modifier.size(64.dp).clickable(
            interactionSource = interactionSource,
            indication = ripple(
                bounded = false,
                radius = 32.dp
            ),
            enabled = true,
            role = Role.Button,
            onClick = { },
        ).onPointerEvent(PointerEventType.Press) {
            isMoved.value = false
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
            startX.value = dropValueX.floatValue
            startY.value = dropValueY.floatValue
            startZ.value = dropValueZ.floatValue - Math.toDegrees(pos.angle).toFloat()
        }.onPointerEvent(PointerEventType.Release) {
            selectedType.value = 0
            if (!isMoved.value){
                dropValueX.value = 0f
                dropValueY.value = 0f
                dropValueZ.value = 0f
                onRotateDisplay()
            }
        }.onPointerEvent(PointerEventType.Move) {
            val sz = this.size
            val posi = it.changes.first().position.toVec2()
            val pos = posi - starPosition.value
            isMoved.value = true

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
            this.drawCircle(ThemeColors.controllerBackground, radius = s/2, style = Fill)

            withTransform({
                val m = Matrix()
                m.rotateX(dropValueX.floatValue)
                m.rotateY(dropValueY.floatValue)
                m.rotateZ(dropValueZ.floatValue)
                translate(size.center.x, size.center.y)
                transform(m)
            }) {
                this.drawCircle(Color.Yellow, center = Offset.Zero, radius = r, style = stroke)
                if (dropValueY.floatValue == 0.0f && dropValueX.floatValue == 0.0f) {
                    this.drawLine(
                        Color.Red,
                        start = Offset(-r, 0f),
                        end = Offset(r, 0f),
                        strokeWidth = stroke.width
                    )

                    this.drawLine(
                        Color.Green ,
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
                    this.drawCircle(Color.Green, center = Offset.Zero, radius = r, style = stroke)
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