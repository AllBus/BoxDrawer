package figure

import androidx.compose.runtime.Immutable
import com.kos.boxdrawe.drawer.IFigureGraphics
import vectors.BoundingRectangle
import vectors.Vec2


@Immutable
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

@Immutable
abstract class Figure : IFigure {
    override val count: Int
        get() = 1

    override fun list(): List<Figure> {
        return listOf(this)
    }

    companion object {
        val Empty = FigureEmpty
    }
}

@Immutable
object FigureEmpty: IFigure{
    override val count: Int
        get() = 0

    override fun crop(k: Double, cropSide: CropSide): IFigure = this

    override fun list(): List<Figure> = emptyList()

    override fun rect(): BoundingRectangle = BoundingRectangle.Empty

    override fun translate(translateX: Double, translateY: Double): IFigure = this

    override fun rotate(angle: Double): IFigure = this

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure = this

    override fun draw(g: IFigureGraphics) {}

}