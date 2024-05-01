package com.kos.figure.composition

import com.kos.drawer.IFigureGraphics
import com.kos.figure.IFigure
import com.kos.figure.matrix.FigureMatrix
import vectors.Matrix

class Figure3dTransform(val m : Matrix, val figure: IFigure): FigureMatrix(){

    override fun draw(g: IFigureGraphics) {
        g.save()
        g.transform(m){
            figure.draw(g)
        }
        g.restore()
    }

    override fun print(): String {
        return "3dTransform"
    }
}