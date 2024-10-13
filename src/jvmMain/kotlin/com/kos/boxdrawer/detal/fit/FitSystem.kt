package com.kos.boxdrawer.detal.fit

import vectors.Vec2

object FitSystem {

    fun packRectangles(rectangles: List<Rectangle>): Rectangle {
        val sortedRectangles = rectangles.sortedByDescending { it.width * it.height }
        var boundingBox = Rectangle(0.0, 0.0)
        val placements = mutableMapOf<Rectangle, Vec2>()

        for (rect in sortedRectangles) {
            var bestPosition = Vec2(0.0, 0.0)
            var bestBoundingBox = boundingBox// Try different positions along the existing bounding box edges
            for ((placedRect, placedPos) in placements) {
                val rightPos = Vec2(placedPos.x + placedRect.width, placedPos.y)
                val topPos = Vec2(placedPos.x, placedPos.y + placedRect.height)

                // Check if rectangle fits without overlap and update bounding box
                if (fits(rect, rightPos, placements) &&
                    area(boundingBox(placements + (rect to rightPos))) < area(bestBoundingBox)) {
                    bestPosition = rightPos
                    bestBoundingBox = boundingBox(placements + (rect to rightPos))
                }
                if (fits(rect, topPos, placements) &&
                    area(boundingBox(placements + (rect to topPos))) < area(bestBoundingBox)) {
                    bestPosition = topPos
                    bestBoundingBox = boundingBox(placements + (rect to topPos))
                }
            }

            placements[rect] = bestPosition
            boundingBox = bestBoundingBox
        }

        return boundingBox
    }

    fun packRectanglesBFD(rectangles: List<Rectangle>): Rectangle {
        val sortedRectangles = rectangles.sortedByDescending { it.width * it.height }
        var boundingBox= Rectangle(0.0, 0.0)
        val placements = mutableMapOf<Rectangle, Vec2>()

        for (rect in sortedRectangles) {
            var bestPosition = Vec2(0.0, 0.0)
            var bestBoundingBox = boundingBox
            var minWastedArea = Double.MAX_VALUE

            // Try different positions based on existing placements
            for ((placedRect, placedPos) in placements) {
                val rightPos = Vec2(placedPos.x + placedRect.width, placedPos.y)
                val topPos = Vec2(placedPos.x, placedPos.y + placedRect.height)

                if (fits(rect, rightPos, placements)) {
                    val newBoundingBox = boundingBox(placements + (rect to rightPos))
                    val wastedArea = area(newBoundingBox) - area(boundingBox) - area(rect)
                    if (wastedArea < minWastedArea) {
                        minWastedArea = wastedArea
                        bestPosition = rightPos
                        bestBoundingBox = newBoundingBox
                    }
                }

                if (fits(rect, topPos, placements)) {
                    val newBoundingBox = boundingBox(placements + (rect to topPos))
                    val wastedArea = area(newBoundingBox) - area(boundingBox) - area(rect)
                    if (wastedArea < minWastedArea) {
                        minWastedArea = wastedArea
                        bestPosition = topPos
                        bestBoundingBox = newBoundingBox
                    }
                }
            }

            // If no fit is found, expand the bounding box
            if (minWastedArea == Double.MAX_VALUE) {
                bestPosition = Vec2(boundingBox.width, 0.0) // Place to the right
                bestBoundingBox = Rectangle(boundingBox.width + rect.width, maxOf(boundingBox.height, rect.height))
            }

            placements[rect] = bestPosition
            boundingBox = bestBoundingBox
        }

        return boundingBox
    }

    // Checks if a rectangle at a given position overlaps with any other placed rectangle
    fun fits(rect: Rectangle, position: Vec2, placements: Map<Rectangle, Vec2>): Boolean {
        val rectToCheck = Rectangle4(position.x, position.y, rect.width, rect.height)
        for ((placedRect, placedPos) in placements) {
            val placedRectToCheck = Rectangle4(placedPos.x, placedPos.y, placedRect.width, placedRect.height)
            if (intersects(rectToCheck, placedRectToCheck)) {
                return false
            }
        }
        return true
    }

    // Calculates the bounding box that encloses all placed rectangles
    fun boundingBox(placements: Map<Rectangle, Vec2>): Rectangle {
        if (placements.isEmpty()) {
            return Rectangle(0.0, 0.0)
        }

        var minX = Double.MAX_VALUE
        var minY = Double.MAX_VALUE
        var maxX = Double.MIN_VALUE
        var maxY = Double.MIN_VALUE

        for ((rect, pos) in placements) {
            minX = minOf(minX, pos.x)
            minY = minOf(minY, pos.y)
            maxX = maxOf(maxX, pos.x + rect.width)
            maxY = maxOf(maxY, pos.y + rect.height)
        }

        return Rectangle(maxX - minX, maxY - minY)
    }

    // Calculates the area of a rectangle
    fun area(rect: Rectangle): Double {
        return rect.width * rect.height
    }

    // Checks if two rectangles intersect
    fun intersects(rect1: Rectangle4, rect2: Rectangle4): Boolean {
        return rect1.x < rect2.x + rect2.width &&
                rect1.x + rect1.width > rect2.x &&
                rect1.y < rect2.y + rect2.height &&
                rect1.y + rect1.height > rect2.y
    }
}