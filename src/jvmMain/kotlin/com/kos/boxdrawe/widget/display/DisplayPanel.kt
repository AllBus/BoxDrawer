package com.kos.boxdrawe.widget.display

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.isTypedEvent
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.input.KeyboardType
import com.kos.boxdrawe.widget.EditTextField
import com.kos.boxdrawer.detal.grid.CadGrid


private val colorList = listOf(
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisplayGrid(grid: CadGrid) {
    val posX = rememberSaveable("DisplayGridX") { mutableStateOf(0f) }
    val posY = rememberSaveable("DisplayGridY") { mutableStateOf(0f) }
    val requester = remember { FocusRequester() }

    val redrawEvent = remember { mutableStateOf(1) }

    Box(
        modifier = Modifier.fillMaxSize()
            .pointerInput(key1 = true) {
                detectTapGestures(onPress = {
                    requester.requestFocus()
                })
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
                            grid.currentColor = grid.colorAt(grid.currentX, grid.currentY);
                        Key.F ->
                            grid.fillColor(grid.currentX, grid.currentY, grid.currentColor);
                        Key.A ->
                            grid.shiftX += 1;
                        Key.D ->
                            grid.shiftX = Math.max(0, grid.shiftX - 1);
                        Key.W ->
                            grid.shiftY += 1;
                        Key.S ->
                            grid.shiftY = Math.max(0, grid.shiftY - 1);
                        else -> {}
                    }


                    if (k.isShiftPressed) {
                        grid.setColor(grid.currentX, grid.currentY, grid.currentColor)
                    }

                    redrawEvent.value = redrawEvent.value + 1
                }
                true
            }

    )

    Canvas(modifier = Modifier.fillMaxSize().clipToBounds(),
        onDraw = {
            val c = size

            val d = Math.min(c.width * 1.0f / (grid.width + 2), c.height * 1.0f / (grid.height + 2))
            val style = Fill
            val selectStyle = Stroke(3f)
            val rectSize = Size(d, d)
            this.translate(posX.value + d, posY.value + d) {
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
            }
        }
    )

    LaunchedEffect(Unit) {
        requester.requestFocus()
    }
}

