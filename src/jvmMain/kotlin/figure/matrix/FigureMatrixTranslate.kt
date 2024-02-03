package figure.matrix

import androidx.compose.ui.graphics.Matrix
import com.kos.boxdrawe.drawer.IFigureGraphics
import figure.CropSide
import figure.Figure
import figure.IFigure
import org.jetbrains.skia.Matrix44
import vectors.BoundingRectangle
import vectors.Vec2


abstract class FigureMatrix(): Figure(){

    override val count: Int
        get() = 0

    override fun crop(k: Double, cropSide: CropSide): IFigure {
        return this
    }

    override fun rect(): BoundingRectangle {
        return BoundingRectangle.Empty
    }

    override fun translate(translateX: Double, translateY: Double): IFigure {
        return this
    }

    override fun rotate(angle: Double): IFigure {
        return this
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure {
        return this
    }
}
class FigureMatrixTranslate(val x: Double, val y: Double) : FigureMatrix() {
    override fun draw(g: IFigureGraphics) {
        g.translate(x, y)
    }
}

class FigureMatrixScale(val x: Double, val y: Double) : FigureMatrix() {
    override fun draw(g: IFigureGraphics) {
        g.scale(x, y)
    }
}

class FigureMatrixRotate(val angle: Double, val pivot: Vec2 = Vec2.Zero) : FigureMatrix() {
    override fun draw(g: IFigureGraphics) {
        g.rotate(angle, pivot)
    }
}

class FigureMatrixSave() : FigureMatrix() {
    override fun draw(g: IFigureGraphics) {
        g.save()
    }
}

class FigureMatrixRestore() : FigureMatrix() {

    override fun draw(g: IFigureGraphics) {
        g.restore()
    }
}

class Figure3dTransform(val m : Matrix, val figure: IFigure): FigureMatrix(){

    override fun draw(g: IFigureGraphics) {
        g.save()
        g.transform(m){
            figure.draw(g)
        }

        g.restore()

    }
}
