package com.kos.boxdrawe.widget

import LocalResourceLoader
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kos.boxdrawe.drawer.ComposeFigureDrawer
import com.kos.boxdrawe.drawer.drawFigures
import com.kos.boxdrawe.presentation.DrawerViewModel
import figure.IFigure
import java.nio.file.Path


@Composable
fun DisplayGrid() {

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DisplayBox(previewPage: Path?) {
    previewPage?.let { path ->
        Image(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
            painter = painterResource(
                resourcePath = path.toString(),
                loader = LocalResourceLoader()
            ),
            contentDescription = "Page preview"
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun DisplayTortoise(displayScale: Float, figures: IFigure) {
    val posX = rememberSaveable("DisplayTortoiseX") { mutableStateOf(0f) }
    val posY = rememberSaveable("DisplayTortoiseY") { mutableStateOf(0f) }

    Canvas(modifier = Modifier.fillMaxSize().clipToBounds().onDrag(
        onDrag = {  offset ->
            posX.value = posX.value +offset.x
            posY.value = posY.value +offset.y
        }
    ),
        onDraw = {
            val c = size / 2f
            this.translate(posX.value, posY.value) {
                this.scale(scale = Math.log(displayScale.toDouble()).toFloat()) {
                    this.translate(c.width + posX.value, c.height + posY.value) {
                        this.drawFigures(figures)

                    }
                }
            }

        })
}