package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import com.jsevy.jdxf.DXFDocument
import com.kos.boxdrawe.drawer.DxfFigureDrawer
import figure.IFigure
import turtoise.DrawerSettings
import java.awt.BasicStroke
import java.awt.Color
import java.io.FileWriter

interface ITools {
    fun ds(): DrawerSettings

    fun saveFigures(fileName: String, figures: IFigure)
}

class Tools() : ITools {

    var drawingSettings = DrawerSettings()
    override fun ds(): DrawerSettings {
        return drawingSettings
    }



    override fun saveFigures(fileName: String, figures: IFigure) {
        val dxfDocument = DXFDocument("Figure")

        val graphics = dxfDocument.getGraphics()

        // set pen characteristics
        graphics.setColor(Color.BLACK)
        graphics.setStroke(BasicStroke(1f))

        val g = DxfFigureDrawer(dxfDocument)
        figures.draw(g)

        /* Get the DXF output as a string - it's just text - and  save  in a file for use with a CAD package */
        val dxfText = dxfDocument.toDXFString();

        FileWriter(fileName).use { fileWriter ->
            fileWriter.write(dxfText);
            fileWriter.flush();
        };

    }

}