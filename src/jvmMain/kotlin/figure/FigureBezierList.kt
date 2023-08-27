package figure

import vectors.BoundingRectangle
import vectors.Vec2

class FigureBezierList(val points : List<List<Vec2>>): Figure() {

    override fun crop(k: Double, cropSide: CropSide): IFigure {
        val figures = mutableListOf<List<Vec2>>()

        val (predicate, napr) = when (cropSide) {
            CropSide.LEFT ->
                Pair({ a: Vec2 -> a.x >= k }, { a: Vec2 -> a.x })

            CropSide.RIGHT ->
                Pair({ a: Vec2 -> a.x <= k }, { a: Vec2 -> a.x })

            CropSide.BOTTOM ->
                Pair({ a: Vec2 -> a.y >= k }, { a: Vec2 -> a.y })

            CropSide.TOP ->
                Pair({ a: Vec2 -> a.y <= k }, { a: Vec2 -> a.y })
        }//end when

        for (ps in points) {
            when {
                ps.all(predicate) -> figures.add(ps)
                !ps.all { !predicate(it) } -> {
                    val roots = Vec2.getCubicRoots(ps.map(napr).map { it - k })
                    figures.addAll(Vec2.casteljauLine(ps, roots).filter { l -> predicate(l[1]) })
                }
            }
        }

        if (figures.isEmpty())
            return Empty
        return FigureBezierList(figures.toList())
    }

    override fun rect(): BoundingRectangle {
        return BoundingRectangle.union(points.map(BoundingRectangle.Companion::apply))
    }

    override fun translate(translateX: Double, translateY: Double): IFigure {
        return FigureBezierList(points.map { l -> l.map { p -> Vec2(p.x + translateX, p.y + translateY) } })
    }

    override fun rotate(angle: Double): IFigure {
        return FigureBezierList(points.map { l -> l.map { p -> p.rotate(angle) } })
    }

    override fun rotate(angle: Double, rotateCenter: Vec2): IFigure {
        return FigureBezierList(points.map { l -> l.map { p -> (p - rotateCenter).rotate(angle) + rotateCenter } })
    }

    companion object {
        fun simple(beziers: List<FigureBezierList>): FigureBezierList{
            return FigureBezierList(beziers.flatMap { it.points })
        }
    }
}