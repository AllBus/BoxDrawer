package com.kos.boxdrawe.drawer

import com.jsevy.jdxf.DXFDocument
import figure.*

object FigureDxf {

   fun draw(document: DXFDocument,  figureLine: IFigure){

       val g = DxfFigureDrawer(document)
       figureLine.draw(g)
   }
}
