package com.kos.boxdrawe.drawer

import com.jsevy.jdxf.DXFDocument
import com.kos.drawer.DxfFigureDrawer
import com.kos.figure.IFigure

object FigureDxf {

   fun draw(document: DXFDocument,  figureLine: IFigure){

       val g = DxfFigureDrawer(document)
       figureLine.draw(g)
   }
}
