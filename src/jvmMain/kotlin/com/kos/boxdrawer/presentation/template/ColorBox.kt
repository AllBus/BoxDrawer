package com.kos.boxdrawer.presentation.template

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.onClick
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import com.jsevy.jdxf.DXFColor

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ColorBox(modifier: Modifier = Modifier, onClick: (Int) -> Unit, onHover: (Int) -> Unit, onExit: () -> Unit) {
    for (i in 0 until 26) {
        Row(
            modifier.height(8.dp).onPointerEvent(PointerEventType.Exit) {
                onExit()
            }
        ) {
            for (j in 0 until 10) {
                val colorId = i * 10 + j
                val color = DXFColor.getRgbColor(colorId)
                Box(
                    modifier.weight(0.125f, true).fillMaxHeight().background(color = Color(color))
                        .onClick { onClick(colorId) }.onPointerEvent(
                        PointerEventType.Enter
                    ) {
                        onHover(colorId)
                    }
                )
            }
        }
    }
}