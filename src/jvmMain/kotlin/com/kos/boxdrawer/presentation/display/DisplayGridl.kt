package com.kos.boxdrawer.presentation.display

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.drawer.drawFigures
import com.kos.boxdrawe.presentation.GridData
import com.kos.boxdrawe.widget.toVec2
import com.kos.boxdrawer.detal.grid.Coordinates
import com.kos.boxdrawer.detal.grid.Grid3DVisualizer
import com.kos.boxdrawer.detal.grid.Kubik
import com.kos.boxdrawer.presentation.Rotate3dController
import com.kos.figure.FigureEmpty
import vectors.Vec2
import kotlin.math.sign


internal val colorList = listOf(
    Color(0xFFBA0000),
    Color(0xFFBC8F8F),
    Color(0xFFE6E6Fa),
    Color(0xFFFF7F50),
    Color(0xFF8B008B),
    Color(0xFFFFA500),
    Color(0xFFADFF2F),
    Color(0xFF006400),
    Color(0xFF40E0D0),
    Color(0xFFFFFF00),
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun DisplayGrid(gridData: GridData) {
    val requester = remember { FocusRequester() }
    val edges = gridData.gridEdges.collectAsState(emptyList())

    val grid = gridData.cad

    val redrawEvent = gridData.redrawEvent.collectAsState()
    val measurer = rememberTextMeasurer()

    val scale = remember{1.0}
    var pos by rememberSaveable("DisplayyGridOffset") { mutableStateOf(Offset.Zero) }
    val figure = gridData.figure.collectAsState(FigureEmpty)

    val widthCell = remember { gridData.widthCell }
    val figurePreview = remember { gridData.figurePreview }

    val dropValueX = remember { mutableStateOf(0f) }
    val dropValueY = remember { mutableStateOf(0f) }
    val dropValueZ = remember { mutableStateOf(0f) }
    val grid3d = gridData.useGrid3d.collectAsState(false)

    Box(
        modifier = Modifier.fillMaxSize()
            .pointerInput(key1 = true) {
                detectTapGestures(onPress = {
                    requester.requestFocus()
                })
            }
            .onPointerEvent(PointerEventType.Scroll) {
                val change = it.changes.first()
                val delta = change.scrollDelta.y.toInt().sign
                grid.currentColor = (grid.currentColor + delta+10 )%10
                gridData.redraw()

            }
            .onPointerEvent(PointerEventType.Press) {
                if (grid3d.value){} else {
                    val c = size
                    val d = Math.min(
                        c.width * 1.0f / (grid.width + 2),
                        c.height * 1.0f / (grid.height + 2)
                    ).toDouble()

                    val sp =
                        (it.changes.first().position.toVec2()) / scale.toDouble() - (pos.toVec2() + Vec2(
                            d,
                            d
                        )) / scale.toDouble()

                    val xy = sp / d
                    grid.currentX = xy.x.toInt()
                    grid.currentY = xy.y.toInt()
                    grid.setColor(grid.currentX, grid.currentY, grid.currentColor)
                    gridData.redraw()
                }
            }
            .onPointerEvent(PointerEventType.Move) {
                if (grid3d.value){} else {
                    if (it.changes.first().pressed) {
                        val c = size
                        val d = Math.min(
                            c.width * 1.0f / (grid.width + 2),
                            c.height * 1.0f / (grid.height + 2)
                        ).toDouble()

                        val sp =
                            (it.changes.first().position.toVec2()) / scale.toDouble() - (pos.toVec2() + Vec2(
                                d,
                                d
                            )) / scale.toDouble()

                        val xy = sp / d
                        grid.currentX = xy.x.toInt()
                        grid.currentY = xy.y.toInt()
                        grid.setColor(grid.currentX, grid.currentY, grid.currentColor)
                        gridData.redraw()
                    }
                }
            }
            .focusRequester(requester)
            .focusable(true)
            .onKeyEvent { k ->
                if (k.type == KeyEventType.KeyDown) {
                    when (k.key) {
                        Key.DirectionLeft ->
                            grid.currentX = Math.max(grid.currentX - 1, 0);

                        Key.DirectionDown ->
                            grid.currentY = Math.min(grid.currentY + 1, grid.height - 1);
                        Key.DirectionRight ->
                            grid.currentX = Math.min(grid.currentX + 1, grid.width - 1);
                        Key.DirectionUp ->
                            grid.currentY = Math.max(grid.currentY - 1, 0);

                        Key.Q ->
                            grid.currentZ = Math.max(grid.currentZ - 1, -100);
                        Key.E ->
                            grid.currentZ = Math.min(grid.currentZ + 1, 100);
                        Key.One ->
                            grid.currentColor = 1;
                        Key.Two ->
                            grid.currentColor = 2;
                        Key.Three ->
                            grid.currentColor = 3;
                        Key.Four ->
                            grid.currentColor = 4;
                        Key.Five ->
                            grid.currentColor = 5;
                        Key.Six ->
                            grid.currentColor = 6;
                        Key.Seven ->
                            grid.currentColor = 7;
                        Key.Eight ->
                            grid.currentColor = 8;
                        Key.Nine ->
                            grid.currentColor = 9;
                        Key.Zero ->
                            grid.currentColor = 0;
                        Key.C ->
                            if (!grid3d.value) {
                                grid.currentColor = grid.colorAt(grid.currentX, grid.currentY)
                            }else{
                                grid.currentColor = gridData.grid[grid.currentX, grid.currentY, grid.currentZ]?.color?:0
                            }
                        Key.F ->
                            if (!grid3d.value) {
                                grid.fillColor(grid.currentX, grid.currentY, grid.currentColor);
                            }
                        Key.A ->
                            grid.currentX = Math.max(grid.currentX - 1, 0);
                        Key.D ->
                            grid.currentX = Math.min(grid.currentX + 1, grid.width - 1);
                        Key.W ->
                            grid.currentY = Math.max(grid.currentY - 1, 0);
                        Key.S ->
                            grid.currentY = Math.min(grid.currentY + 1, grid.height - 1);
                        else -> {}
                    }


                    if (k.isShiftPressed) {
                        if (grid3d.value) {
                            gridData.grid[grid.currentX, grid.currentY, grid.currentZ] = (if (grid.currentColor == 0) null else Kubik(grid.currentColor))
                        }else{
                            grid.setColor(grid.currentX, grid.currentY, grid.currentColor)
                        }
                    }

                    gridData.redraw()
                }
                true
            }


    ){
        if (grid3d.value) {
            Grid3DVisualizer(edges.value, dropValueX.value, dropValueY.value, dropValueZ.value, redrawEvent.value,
                Coordinates(grid.currentX, grid.currentY, grid.currentZ))
            val modifier = Modifier.align(Alignment.TopEnd).padding(end = 120.dp)
            Rotate3dController(modifier, dropValueX, dropValueY, dropValueZ, {})
        } else {
            Canvas(modifier = Modifier.fillMaxSize().clipToBounds(),
                onDraw = {
                    val c = size

                    val d = Math.min(c.width * 1.0f / (grid.width + 2), c.height * 1.0f / (grid.height + 2))
                    val style = Fill
                    val selectStyle = Stroke(3f)
                    val rectSize = Size(d, d)
                    this.translate(pos.x + d, pos.y + d) {
                        val k = redrawEvent.value
                        for (x in 0 until grid.width) {
                            for (y in 0 until grid.height) {
                                val cell = grid.colorAt(x, y)
                                val penColor = colorList[cell % colorList.size]
                                drawRect(penColor, Offset(x * d, y * d), rectSize, style = style)
                            }
                        }

                        val penColor = colorList[grid.currentColor % colorList.size]
                        drawRect(penColor, Offset(grid.currentX * d, grid.currentY * d), rectSize, style = selectStyle)

                        if (figurePreview.value) {
                            val sc = (d * 1f / widthCell.decimal).toFloat()
                            this.scale(
                                sc,
                                sc,
                                Offset(0.0f, 0.0f)
                            ) {
                                drawFigures(figure.value, emptyList(), measurer)
                            }

                        }
                    }

                }
            )
        }
    }


    LaunchedEffect(Unit) {
        requester.requestFocus()
    }
}

