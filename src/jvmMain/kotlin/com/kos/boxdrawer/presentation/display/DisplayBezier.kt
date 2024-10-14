package com.kos.boxdrawer.presentation.display

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isAltPressed
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.rememberTextMeasurer
import com.kos.boxdrawe.drawer.drawFigures
import com.kos.boxdrawe.presentation.BezierData
import com.kos.boxdrawe.themes.ThemeColors
import com.kos.boxdrawe.widget.toOffset
import com.kos.boxdrawe.widget.toVec2
import com.kos.boxdrawer.presentation.model.ImageMap
import vectors.Vec2
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private fun indexOfPoint(points: List<Vec2>, position: Vec2, maxDistance: Double): Int {
    var index = -1
    var dist = maxDistance + 1
    points.forEachIndexed { i, v ->
        val nd = Vec2.distance(v, position)
        if (nd < dist) {
            index = i
            dist = nd
        }
    }
    return index
}

private const val START_DOT_INDEX = -21
private const val END_DOT_INDEX = -22

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun DisplayBezier(
    displayScale: MutableFloatState,
    pos: MutableState<Offset>,
    vm: BezierData,
    onSetStateText: (String) -> Unit,
) {

    val figure = remember { vm.figure }
    val c1 = vm.c1.collectAsState()
    val cst = vm.startActionPos.collectAsState(Vec2.Zero)
    val cen = vm.endActionPos.collectAsState(Vec2.Zero)
    val currentDistance = remember { vm.currentDistance }
    val lastSelectedPoint = remember { vm.lastSelectedPoint }

    val selectIndex = remember { mutableIntStateOf(-1) }
    val selectedType = remember { mutableIntStateOf(0) }

    val measurer = rememberTextMeasurer()
    val scale = displayScale.value
    val mousePointIndex = remember { mutableIntStateOf(1) }

    LaunchedEffect(mousePointIndex.value) {
        val mpv =  mousePointIndex.value
        val helpText = when {
            mpv == START_DOT_INDEX ||
                    mousePointIndex.value == END_DOT_INDEX -> "При нажатии левой кнопкой происходит  добавление сегмента.\n" +
                    "При нажатии правой кнопкой происходит удаление сегмента."

            mpv < 0 -> ""
            mpv % 3 != 0 -> "Захватите левой кнопкой для перемещении точки.\n" +
                    "При нажатии средней кнопкой на белый кружочек сохраняется угол между соседникми точками.\n" +
                    "При нажатии правой кнопкой на белый кружочек становится развернутым на 180."

            mpv >= 0 -> "Захватите левой кнопкой для перемещении точки."
            else -> ""
        }
        onSetStateText(helpText)
    }

    val octagon = remember(scale) {
        val octagon = Path()
        val r = 10f/scale
        octagon.moveTo(r * sin(0.0).toFloat(), r * cos(0.0).toFloat())
        for (i in 1..8) {
            octagon.lineTo(
                (r * sin((45 * PI * i) / 180)).toFloat(),
                r * cos((45 * PI * i) / 180).toFloat()
            )
        }
        octagon.close()
        octagon
    }

    Canvas(modifier = Modifier.fillMaxSize().clipToBounds().onPointerEvent(PointerEventType.Press) {
        val sp =
            (it.changes.first().position.toVec2() - size.toVec2() / 2.0) / scale.toDouble() - pos.value.toVec2() / scale.toDouble()
        // if (it.button == PointerButton.Primary) {
        selectIndex.value = indexOfPoint(c1.value, sp, 20.0)
        lastSelectedPoint.value =  selectIndex.value
        /// }

        if (selectIndex.value < 0) {
            if (Vec2.distance(cst.value, sp) < 20.0) {
                if (it.button == PointerButton.Secondary)
                    vm.deleteStart()
                else
                    vm.addStartBezier()
            }
            if (Vec2.distance(cen.value, sp) < 20.0) {
                if (it.button == PointerButton.Secondary)
                    vm.deleteEnd()
                else
                    vm.addEndBezier()
            }
        }
        if (it.keyboardModifiers.isAltPressed) {
            selectedType.value = 3
            vm.movePointLine(selectIndex.value)
        } else
            if (it.keyboardModifiers.isCtrlPressed) {
                selectedType.value = 1
                vm.movePointLine(selectIndex.value)
            } else {
                when (it.button) {
                    PointerButton.Primary -> selectedType.value = 0
                    PointerButton.Secondary -> selectedType.value = 1
                    PointerButton.Tertiary -> selectedType.value = 2
                }
            }

    }.onPointerEvent(PointerEventType.Release) {
        selectIndex.value = -1
        selectedType.value = 0
    }.onPointerEvent(PointerEventType.Move) {
        val sp = Vec2.freqency(
            (it.changes.first().position.toVec2() - size.toVec2() / 2.0) / scale.toDouble() - pos.value.toVec2() / scale.toDouble(),
            currentDistance.value
        )

        val v = selectIndex.value
        if (Vec2.distance(cst.value, sp) < 20.0) {
            mousePointIndex.value = START_DOT_INDEX
        } else
            if (Vec2.distance(cen.value, sp) < 20.0) {
                mousePointIndex.value = END_DOT_INDEX
            } else {
                mousePointIndex.value = indexOfPoint(c1.value, sp, 20.0)
            }

        if (it.changes.first().pressed) {


            if (v >= 0) {
                when (selectedType.value) {
                    0 -> vm.movePoint(v, sp)
                    1 -> vm.movePointFlat(v, sp)
                    2 -> vm.movePointEdge(v, sp)
                }

            }
        }

    }.onDrag(
        matcher = PointerMatcher.mouse(PointerButton.Secondary) + PointerMatcher.mouse(
            PointerButton.Tertiary
        ) + PointerMatcher.stylus,
        onDrag = { offset ->
            if (selectIndex.value < 0)
                pos.value += offset
        }
    )
    ) {
        val c = size / 2f
        val razm = c / 20f

        this.withTransform(
            {
                translate(pos.value.x, pos.value.y)
                scale(scale = displayScale.value)
                translate(c.width, c.height)
            }) {

            val gm =
                20f * (if (scale > 100) 0.01f else if (scale > 10) 0.1f else if (scale < 0.5) 10f else 1f)
            for (j in -razm.width.toInt()..razm.width.toInt()) {
                for (k in -razm.height.toInt()..razm.height.toInt()) {
                    this.drawCircle(
                        color = Color.Yellow,
                        radius = 0.5f / scale,
                        center = Offset(j * gm, k * gm)
                    )
                }
            }

            c1.value.let { bezier ->

                bezier.forEachIndexed { i, c ->
                    val color = if (selectIndex.value == i) ThemeColors.bezierPointSelect else
                        if (i % 3 == 0) {
                            if (i == lastSelectedPoint.value)
                                ThemeColors.bezierSelectedPoint
                            else
                                ThemeColors.bezierPoint
                        } else ThemeColors.bezierPointTangent
                    this.drawCircle(color, radius = 10f / scale, center = c.toOffset())
                }

                withTransform(
                    {
                        translate(cst.value.x.toFloat(), cst.value.y.toFloat())

                    }
                ) {
                    this.drawPath(octagon, ThemeColors.bezierPointButton)
                }
                withTransform(
                    {
                        translate(cen.value.x.toFloat(), cen.value.y.toFloat())
                    }
                ) {
                    this.drawPath(octagon, ThemeColors.bezierPointButton)
                }

                this.drawPoints(
                    bezier.map { it.toOffset() },
                    PointMode.Polygon,
                    Color.DarkGray
                )
            }

            this.drawFigures(figure.value, emptyList(), measurer, ImageMap.EMPTY)
        }
    }
}