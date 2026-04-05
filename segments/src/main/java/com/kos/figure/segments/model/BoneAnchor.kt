package com.kos.figure.segments.model

import vectors.Vec2

class BoneAnchor(
    val name: String,
    val coordinate: Vec2
) {
    companion object {
        val Empty = BoneAnchor("", Vec2.Zero)
    }
}