package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import com.groupdocs.comparison.Document
import com.groupdocs.comparison.options.PreviewOptions
import com.groupdocs.comparison.options.enums.PreviewFormats
import com.jsevy.jdxf.DXFDocument
import com.kos.boxdrawe.drawer.FigureDxf
import com.kos.boxdrawe.widget.BoxDrawerToolBar
import figure.Figure
import figure.IFigure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import turtoise.*
import vectors.Vec2
import java.awt.BasicStroke
import java.awt.Color
import java.io.*
import java.nio.file.Files
import java.nio.file.Path

class DrawerViewModel {

    val tortoise = TortoiseData()
    val tabIndex = mutableStateOf(BoxDrawerToolBar.TAB_TORTOISE)

    val pageFile = mutableStateOf<Path?>(null)

    fun previewDxf(path: String) {

        CoroutineScope(Dispatchers.Default).launch {
              withContext(Dispatchers.IO) {
                Document(path).use { document ->
                    document.generatePreview(PreviewOptions { pageNumber ->
                        if (pageNumber == 1) {
                            val f = Files.createTempFile("preview_", ".png")
                            pageFile.value = f
                            if (f == null){
                                ByteArrayOutputStream()
                            }else {
                                println("Preview path: $pageFile")
                                BufferedOutputStream(FileOutputStream(f.toFile()))
                            }

                        } else {
                            pageFile.value = null
                            println("Preview for page $pageNumber was ignored")
                            // Just ignoring other pages in the example
                            ByteArrayOutputStream()
                        }
                    }.apply { previewFormat = PreviewFormats.PNG })
                }

            }
        }
    }
}

class TortoiseData{
    val figures = mutableStateOf<IFigure>(Figure.Empty)

    private val t = Tortoise()

    private val ds = DrawerSettings()

    private val memory = object: TortoiseMemory {
        override fun value(variable: String, defaultValue: Double): Double {
            val d = variable.toDoubleOrNull()
            if (d != null)
                return d
            return defaultValue
        }
    }



    fun saveTortoise(fileName: String, lines:String) {
        val program = tortoiseProgram(lines)
        val fig = t.draw(program, Vec2.Zero, ds, memory)

        val dxfDocument = DXFDocument("Figure");
        val graphics = dxfDocument.getGraphics();

        // set pen characteristics
        graphics.setColor(Color.BLACK);
        graphics.setStroke(BasicStroke(1f));


        FigureDxf.draw(dxfDocument, fig)


        /* Get the DXF output as a string - it's just text - and  save  in a file for use with a CAD package */
        val dxfText = dxfDocument.toDXFString();
        val filePath = fileName;
        val fileWriter = FileWriter(filePath);
        fileWriter.write(dxfText);
        fileWriter.flush();
        fileWriter.close();
    }

    private fun tortoiseProgram(lines: String): TortoiseProgram {
        return TortoiseProgram(commands = lines.split("\n").map {line ->
            TortoiseParser.extractTortoiseCommands(line)
        })
    }

    fun createTortoise(lines:String){
        val program = tortoiseProgram(lines)
        figures.value = t.draw(program, Vec2.Zero, ds, memory)
    }


    val text = mutableStateOf("")
}