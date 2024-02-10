package com.kos.boxdrawe.widget

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import vectors.Vec2

fun Vec2.toOffset(): Offset = Offset(this.x.toFloat(), this.y.toFloat())

fun Offset.toVec2() = Vec2(this.x.toDouble(), this.y.toDouble())
fun IntSize.toVec2() = Vec2(this.width.toDouble(), this.height.toDouble())