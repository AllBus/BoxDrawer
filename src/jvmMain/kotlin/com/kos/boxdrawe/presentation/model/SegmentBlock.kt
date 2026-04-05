package com.kos.boxdrawe.presentation.model

import com.kos.figure.segments.model.EmptyPath
import com.kos.figure.segments.model.PathElement
import vectors.Matrix

data class SegmentBlock(
    val element: PathElement,
    val matrix: Matrix = Matrix.identity,
    val modifiers: List<BlockModifier> = emptyList(),
    val children: List<SegmentBlock> = emptyList(), // Добавляем поддержку дочерних блоков
) {
    val isGroup: Boolean get() = children.isNotEmpty()

    companion object {
        val EMPTY = SegmentBlock(EmptyPath, Matrix.identity, emptyList())
    }
}