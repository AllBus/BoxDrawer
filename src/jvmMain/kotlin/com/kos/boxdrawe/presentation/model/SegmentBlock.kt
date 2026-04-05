package com.kos.boxdrawe.presentation.model

import com.kos.figure.segments.model.EmptyPath
import com.kos.figure.segments.model.PathElement
import vectors.Matrix

data class SegmentBlock(
    val element: PathElement,
    val matrix: Matrix,
    val modifier: BlockModifier,
) {
    companion object {
        val EMPTY = SegmentBlock(EmptyPath, Matrix(), BlockModifier(0))
    }
}