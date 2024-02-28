package com.kos.boxdrawe.widget.display

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import com.kos.boxdrawe.drawer.drawFigures
import com.kos.figure.IFigure
import kotlin.math.exp
import kotlin.math.sign

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun DisplayTortoise(displayScale: MutableFloatState, matrix: State<Matrix>, enableMatrix: Boolean, figures: IFigure) {
//    val posX = rememberSaveable("DisplayTortoiseX") { mutableStateOf(0f) }
//    val posY = rememberSaveable("DisplayTortoiseY") { mutableStateOf(0f) }

    var rotation by rememberSaveable("DisplayTortoiseRotation") { mutableStateOf(0f) }
    var pos by rememberSaveable("DisplayTortoiseOffset") { mutableStateOf(Offset.Zero) }


//    val state = rememberTransformableState {
//            zoomChange, offsetChange, rotationChange ->
//            scale *= zoomChange
//            rotation += rotationChange
//            pos += offsetChange
//    }
    val m = Matrix()
    m.rotateY(45f)
    m.rotateX(30f)
    //var currentSize =

    Canvas(modifier = Modifier.fillMaxSize().clipToBounds().onPointerEvent(PointerEventType.Scroll) {

        val change = it.changes.first()
        val delta = change.scrollDelta.y.toInt().sign
        val p = change.position
        val predScale = displayScale.value

        //  pos+= (Offset(size.width/2f, -size.height/2f) - Offset(p.x, -p.y)) * scale / predScale
        displayScale.value = scale(displayScale.value, delta)
    }.onDrag(
        matcher = PointerMatcher.Primary + PointerMatcher.mouse(PointerButton.Secondary) + PointerMatcher.mouse(PointerButton.Tertiary) + PointerMatcher.stylus,
        onDrag = { offset ->
            pos += offset //  (offset*2.0f)/scale
        }
    ),
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
                    this.drawFigures(figures)
                }
        })
}

fun scale(value: Float, delta: Int): Float {
    return (value * exp(delta * 0.2f)).coerceIn(0.02f, 50f)
}