package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.useResource
import com.jsevy.jdxf.DXFDocument
import com.kos.boxdrawe.drawer.DxfFigureDrawer
import figure.IFigure
import nl.adaptivity.xmlutil.StAXReader
import nl.adaptivity.xmlutil.XmlReader
import nl.adaptivity.xmlutil.serialization.XML
import turtoise.DrawerSettings
import turtoise.DrawerSettingsList
import turtoise.FullSettings
import java.awt.BasicStroke
import java.awt.Color
import java.io.FileWriter
import java.io.InputStreamReader

interface ITools {
    fun ds(): DrawerSettings

    fun saveFigures(fileName: String, figures: IFigure)
}

class Tools() : ITools {

    var drawingSettings = mutableStateOf(DrawerSettings())

    val settingsList = mutableStateOf<DrawerSettingsList>(DrawerSettingsList(emptyList()))

    override fun ds(): DrawerSettings {
        return drawingSettings.value
    }


    fun loadSettings(){
        val settingsFile = useResource("settings/properties.xml"){ input ->
           // InputStreamReader(input).use { r -> r.readLines().forEach(::println) }
            val settings = XML.decodeFromReader<FullSettings>(StAXReader(input, "UTF-8"))
            settingsList.value = settings.properties
            selectSettings(settingsList.value.group.firstOrNull()?: DrawerSettings())

        }
    }

    fun selectSettings(newDs: DrawerSettings){
        drawingSettings.value = newDs.copy()
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