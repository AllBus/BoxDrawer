package figure

import com.kos.boxdrawe.drawer.IFigureGraphics
import vectors.Vec2

class FigureSpline(points: List<Vec2>): FigurePolygon(points) {

    override fun create(points: List<Vec2>): FigurePolygon {
        return FigureSpline(points)
    }

    override fun draw(g: IFigureGraphics) {

        g.drawSpline(points)
    }
}