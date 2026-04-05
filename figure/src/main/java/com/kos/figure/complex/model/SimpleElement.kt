package com.kos.figure.complex.model

import com.kos.figure.segments.model.PathIterator

interface SimpleElement {
    fun segments(): PathIterator
}