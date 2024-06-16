package turtoise

import vectors.Vec2
import kotlin.math.*

class TortoiseState {
    var x = 0.0
    var y = 0.0
    /** degrees */
    var angleInDegrees = 0.0
    var zigDelta = 30.0
    var zigWidth = 15.0

    var zigParam = DrawingParam(
        reverse = false,
        back = false,
    )

    fun from(other: TortoiseState): TortoiseState {
        x = other.x
        y = other.y
        angleInDegrees = other.angleInDegrees
        zigDelta = other.zigDelta
        zigWidth = other.zigWidth
        zigParam = DrawingParam(
            reverse = other.zigParam.reverse,
            back = other.zigParam.back,
        )
        return this
    }

    public val xy : Vec2 get() = Vec2(x, y)

    /** radians */
    public val angle : Double get() = PI * angleInDegrees / 180

    fun move(delta: Double) {
        val angle: Double = PI * angleInDegrees / 180
        x += delta * cos(angle)
        y += delta * sin(angle)
    }

    fun move90(delta: Double) {
        val angle: Double = PI * angleInDegrees / 180
        x += delta * -sin(angle)
        y += delta * cos(angle)
    }

    fun move(deltaX: Double, deltaY: Double) {
        val angle: Double = PI * angleInDegrees / 180
        x += deltaX * cos(angle) - deltaY * sin(angle)
        y += deltaX * sin(angle) + deltaY * cos(angle)
    }

    fun move(delta: Vec2) {
        val angle: Double = PI * angleInDegrees / 180
        x += delta.x * cos(angle) - delta.y * sin(angle)
        y += delta.x * sin(angle) + delta.y * cos(angle)
    }

    fun moveTo(coord: Vec2) {
        x = coord.x
        y = coord.y
    }

    fun clear()
    {
        x = 0.0
        y = 0.0
        angleInDegrees = 0.0
        zigDelta = 30.0
        zigWidth = 15.0

        zigParam = DrawingParam(
            reverse = false,
            back = false,
        )
    }
}