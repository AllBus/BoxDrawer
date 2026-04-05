package com.kos.boxdrawe.presentation.model

import com.kos.figure.segments.model.EmptyPath
import com.kos.figure.segments.model.PathElement
import vectors.Matrix
import java.util.concurrent.atomic.AtomicLong

data class SegmentBlock(
    val element: PathElement,
    val matrix: Matrix = Matrix.identity,
    val modifiers: List<BlockModifier> = emptyList(),
    val children: List<SegmentBlock> = emptyList(),
    val id: Long = nextId(),
) {
    val isGroup: Boolean get() = children.isNotEmpty()
    fun isSame(other: SegmentBlock): Boolean {
        return id == other.id
    }


    companion object {
        private val idGenerator = AtomicLong(0)
        private fun nextId() = idGenerator.incrementAndGet()

        operator fun invoke(elements: List<PathElement>): SegmentBlock {
            return if (elements.size == 1) {
                SegmentBlock(
                    element = elements.first(),
                    matrix = Matrix.identity,
                    modifiers = emptyList(),
                )
            } else {
                SegmentBlock(
                    element = EmptyPath,
                    matrix = Matrix.identity,
                    modifiers = emptyList(),
                    children = elements.map { SegmentBlock(it) })
            }
        }

        val EMPTY = SegmentBlock(
            element = EmptyPath,
            matrix = Matrix.identity,
            modifiers = emptyList(),
            id = -1
        )
    }
}