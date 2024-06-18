package turtoise

import com.kos.figure.FigureEmpty
import com.kos.figure.FigurePolyline
import com.kos.figure.IFigure
import com.kos.figure.composition.FigureTranslateWithRotate
import vectors.Vec2

class TortoiseBuilder(
    val state: TortoiseState
) {
    private val res = mutableListOf<IFigure>()
    var result = mutableListOf<Vec2>()

    val xy get() = state.xy

    /** radians */
    val angle get() = state.angle

    fun build(): List<IFigure> {
        return res
    }

    fun saveLine() {
        if (result.size > 1) {
            res.add(FigurePolyline(points = result.toList()))
        }
        result = mutableListOf()
    }

    fun addAll(figures: List<IFigure>) {
        res.addAll(figures)
    }

    fun add(figure: IFigure) {
        res.add(figure)
    }


    fun add(point: Vec2) {
        result.add(point)
    }

    fun addPoint() {
        result.add(state.xy)
    }

    fun startPoint() {
        if (result.isEmpty()) {
            result.add(state.xy)
        }
    }

    fun closeLine() {
        if (result.size > 2) {
            result.add(result.first())
            saveLine()
        }
    }

    fun addProduct(figure: IFigure) {
        if (figure !is FigureEmpty)
            add(FigureTranslateWithRotate(figure, state.xy, state.angleInDegrees))
    }

    fun addPolyline(points:List<Vec2>, isClose:Boolean = false){
        val xy = xy
        val angle = angle
        add(FigurePolyline(points.map{ xy+it.rotate(angle)}, true))
    }
}