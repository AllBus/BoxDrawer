package figure

import vectors.BoundingRectangle
import vectors.Vec2
import kotlin.math.cos
import kotlin.math.sin


interface IFigure {

    val count: Int
    fun crop(k: Double, cropSide: CropSide): IFigure
    fun list(): List<Figure>
    fun rect(): BoundingRectangle
//    fun createEntity(): IEnumerable<EntityObject?>
//    fun createEntity(
//        scaleX: Double,
//        scaleY: Double,
//        translateX: Double,
//        translateY: Double,
//        afterTranslateX: Double,
//        afterTranslateY: Double
//    ): IEnumerable<EntityObject?>?

    fun translate(translateX: Double, translateY: Double): IFigure
    fun rotate(angle: Double): IFigure
    fun rotate(angle: Double, rotateCenter: Vec2): IFigure
//    fun draw(g: Graphics?, pen: Pen?)
}

enum class CropSide {
    LEFT,
    TOP,
    RIGHT,
    BOTTOM,
}

abstract class Figure : IFigure {
    override val count: Int
        get() = 1

    override fun list(): List<Figure> {
        return listOf(this)
    }

    companion object {
        val Empty = FigureList(emptyList())

    }
}