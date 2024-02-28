package com.kos.boxdrawer.detal.bublik

import androidx.compose.ui.graphics.Matrix

class Vec3(val x: Double, val y:Double, val z:Double) {
}

operator fun Matrix.times(col: Vec3): Vec3{
    return Vec3(
        this[0, 0]*col.x+this[0, 1]*col.y+this[0, 2]*col.z,
        this[1, 0]*col.x+this[1, 1]*col.y+this[1, 2]*col.z,
        this[2, 0]*col.x+this[2, 1]*col.y+this[2, 2]*col.z,
    )
}