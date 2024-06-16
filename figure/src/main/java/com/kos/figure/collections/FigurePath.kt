package com.kos.figure.collections

import com.kos.drawer.IFigureGraphics
import com.kos.figure.CropSide
import com.kos.figure.Figure
import vectors.BoundingRectangle
import vectors.Matrix
import vectors.Vec2

class FigurePath(val figures: List<Figure>): Figure() {
    override fun collection(): List<Figure> {
        return figures
    }

    override fun translate(translateX: Double, translateY: Double): Figure {
        return FigurePath(figures.map { it.translate(translateX, translateY) })
    }

    override fun rotate(angle: Double): Figure {
        return FigurePath(figures.map { it.rotate(angle) })
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): Figure {
        return FigurePath(figures.map { it.rotate(angle, rotateCenter) })
    }

    override fun transform(matrix: Matrix): Figure {
        return FigurePath(figures.map { it.transform(matrix) })
    }

    override fun crop(k: Double, cropSide: CropSide): Figure {
        return FigurePath( figures.map { it.crop(k, cropSide) })
    }


    /*
    private val length: List<Double> by lazy { calculateLength() }
    private val edges: List<Int> by lazy { calculateEdges() }

    fun calculateLength(): List<Double> {
        var sum = 0.0
        return figures.map { p ->
            sum += p.pathLength()
            sum
        }
    }

    fun calculateEdges(): List<Int> {
        var sum = 0
        return figures.map { p ->
            sum += p.edgeCount()
            sum
        }
    }

    val fullLength : Double by lazy {
        figures.map { it.pathLength() }.sum()
    }

    override fun positionInPath(delta: Double): PointWithNormal {

        TODO("Not yet implemented")
    }

    override fun positionInPath(edge: Int, delta: Double): PointWithNormal {
        TODO("Not yet implemented")
    }

    override fun pathLength(): Double {
        TODO("Not yet implemented")
    }

    override fun pathLength(edge: Int): Double {
        TODO("Not yet implemented")
    }

    override fun edgeCount(): Int {
        TODO("Not yet implemented")
    }

    override fun path(edge: Int): IFigurePath {
        TODO("Not yet implemented")
    }

    override fun startPoint(): Vec2 {
        TODO("Not yet implemented")
    }

    override fun endPoint(): Vec2 {
        TODO("Not yet implemented")
    }

    override fun take(startMM: Double, endMM: Double): Figure {
        TODO("Not yet implemented")
    }

    override fun duplicationAtNormal(h: Double): Figure {
        TODO("Not yet implemented")
    }

 */
    override val count: Int
        get() = figures.size

    override fun list(): List<Figure> {
        return figures.flatMap { it.list() }
    }

    override fun rect(): BoundingRectangle {
        return BoundingRectangle.union( figures.map { it.rect() })
    }

    override fun draw(g: IFigureGraphics) {
        figures.forEach { f -> f.draw(g) }
    }

    override fun print(): String {
        return figures.joinToString(" "){it.print()}
    }

    override fun name(): String {
        return "Путь"
    }
}