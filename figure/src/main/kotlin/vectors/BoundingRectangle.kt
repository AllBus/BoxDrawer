package vectors

import kotlin.math.max
import kotlin.math.min

data class BoundingRectangle(val min: Vec2, val max: Vec2) {
    val width: Double get() = max.x - min.x
    val height: Double get() = max.y - min.y

    val centerX = (min.x + max.x) / 2
    val centerY = (min.y + max.y) / 2

    override fun toString(): String {
        return "$min : $max"
    }

    fun union(rect: BoundingRectangle): BoundingRectangle {

        return BoundingRectangle(
            Vec2(min(this.min.x, rect.min.x), min(this.min.y, rect.min.y)),
            Vec2(max(this.max.x, rect.max.x), max(this.max.y, rect.max.y))
        )
    }

    fun translate(delta: Vec2): BoundingRectangle {
        return BoundingRectangle(
            min + delta,
            max + delta,
        )
    }

    fun scale(scaleX: Double, scaleY: Double): BoundingRectangle {
        return BoundingRectangle(
            Vec2(min.x * scaleX, min.y * scaleY),
            Vec2(max.x * scaleX, max.y * scaleY),
        )
    }

    fun transform(transform: Matrix): BoundingRectangle {
        val a = Vec2(min.x, max.y)
        val b = Vec2(max.x, min.y)
        val c = min
        val d = max
        val points: List<Vec2> = listOf(a, b, c, d).map { transform * it }
        return apply(points)
    }

    constructor(center: Vec2, radius: Double) : this(
        Vec2(center.x - radius, center.y - radius),
        Vec2(center.x + radius, center.y + radius)
    ) {

    }

    companion object {
        val Empty: BoundingRectangle = BoundingRectangle(Vec2(0.0, 0.0), Vec2(0.0, 0.0))

        fun apply(points: List<Vec2>): BoundingRectangle {
            var minX = points.first().x
            var minY = points.first().y
            var maxX = minX
            var maxY = minY;
            points.forEach { p ->
                minX = min(minX, p.x)
                minY = min(minY, p.y)

                maxX = max(maxX, p.x)
                maxY = max(maxY, p.y)
            }

            return BoundingRectangle(Vec2(minX, minY), Vec2(maxX, maxY))
        }

        @Suppress("unused")
        fun apply(
            center: Vec2,
            radius: Double,
            minorRadius: Double,
            rotation: Double
        ): BoundingRectangle {
            val maxRadius = max(radius, minorRadius)
            return BoundingRectangle(center, maxRadius)
        }

        fun union(boundings: List<BoundingRectangle>): BoundingRectangle {
            if (boundings.isEmpty())
                return Empty

            return boundings.drop(1).fold(boundings.first()) { a, b -> a.union(b) }
        }
    }
}