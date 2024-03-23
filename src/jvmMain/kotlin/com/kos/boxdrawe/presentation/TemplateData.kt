package com.kos.boxdrawe.presentation

import com.kos.boxdrawer.figure.FigureExtractor
import com.kos.boxdrawer.template.TemplateCreator
import com.kos.boxdrawer.template.TemplateGeneratorListener
import com.kos.boxdrawer.template.TemplateInfo
import com.kos.boxdrawer.template.TemplateMemory
import com.kos.boxdrawer.template.TemplateMemoryItem
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.kabeja.dxf.DXFDocument
import org.kabeja.parser.DXFParser
import org.kabeja.parser.ParserBuilder
import turtoise.TemplateAlgorithm
import turtoise.TortoiseProgram
import turtoise.TortoiseRunner
import turtoise.TortoiseState
import turtoise.TurtoiseParserStackBlock
import turtoise.memory.TwoBlockTortoiseMemory
import java.io.File
import java.io.FileInputStream

class TemplateData(val tools: ITools) {
    private val templater = TemplateCreator

    private val emptyAlgorithm = TemplateAlgorithm(
        "",
        TurtoiseParserStackBlock(),
        TurtoiseParserStackBlock(),
        TurtoiseParserStackBlock()
    )

    private val algorithm = MutableStateFlow<TemplateAlgorithm>(
        emptyAlgorithm
    )

    private val algorithmName = MutableStateFlow("")
    val menu = algorithm.map {
        TemplateInfo(
            templater.parse(it.template),
            it.default,
        )
    }

    val memory = TemplateMemory()

    val currentFigure = MutableStateFlow<IFigure>(FigureEmpty)

    fun redraw() {
        val runner = TortoiseRunner(TortoiseProgram(emptyList(), tools.algorithms().toMap()))

        val top = TurtoiseParserStackBlock()
        memory.memoryBlock(top)

        val m = TwoBlockTortoiseMemory(top, algorithm.value.default)

        currentFigure.value =
            algorithm.value.draw("", tools.ds(), TortoiseState(), m, runner, 10)
    }

    val templateGenerator = object : TemplateGeneratorListener {
        override fun templateGenerator(arg: String, index: Int, count: Int, value: String) {
            if (count > 1) {
                memory.put(arg, index, count, value)
                redraw()
            } else {
                templateGenerator(arg, value)
            }
        }

        override fun templateGenerator(arg: String, value: String) {
            memory.put(arg, value)
            redraw()
        }
    }

    fun setTemplate(a: TemplateAlgorithm, name: String) {
        algorithm.value = a
        algorithmName.value = name
        memory.clear()

        redraw()
    }

    fun clearTemplate() {
        algorithm.value = emptyAlgorithm
        algorithmName.value = ""
        memory.clear()
        redraw()
    }

    fun save(fileName: String) {
        //   redraw()
        tools.saveFigures(fileName, currentFigure.value)
        tools.updateChooserDir(fileName)
    }

    suspend fun print(): String {
        val name = algorithmName.value

        val top = TurtoiseParserStackBlock()

        memory.union( menu.first().memoryValues()).memoryBlock(top)

        val memoryarguments = top.line

        return "f (@${name} ${memoryarguments})"
    }

    fun loadDxf(fileName: String) {
        try {
            val f = File(fileName)
            val parser = ParserBuilder.createDefaultParser()

            parser.parse(FileInputStream(f), DXFParser.DEFAULT_ENCODING)
            val doc: DXFDocument = parser.getDocument()

            val extractor = FigureExtractor()
            currentFigure.value = extractor.extractFigures(doc)
            tools.updateChooserDir(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}