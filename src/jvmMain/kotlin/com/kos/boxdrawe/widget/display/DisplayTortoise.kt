package com.kos.boxdrawe.widget.display

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isAltPressed
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.kos.boxdrawe.drawer.drawFigures
import com.kos.boxdrawe.widget.toVec2
import com.kos.figure.IFigure
import vectors.Vec2
import kotlin.math.exp
import kotlin.math.sign

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun DisplayTortoise(displayScale: MutableFloatState,
                    matrix: State<Matrix>,
                    enableMatrix: Boolean,
                    figures: IFigure,
                    selectedItem: State<IFigure>,
                    onStateChange: (String)-> Unit,
                    onPress: (Vec2, Int, Float) -> Unit) {

    var pos by rememberSaveable("DisplayTortoiseOffset") { mutableStateOf(Offset.Zero) }
    val displaySize = remember { mutableStateOf(IntSize(0,0)) }


    val selectedType = remember { mutableIntStateOf(0) }

    val m = Matrix()
    m.rotateY(45f)
    m.rotateX(30f)

    LaunchedEffect(pos, displayScale){
        val ds = displayScale.value.toDouble()
        val dz = displaySize.value.toVec2() / ds
        onStateChange(String.format("(%.4f : %.4f) x (%.4f : %.4f)", pos.x/ds, pos.y/ds , dz.x, dz.y))
    }

    val scale = displayScale.value

    Canvas(modifier = Modifier.fillMaxSize().clipToBounds()
        .onPointerEvent(PointerEventType.Press) {
            val sp =
                (it.changes.first().position.toVec2() - size.toVec2() / 2.0) / scale.toDouble() - pos.toVec2() / scale.toDouble()

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
        val p = change.position
        val predScale = displayScale.value
        val newScale =  scale(displayScale.value, delta)
        //val position = change.position
        val s = size.toVec2()/2.0/ predScale.toDouble()
        val s2 = size.toVec2()/2.0/ newScale.toDouble()
        val s3 = s2-s
        pos += Offset( s3.x.toFloat(), s3.y.toFloat())

        displayScale.value = newScale
    }.onDrag(
        matcher = PointerMatcher.Primary + PointerMatcher.mouse(PointerButton.Secondary) + PointerMatcher.mouse(PointerButton.Tertiary) + PointerMatcher.stylus,
        onDrag = { offset ->
            pos += offset
        }
    ).onSizeChanged { s ->
        displaySize.value = s
        val ds = displayScale.value.toDouble()
        val dz = displaySize.value.toVec2() / ds
        onStateChange( String.format("(%.4f : %.4f) x (%.4f : %.4f)", pos.x/ds, pos.y/ds , dz.x, dz.y))
    },
        onDraw = {
            val c = size / 2f
                this.withTransform(
                   {
                       translate(pos.x, pos.y)
                       scale(scale = displayScale.value)
                       translate(c.width, c.height)
                       if (enableMatrix) {
                           transform(matrix.value)
                       }
                    }
                ) {
                    this.drawFigures(figures, selectedItem.value)

                }
        })

}

fun scale(value: Float, delta: Int): Float {
    return (value * exp(delta * 0.2f)).coerceIn(0.02f, 50f)
}