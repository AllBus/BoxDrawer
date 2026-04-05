package com.kos.compose

import androidx.compose.runtime.Immutable
import com.kos.figure.IFigure
import vectors.Matrix

@Immutable
class FigureInfo (
    val figure: IFigure,
    val parent: FigureInfo?,
    val transform: Matrix,
) {
    fun rect() = figure.rect().transform(transform)

    companion object {
        fun tree(figure: IFigure): List<FigureInfo> {
            val mt = Matrix()
            mt *= figure.transform
            return tree(figure, null, mt)
        }

        fun tree(figure: IFigure, parent: FigureInfo?, matrix: Matrix): List<FigureInfo> {
            val nm = matrix.copyWithTransform(figure.transform)
            val tek = FigureInfo(figure, parent, matrix)
            return listOf(tek) +
                    figure.collection().flatMap { f ->
                        tree(f, tek, nm)
                    }
        }
    }
}