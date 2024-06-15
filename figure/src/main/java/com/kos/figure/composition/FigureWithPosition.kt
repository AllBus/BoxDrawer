package com.kos.figure.composition

import com.kos.drawer.IFigureGraphics
import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.IFigure
import com.kos.figure.IFigurePath
import vectors.BoundingRectangle
import vectors.Matrix

class FigureWithPosition(
    val path: IFigurePath,
    val positions: List<PositionOnFigure>,
    /** Если true рисует части вне positions если false рисует части внутри positions*/
    val isDrop: Boolean,
) : Figure() {

    val groupedPositions = positions.groupBy { it.edge }.mapValues { v ->
        v.value.sortedBy { it.offset }
    }

    private fun createOutFigures(): List<IFigure> {
        val res = mutableListOf<IFigure>()
        for (i in 0 until path.edgeCount()) {
            val p = path.path(i)
            val group = groupedPositions[i]
            if (group.isNullOrEmpty()){
                res.add( p)
            } else {
                var pred = 0.0
                group.forEach { pos ->
                    res.add(p.take(pred, pos.start() ))
                    pred = pos.end()
                }
                res.add(p.take(pred, path.pathLength(i)))
            }
        }
        return res.toList()
    }

    private fun createInFigures(): List<IFigure> {
        val res = mutableListOf<IFigure>()
        for (i in 0 until path.edgeCount()) {
            val p = path.path(i)
            val group = groupedPositions[i]
            if (group.isNullOrEmpty()){

            } else {
                group.forEach { pos ->
                    res.add(p.take( pos.start(), pos.end() ))
                }
            }
        }
        return res.toList()
    }

    private val figures : List<IFigure> by lazy {
        if (isDrop)
            createOutFigures()
        else
            createInFigures()
    }

    override val count: Int
        get() = 1

    override fun crop(k: Double, cropSide: CropSide): IFigure {
        // TODO
        return this
    }

    override fun list(): List<Figure> {
        return emptyList()
    }

    override fun rect(): BoundingRectangle {
        return path.rect()
    }

    override fun draw(g: IFigureGraphics) {
        figures.forEach { f ->
             f.draw(g)
        }
    }

    override fun print(): String {
        return "/${if (isDrop) "drop" else "take"} (${path.print()}) ${positions.joinToString(" ") { "(${it.edge} ${it.offset} ${it.width} ${it.bias})" }}"
    }

    override fun collection(): List<IFigure> {
        return figures
    }

    override fun name(): String {
        return if (isDrop) "drop" else "take"
    }

    override val transform: Matrix
        get() = Matrix.identity

}

data class PositionOnFigure(
    val edge: Int,
    val offset: Double,
    val width: Double,
    val bias: Double = 0.5,
){
    fun start(): Double {
        return offset-width*bias
    }

    fun end(): Double {
        return offset+width-width*bias
    }
}