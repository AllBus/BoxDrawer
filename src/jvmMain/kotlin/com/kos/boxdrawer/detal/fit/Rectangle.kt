package com.kos.boxdrawer.detal.fit

import vectors.Vec2
import kotlin.math.cos
import kotlin.math.sin

data class Rectangle(val width: Double, val height: Double) {
    // Add helper functions for area, bottom-left corner, etc. if needed
}

data class Rectangle4(val x: Double, val y: Double, val width: Double, val height: Double)

data class ConvexPolygon(val vertices: List<Vec2>) {
    // Rotates the polygon by the given angle (in radians) around the origin (0, 0)
    fun rotate(angle: Double): ConvexPolygon {
        val rotatedVertices = vertices.map { vertex ->
            Vec2(
                vertex.x * cos(angle) - vertex.y * sin(angle),
                vertex.x * sin(angle) + vertex.y * cos(angle)
            )
        }
        return ConvexPolygon(rotatedVertices)
    }

    // Translates the polygon by the given vector
    fun translate(vector: Vec2): ConvexPolygon {
        val translatedVertices = vertices.map { vertex ->
            Vec2(vertex.x + vector.x, vertex.y + vector.y)
        }
        return ConvexPolygon(translatedVertices)
    }
}

// Placement data class to store position and rotation
data class Placement(val position: Vec2, val rotation: Double)