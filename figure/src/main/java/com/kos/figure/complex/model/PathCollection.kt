package com.kos.figure.complex.model

import vectors.Vec2
import java.nio.file.Path

interface PathIterator {
    val size: Int
    operator fun get(index: Int): PathElement
}

class SegmentList(points: List<Vec2>) : PathIterator {
    override val size: Int = points.size-1

    private val item = SegmentIter(points, 0)

    override fun get(index: Int): PathElement {
        item.index = index
        return item
    }
}

class CurveList(points: List<Vec2>) : PathIterator {
    override val size: Int = (points.size-1) / 3

    private val item = CurveIter(points, 0)

    override fun get(index: Int): PathElement {
        item.index = index*3
        return item
    }
}

class CustomPathIterator(val elements: List<PathElement>) : PathIterator{
    override val size: Int
        get() = elements.size

    override fun get(index: Int): PathElement {
        return elements[index]
    }

}