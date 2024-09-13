package com.kos.figure.composition.booleans

import com.kos.figure.FigureBezier
import com.kos.figure.FigureEmpty
import com.kos.figure.FigureLine
import com.kos.figure.IFigure
import com.kos.figure.algorithms.UnionFigure
import com.kos.figure.collections.FigureList
import vectors.Vec2
import java.awt.geom.AffineTransform
import java.awt.geom.PathIterator


class FigureIntersect(
    figure1: IFigure,
    figure2: IFigure,
    approximationSize: Int,
) : FigureUnion(figure1, figure2, approximationSize) {

    override fun recalculate(): IFigure {
        val newFigure: IFigure = when {
            figure1 == figure2 -> figure1
            figure1 == FigureEmpty -> FigureEmpty
            figure2 == FigureEmpty -> FigureEmpty
            else -> {
//                val a1 = UnionFigure.figureToArea(figure1)
//                val a2 = UnionFigure.figureToArea(figure2)
//
//                a1.intersect(a2)
//                val coords = DoubleArray(8)
//                val i = a1.getPathIterator(AffineTransform())
//                var result = mutableListOf<IFigure>()
//                while(!i.isDone){
//                    i.next()
//                    val t= i.currentSegment(coords)
//                    when(t){
//                        PathIterator.SEG_CLOSE -> {} // 0
//                        PathIterator.SEG_QUADTO -> {}// 2
//                        PathIterator.SEG_CUBICTO ->{
//                            result.add(FigureBezier(listOf(
//                                Vec2(coords[0], coords[1]),
//                                Vec2(coords[2], coords[3]),
//                                Vec2(coords[4], coords[5]),
//                                Vec2(coords[6], coords[7]),
//                            )))
//                        }  // 3
//                        else -> {
//                            result.add(FigureLine(Vec2(coords[0], coords[1]),Vec2(coords[2], coords[3])))
//                        }//1
//                    }
//                }
//                return FigureList(result.toList())
                return UnionFigure.intersect(
                    approximations(figure1) ,
                    approximations(figure2) ,
                    approximationSize
                )
            }
        }
        return newFigure
    }
}