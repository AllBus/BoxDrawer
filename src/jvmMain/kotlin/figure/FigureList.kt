package figure

import com.kos.boxdrawe.drawer.IFigureGraphics
import vectors.BoundingRectangle
import vectors.Vec2

class FigureList(
    private val figures: List<IFigure>
) : IFigure {

    override val count: Int
        get() = figures.size

    override fun list(): List<Figure> {
        return figures.flatMap { it.list() }
    }

    override fun crop(k: Double, cropSide: CropSide): IFigure {
        return FigureList(
            list().map { it.crop(k, cropSide) }
        )
    }

    override fun rect(): BoundingRectangle {
        val l = list()

        if (l.isEmpty()) {
            return BoundingRectangle.Empty
        }

        return l.map { it.rect() }.fold(l.first().rect()) { a, b -> a.union(b) }
    }

    override fun translate(translateX: Double, translateY: Double): IFigure {
        return FigureList(list().map { it.translate(translateX, translateY) })
    }

    override fun rotate(angle: Double): IFigure {
        return FigureList(list().map { it.rotate(angle) })
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure {
        return FigureList(list().map { it.rotate(angle, rotateCenter) })
    }

    override fun draw(g: IFigureGraphics) {
        list().forEach { it.draw(g) }
    }

    fun simple(): FigureList {
        return FigureList(list())
    }

}