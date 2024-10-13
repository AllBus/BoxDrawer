package com.kos.boxdrawer.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.kos.boxdrawe.icons.IconCopy
import com.kos.boxdrawe.presentation.ImageToolsData

fun parseCoordinates(coordsString: String): List<Offset> {
    return coordsString.split(", ")
        .chunked(2)
        .map { (x, y) -> Offset(x.toFloat(), y.toFloat()) }
}

@Composable
fun DisplayImageProcessing(imageData: ImageToolsData) {
    val src = imageData.sourceBitmap.collectAsState()
    val cur = imageData.currentBitmap.collectAsState(null)
    val srcCoords = remember { mutableStateListOf<Offset>() }
    val curCoords = remember { mutableStateListOf<Offset>() }
    val clipboardManager = LocalClipboardManager.current

    val initialCoords = parseCoordinates("0.4628016, 0.5003351, 0.4366622, 0.52446383, 0.24765415, 0.5867962, 0.16119303, 0.6069035, 0.06668901, 0.6069035, 0.0023458444, 0.5546247, 0.026474532, 0.5003351")

    val transformedCoords = remember(initialCoords) {
        val firstX = initialCoords.first().x
        val firstY = initialCoords.first().y
        initialCoords.map { offset ->
            Offset( -(offset.x - firstX),  (offset.y - firstY)) // Move and flip
        }
    }


    Row(modifier = Modifier.padding(8.dp)) {
        Button(onClick = {
            val text = transformedCoords.joinToString(", ") { "${it.x}, ${it.y}" }
            clipboardManager.setText(AnnotatedString(text))
        }) {
            Icon(
                imageVector = IconCopy.rememberContentCopy(),
                contentDescription = "Copy Source Coordinates"
            )
        }
        Button(onClick= { srcCoords.clear() }) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Clear Source Coordinates"
            )
        }

        Button(onClick = {
            val text = curCoords.joinToString(", ") { "${it.x}, ${it.y}" }
            clipboardManager.setText(AnnotatedString(text))
        }) {
            Icon(
                imageVector = IconCopy.rememberContentCopy(),
                contentDescription = "Copy Current Coordinates"
            )
        }

        Button(onClick = { curCoords.clear() }) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Clear Current Coordinates"
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        ImageItem(bitmap = src.value,
            coords= srcCoords,
            modifier = Modifier.weight(1f),

            onImageClick = { offset -> srcCoords.add(offset) },
            onRightClick =  { if (srcCoords.isNotEmpty()) srcCoords.removeLast() }
        )
        ImageItem(bitmap = cur.value,
            coords = curCoords,
            modifier = Modifier.weight(1f),
            onImageClick = { offset -> curCoords.add(offset)},
            onRightClick = { if (curCoords.isNotEmpty()) curCoords.removeLast() }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ImageItem(
    bitmap: androidx.compose.ui.graphics.ImageBitmap?,
    coords: List<Offset>,
    modifier: Modifier = Modifier,
    onImageClick: (Offset) -> Unit = {},
    onRightClick: () -> Unit = {} // Added onRightClick
) {

    if (bitmap != null) {
        var imageSize by remember { mutableStateOf(IntSize.Zero) }
        var imagePosition by remember { mutableStateOf(Offset.Zero) }
        Box(modifier = modifier.fillMaxSize()) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(bitmap.width.toFloat() / bitmap.height.toFloat())
                    .clip(RectangleShape)
                    .onPointerEvent(PointerEventType.Press) { event ->
                        if (event.buttons.isSecondaryPressed) {
                            onRightClick()
                        } else {
                            // Calculate relative coordinates
                            val imageWidth = imageSize.width.toFloat()
                            val imageHeight = imageSize.height.toFloat()
                            val offsetX = event.changes.first().position.x
                            val offsetY = event.changes.first().position.y
                            val relativeX = offsetX / imageWidth
                            val relativeY = offsetY / imageHeight
                            onImageClick(Offset(relativeX, relativeY))
                        }
                    }
                    .onGloballyPositioned { coordinates ->
                        imageSize = coordinates.size
                        imagePosition = coordinates.positionInParent()
                    },
                contentScale = ContentScale.Fit
            )
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                val color = Color.Red.copy(0.8f)
                coords.forEach{ offset ->
                    // Convert relative coordinates to canvas coordinates
                    val canvasX = offset.x * imageSize.width+imagePosition.x
                    val canvasY = offset.y * imageSize.height+imagePosition.y
                    drawCircle(
                        color = color,
                        center = Offset(canvasX, canvasY),
                        radius = 10f
                    )
                }
            }
        }
    } else {
        Box(modifier = modifier)
    }
}

//fun DisplayImageProcessing(imageData: ImageToolsData) {
//    val src = imageData.sourceBitmap.collectAsState()
//    val cur = imageData.currentBitmap.collectAsState(null)
//    Row(modifier = Modifier.fillMaxSize().clipToBounds()){
//        val src1= src.value
//        if (src1!=null) {
//            Image(bitmap = src1, contentDescription = null, modifier = Modifier.weight(1f))
//        }else{
//            Box(modifier = Modifier.weight(1f))
//        }
//        val cur1= cur.value
//        if (cur1!=null) {
//            Image(bitmap = cur1, contentDescription = null, modifier = Modifier.weight(1f))
//        }else{
//            Box(modifier = Modifier.weight(1f))
//        }
//    }
//  //  val imageBitmap =  bufferedImageToImageBitmap(imageData.sourceImage.value)
//  //
//}

