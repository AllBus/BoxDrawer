package com.kos.boxdrawer.presentation.display

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.drawer.drawFigures
import com.kos.boxdrawe.presentation.InstrumentState
import com.kos.boxdrawe.presentation.Instruments
import com.kos.boxdrawe.presentation.Instruments.POINTER_LEFT
import com.kos.boxdrawe.presentation.Instruments.POINTER_MIDDLE
import com.kos.boxdrawe.presentation.Instruments.POINTER_NONE
import com.kos.boxdrawe.presentation.Instruments.POINTER_RIGHT
import com.kos.boxdrawe.widget.toOffset
import com.kos.boxdrawe.widget.toVec2
import com.kos.boxdrawer.presentation.ZoomUtils
import com.kos.boxdrawer.presentation.model.ImageMap
import com.kos.figure.FigureInfo
import com.kos.figure.IFigure
import vectors.Vec2
import kotlin.math.sign

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun DisplayDxf(
    displayScale: MutableFloatState,
    pos: MutableState<Offset>,
    matrix: State<Matrix>,
    enableMatrix: Boolean,
    figures: IFigure,
    images: ImageMap,
    selectedItem: State<List<FigureInfo>>,
    onStateChange: (String) -> Unit,
    onPress: (Vec2, Int, Float) -> Unit,
    onMove: (Vec2, Int, Float) -> Unit,
    onRelease: (Vec2, Int, Float) -> Unit,
    instrument: State<InstrumentState>  = remember { mutableStateOf(Instruments.NONE) },
) {

//    var pos by rememberSaveable("DisplayTortoiseOffset") { mutableStateOf(Offset.Zero) }
    val displaySize = remember { mutableStateOf(IntSize(0, 0)) }
    val measurer = rememberTextMeasurer()

    val selectedType = remember { mutableIntStateOf(0) }

    val scale = displayScale.value

    val points = remember { mutableStateOf(listOf<Vec2>()) }

    Canvas(modifier = Modifier.fillMaxSize().clipToBounds()
        .onPointerEvent(PointerEventType.Press) { event ->

            val posi = event.changes.first().position.toVec2()
            val sp = coordAtPointer(pos.value, scale, posi)

            val pt = pointerButtonToId(event)
            selectedType.value = pt
            onPress(sp, pt, scale)


        }.onPointerEvent(PointerEventType.Release) { event ->
            selectedType.value = 0

            val posi = event.changes.first().position.toVec2()
            val sp = coordAtPointer(pos.value, scale, posi)
            val pt = pointerButtonToId(event)
            onRelease(sp,pt, scale)
        }
        .onPointerEvent(PointerEventType.Scroll) {
            val change = it.changes.first()
            val delta = change.scrollDelta.y.toInt().sign

            val posi = change.position.toVec2()
            val sp = coordAtPointer(pos.value, scale, posi)

            val predScale = displayScale.value.toDouble()
            // val newScale = scale(delta).toDouble()
            val resScale = ZoomUtils.scale(displayScale.value.toDouble(), delta)
                .toDouble()  // predScale * newScale
            val newScale = resScale / predScale
            if (resScale in 0.01..10000.0) {
                val center = -pos.value.toVec2()
                val pv = (sp - center) / newScale
                pos.value = -(sp - pv).toOffset()
                displayScale.value = resScale.toFloat()
            }
        }.onPointerEvent(PointerEventType.Move) { event->
            val posi = event.changes.first().position.toVec2()
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
            onMove(sp,  selectedType.value , scale)
        }.onDrag(
            matcher = PointerMatcher.mouse(PointerButton.Secondary) + PointerMatcher.mouse(
                PointerButton.Tertiary
            ),
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
                this.drawFigures(figures, selectedItem.value, measurer, images)
            }
        })
    Box(modifier = Modifier.padding(8.dp)){
        DrawInstrumentIcon(instrument.value.selectedInstrument)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun pointerButtonToId(event: PointerEvent): Int {
    val pt = when (event.button) {
        PointerButton.Primary -> POINTER_LEFT
        PointerButton.Secondary -> POINTER_RIGHT
        PointerButton.Tertiary -> POINTER_MIDDLE
        else -> POINTER_NONE
    }
    return pt
}

private fun AwaitPointerEventScope.coordAtPointer(
    translate: Offset,
    scale: Float,
    pointerPosition: Vec2
): Vec2 {
    return (-translate.toVec2() + (pointerPosition - size.toVec2() / 2.0) / scale.toDouble())
}

