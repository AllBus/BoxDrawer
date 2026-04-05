package turtoise

import vectors.Vec2
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

object Geometry {
    fun otstup(
        pred: Vec2,
        cur: Vec2,
        next: Vec2,
        radius: Double
    ): Double {
        //Todo:
        val ang = Vec2.angle(next, cur, pred)
        val alpha = ang - Math.PI / 2
        val cs = cos(alpha)
        val drr = if (abs(cs) > 0.0001) {

            abs(radius * (1 - sin(alpha)) / cs)
        } else 0.0
        return drr
    }

    fun center(
        pred: Vec2,
        cur: Vec2,
        next: Vec2,
        radius: Double,
    ): Vec2? {
        //Todo:
        val a = Vec2.angle(pred, cur, next)
        val m = if (a>Math.PI*2) 1.0 else -1.0

        val normA = Vec2.normal(pred, cur)*radius*m
        val normB = Vec2.normal(cur, next)*radius*m

        return Vec2.intersection(pred+normA, cur+normA, cur+normB, next+normB)
    }
}