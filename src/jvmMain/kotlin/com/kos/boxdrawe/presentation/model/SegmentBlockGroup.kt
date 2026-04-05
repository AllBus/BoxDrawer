package com.kos.boxdrawe.presentation.model

import vectors.Matrix

class SegmentBlockGroup(
    val blocks: List<SegmentBlock>,
    val matrix: Matrix = Matrix.identity,
    val modifiers: List<BlockModifier> = emptyList(),
) {
}