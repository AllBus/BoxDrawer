package com.kos.boxdrawer.presentation.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap

@Immutable
class ImageMap(val images: Map<String, ImageBitmap>): Map<String, ImageBitmap> by images {
    companion object {
        val EMPTY = ImageMap(emptyMap())
    }
}