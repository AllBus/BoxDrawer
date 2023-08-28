package com.kos.boxdrawe.drawer

import com.jsevy.jdxf.DXFDocument
import com.jsevy.jdxf.DXFGraphics
import figure.*
import vectors.Vec2

object FigureDxf {

   fun draw(document: DXFDocument,  figureLine: IFigure){

       val g = DxfFigureDrawer(document)
       figureLine.draw(g)
   }
}
