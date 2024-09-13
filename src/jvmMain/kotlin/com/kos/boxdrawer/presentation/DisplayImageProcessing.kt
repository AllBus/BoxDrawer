package com.kos.boxdrawer.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.loadImageBitmap
import com.kos.boxdrawe.presentation.ImageToolsData
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@Composable
fun DisplayImageProcessing(imageData: ImageToolsData) {
    val src = imageData.sourceBitmap.collectAsState()
    val cur = imageData.currentBitmap.collectAsState(null)
    Row(modifier = Modifier.fillMaxSize().clipToBounds()){
        val src1= src.value
        if (src1!=null) {
            Image(bitmap = src1, contentDescription = null, modifier = Modifier.weight(1f))
        }else{
            Box(modifier = Modifier.weight(1f))
        }
        val cur1= cur.value
        if (cur1!=null) {
            Image(bitmap = cur1, contentDescription = null, modifier = Modifier.weight(1f))
        }else{
            Box(modifier = Modifier.weight(1f))
        }
    }
  //  val imageBitmap =  bufferedImageToImageBitmap(imageData.sourceImage.value)
  //
}

