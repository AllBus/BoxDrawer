package figure

import com.kos.boxdrawe.drawer.IFigureGraphics
import vectors.BoundingRectangle
import vectors.Vec2


interface IFigure {

    val count: Int
    fun crop(k: Double, cropSide: CropSide): IFigure
    fun list(): List<Figure>
    fun rect(): BoundingRectangle

    fun translate(translateX: Double, translateY: Double): IFigure
    fun rotate(angle: Double): IFigure
    fun rotate(angle: Double, rotateCenter: Vec2): IFigure
    fun draw(g: IFigureGraphics)

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