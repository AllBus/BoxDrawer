package turtoise

import vectors.Vec2
import kotlin.math.*

class TortoiseState {
    var x = 0.0
    var y = 0.0
    var a = 0.0
    var zigDelta = 30.0
    var zigWidth = 15.0

    var zigParam = DrawingParam().apply{
        orientation = Orientation.Horizontal
        reverse = false
        back = false
    }

    fun from(other: TortoiseState): TortoiseState {
        x = other.x
        y = other.y
        a = other.a
        zigDelta = other.zigDelta
        zigWidth = other.zigWidth
        zigParam.reverse = other.zigParam.reverse
        zigParam.back = other.zigParam.back
        zigParam.orientation = other.zigParam.orientation
        return this
    }

    public val xy : Vec2 get() = Vec2(x, y)

    public val angle : Double get() = PI * a / 180

    fun move(delta: Double) {
        val angle: Double = PI * a / 180
        x += delta * cos(angle)
        y += delta * sin(angle)
    }

    fun move90(delta: Double) {
        val angle: Double = PI * a / 180
        x += delta * -sin(angle)
        y += delta * cos(angle)
    }

    fun move(deltaX: Double, deltaY: Double) {
        val angle: Double = PI * a / 180
        x += deltaX * cos(angle) - deltaY * sin(angle)
        y += deltaX * sin(angle) + deltaY * cos(angle)
    }

    fun moveTo(coord: Vec2) {
        x = coord.x
        y = coord.y
    }

    fun clear()
    {
        x = 0.0
        y = 0.0
        a = 0.0
        zigDelta = 30.0
        zigWidth = 15.0

        zigParam = DrawingParam().apply{
            orientation = Orientation.Horizontal
            reverse = false
            back = false
        }
    }
}