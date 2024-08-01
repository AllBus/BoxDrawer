package com.kos.boxdrawer.presentation.display

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import com.kos.boxdrawe.drawer.drawFigures
import com.kos.boxdrawe.widget.toOffset
import com.kos.boxdrawe.widget.toVec2
import com.kos.figure.FigureInfo
import com.kos.figure.IFigure
import vectors.Vec2
import kotlin.math.exp
import kotlin.math.sign

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun DisplayTortoise(
    displayScale: MutableFloatState,
    pos: MutableState<Offset>,
    matrix: State<Matrix>,
    enableMatrix: Boolean,
    figures: IFigure,
    selectedItem: State<List<FigureInfo>>,
    onStateChange: (String) -> Unit,
    onPress: (Vec2, Int, Float) -> Unit
) {

//    var pos by rememberSaveable("DisplayTortoiseOffset") { mutableStateOf(Offset.Zero) }
    val displaySize = remember { mutableStateOf(IntSize(0, 0)) }
    val measurer = rememberTextMeasurer()

    val selectedType = remember { mutableIntStateOf(0) }

    val scale = displayScale.value

    Canvas(modifier = Modifier.fillMaxSize().clipToBounds()
        .onPointerEvent(PointerEventType.Press) {

            val posi = it.changes.first().position.toVec2()
            val sp = coordAtPointer(pos.value, scale, posi)

            val pt = when (it.button) {
                PointerButton.Primary -> 1
                PointerButton.Secondary -> 2
                PointerButton.Tertiary -> 3
                else -> 0
            }
            selectedType.value = pt
            onPress(sp, pt, scale)


        }.onPointerEvent(PointerEventType.Release) {
            selectedType.value = 0
        }
        .onPointerEvent(PointerEventType.Scroll) {
            val change = it.changes.first()
            val delta = change.scrollDelta.y.toInt().sign

            val posi = change.position.toVec2()
            val sp = coordAtPointer(pos.value, scale, posi)

            val predScale = displayScale.value.toDouble()
            val newScale = scale(delta).toDouble()
            val resScale = predScale * newScale
            if (resScale in 0.01..2000.0) {
                val center = -pos.value.toVec2()
                val pv = (sp - center) / newScale
                pos.value = -(sp - pv).toOffset()
                displayScale.value = resScale.toFloat()
            }
        }.onPointerEvent(PointerEventType.Move) {
            val posi = it.changes.first().position.toVec2()
            val sp = coordAtPointer(pos.value, scale, posi)
            val ds = displayScale.value.toDouble()
            val dz = displaySize.value.toVec2() / ds
            onStateChange(
                String.format(
                    "(%.4f : %.4f) x (%.4f : %.4f)",
                    sp.x,
                    sp.y,
                    dz.x,
                    dz.y
                )
            )
        }.onDrag(
            matcher = PointerMatcher.Primary + PointerMatcher.mouse(PointerButton.Secondary) + PointerMatcher.mouse(
                PointerButton.Tertiary
            ) + PointerMatcher.stylus,
            onDrag = { offset ->
                pos.value += offset / displayScale.value
            }
        ).onSizeChanged { s ->
            displaySize.value = s
            val ds = displayScale.value.toDouble()
            val dz = displaySize.value.toVec2() / ds
            onStateChange(
                String.format(
                    "(%.4f : %.4f) x (%.4f : %.4f)",
                    pos.value.x,
                    pos.value.y,
                    dz.x,
                    dz.y
                )
            )
        },
        onDraw = {
            val c = size / 2f
            this.withTransform(
                {
                    val s = displayScale.value
                    val x = pos.value.x * s
                    val y = pos.value.y * s
                    translate(x, y)
                    scale(scale = s)
                    translate(c.width, c.height)
                    if (enableMatrix) {
                        transform(matrix.value)
                    }
                }
            ) {
                this.drawFigures(figures,  selectedItem.value, measurer)
            }
        })

}

private fun AwaitPointerEventScope.coordAtPointer(
    translate: Offset,
    scale: Float,
    pointerPosition: Vec2
): Vec2 {
    return (-translate.toVec2() + (pointerPosition - size.toVec2() / 2.0) / scale.toDouble())
}

fun scale(delta: Int): Float {
    return exp(delta * 0.2f) // (value * exp(delta * 0.2f)).coerceIn(0.02f, 50f)
}