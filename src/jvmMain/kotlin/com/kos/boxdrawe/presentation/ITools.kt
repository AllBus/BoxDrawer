package com.kos.boxdrawe.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.useResource
import com.jsevy.jdxf.DXFDocument
import com.kos.boxdrawer.template.TemplateCreator
import com.kos.drawer.DxfFigureDrawer
import com.kos.figure.IFigure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import nl.adaptivity.xmlutil.StAXReader
import nl.adaptivity.xmlutil.serialization.XML
import turtoise.DrawerSettings
import turtoise.DrawerSettingsList
import turtoise.FullSettings
import turtoise.TemplateAlgorithm
import turtoise.TortoiseAlgorithm
import turtoise.help.HelpData
import turtoise.help.HelpInfoCommand
import turtoise.help.TortoiseHelpInfo
import turtoise.parser.TPArg
import turtoise.parser.TortoiseParser
import java.awt.BasicStroke
import java.awt.Color
import java.io.File
import java.io.FileWriter

interface ITools {
    fun ds(): DrawerSettings

    fun saveFigures(fileName: File, figures: IFigure)

    fun dxfText(figures: IFigure): String

    fun algorithms(): List<Pair<String, TortoiseAlgorithm>>

    fun chooserDir(): File

    fun updateChooserDir(fileName: String)
}

class Tools() : ITools, SavableData {

    private val drawingSettings = mutableStateOf(DrawerSettings())

    val settings: State<DrawerSettings> = drawingSettings

    val settingsList = mutableStateOf<DrawerSettingsList>(DrawerSettingsList(emptyList()))

    val figureList = MutableStateFlow<List<Pair<String, TortoiseAlgorithm>>>(emptyList())

    private val lastSelectDir = MutableStateFlow<File>(File(""))

    override fun updateChooserDir(fileName: String) {
        File(fileName).parentFile?.let { p ->
            lastSelectDir.value = p
        }
    }

    override fun chooserDir(): File {
        return lastSelectDir.value
    }

    override fun ds(): DrawerSettings {

        return drawingSettings.value
    }

    override fun algorithms(): List<Pair<String, TortoiseAlgorithm>> {
        return figureList.value
    }

    fun loadSettings() {
        println("loadSettings")
        val settingsFile = useResource("settings/properties.xml") { input ->
            // InputStreamReader(input).use { r -> r.readLines().forEach(::println) }
            val settings = XML.decodeFromReader<FullSettings>(StAXReader(input, "UTF-8"))
            settingsList.value = settings.properties
            selectSettings(settingsList.value.group.firstOrNull() ?: DrawerSettings())
        }

        val algorithms = File("./settings/figures.txt")
        println(algorithms.absolutePath)
        if (algorithms.exists()) {
            try {
                figureList.value =
                    algorithms
                        .readLines(Charsets.UTF_8)
                        .map { TortoiseParser.extractTortoiseCommands(it) }
            } catch (
                e: Throwable
            ) {

            }
        }
    }

    fun selectSettings(newDs: DrawerSettings) {
        drawingSettings.value = newDs
        println(drawingSettings.value)
    }


    override fun saveFigures(fileName: File, figures: IFigure) {
        val dxfText = dxfText(figures)

        FileWriter(fileName).use { fileWriter ->
            fileWriter.write(dxfText);
            fileWriter.flush();
        };
    }

    override fun dxfText(figures: IFigure): String {
        val dxfDocument = DXFDocument("Figure")

        val graphics = dxfDocument.getGraphics()

        // set pen characteristics
        graphics.setColor(Color.BLACK)
        graphics.setStroke(BasicStroke(1f))

        val g = DxfFigureDrawer(dxfDocument)
        figures.draw(g)

        /* Get the DXF output as a string - it's just text - and  save  in a file for use with a CAD package */
        return dxfDocument.toDXFString();
    }

    fun selectFigure(index: Int) {
        //  currentFigure.value = figureList.value.getOrNull(index)?.second.?:FigureEmpty
    }

    override fun saveState() {
        try {
            File("settings/select_dir.txt").bufferedWriter(Charsets.UTF_8).use { output ->
                output.write(lastSelectDir.value.absolutePath)
                output.flush()
            }
        }catch (_:Exception){}
    }

    override fun loadState() {
        loadSettings()
        try {
            File("settings/select_dir.txt").bufferedReader(Charsets.UTF_8).use { input ->
                lastSelectDir.value = File(input.readLine()?:"")
            }
        }catch (_:Exception){}
    }

    private val templater = TemplateCreator
    val helpInfoList = figureList.map {  a ->
        val com = a.map { aa ->
            val (n, f) = aa
            if (f is TemplateAlgorithm) {
                HelpData("@$n", "", emptyList(),
                    creator = TPArg.createWithoutName(
                        TPArg.block(
                            TPArg.text("@$n"),
                            templater.parse(f.template)
                        )
                    )
                )
            } else{
                HelpData("@$n", "", emptyList(),
                    creator = TPArg.createWithoutName(TPArg.block(TPArg.text("@$n")) ))
            }
        }
        listOf(HelpInfoCommand("f",
                com,
            "Созданные ранее фигуры"
        )
        )+ TortoiseHelpInfo().commandList
    }

}