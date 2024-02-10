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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import com.kos.boxdrawe.drawer.drawFigures
import com.kos.boxdrawe.presentation.BezierData
import com.kos.boxdrawe.widget.toOffset
import com.kos.boxdrawe.widget.toVec2
import vectors.Vec2


@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun DisplayBezier(displayScale: MutableFloatState, vm: BezierData) {

    val figure = remember { vm.figure }
    val c1 = vm.c1.collectAsState()
    val cst = vm.startActionPos.collectAsState(Vec2.Zero)
    val cen = vm.endActionPos.collectAsState(Vec2.Zero)
    val currentDistance = remember { vm.currentDistance }

    val selectIndex = remember { mutableIntStateOf(-1) }

    var rotation by rememberSaveable("DisplayTortoiseRotation") { mutableStateOf(0f) }
    var pos by rememberSaveable("DisplayTortoiseOffset") { mutableStateOf(Offset.Zero) }

    val scale = displayScale.value

    Canvas(modifier = Modifier.fillMaxSize().clipToBounds().onPointerEvent(PointerEventType.Press) {
        val sp = (it.changes.first().position.toVec2()- size.toVec2() / 2.0)/scale.toDouble()-pos.toVec2()/scale.toDouble()
        if (it.button == PointerButton.Primary) {
            selectIndex.value = c1.value.indexOfFirst { Vec2.distance(it, sp) < 20.0 }
        }
        if (selectIndex.value < 0){
            if (Vec2.distance(cst.value, sp) < 20.0){
                if (it.button == PointerButton.Secondary)
                    vm.deleteStart()
                else
                    vm.addStartBezier()
            }
            if (Vec2.distance(cen.value, sp) < 20.0){
                if (it.button == PointerButton.Secondary)
                    vm.deleteEnd()
                else
                    vm.addEndBezier()
            }
        }
    }.onPointerEvent(PointerEventType.Release) {
        selectIndex.value = -1
    }.onPointerEvent(PointerEventType.Exit) {
        selectIndex.value = -1
    }.onPointerEvent(PointerEventType.Move) {

        if (it.changes.first().pressed) {
            val sp = Vec2.freqency((it.changes.first().position.toVec2() - size.toVec2() / 2.0)/scale.toDouble()-pos.toVec2()/scale.toDouble(),currentDistance.value)
            val v = selectIndex.value
            if (v >= 0) {
                vm.movePoint(v, sp)
            }
        }

    }.onDrag(
        matcher = PointerMatcher.mouse(PointerButton.Secondary) + PointerMatcher.mouse(PointerButton.Tertiary),
        onDrag = { offset ->
            pos += offset //  (offset*2.0f)/scale
        }
    )
    ) {
        val c = size / 2f
        val razm = c / 20f

        translate(pos.x, pos.y) {
            this.scale(scale = displayScale.value) {
                this.translate(c.width, c.height) {
                    for (j in -razm.width.toInt()..razm.width.toInt()) {
                        for (k in -razm.height.toInt()..razm.height.toInt()) {
                            this.drawCircle(
                                color = Color.Yellow,
                                radius = 0.5f / scale,
                                center = Offset(j * 20f, k * 20f)
                            )
                        }
                    }

                    c1.value.let { bezier ->

                        bezier.forEachIndexed { i, c ->
                            val color = if (selectIndex.value == i) Color.Green else Color.LightGray
                            this.drawCircle(color, radius = 5f / scale, center = c.toOffset())
                        }

                        this.drawCircle(Color.Yellow, 5f / scale, cst.value.toOffset())
                        this.drawCircle(Color.Yellow, 5f / scale, cen.value.toOffset())

                        this.drawPoints(
                            bezier.map { it.toOffset() },
                            PointMode.Polygon,
                            Color.DarkGray
                        )
                    }

                    this.drawFigures(figure.value)
                }
            }
        }
    }
}