package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getTextAfterSelection
import androidx.compose.ui.text.input.getTextBeforeSelection
import com.kos.boxdrawe.presentation.template.TemplateFigureEditor
import com.kos.boxdrawe.presentation.template.TemplateMoveListener
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import kotlinx.coroutines.flow.MutableStateFlow
import sun.awt.geom.AreaOp.IntOp
import turtoise.TortoiseProgram
import turtoise.TortoiseRunner
import turtoise.TortoiseState
import turtoise.parser.TortoiseParser
import java.awt.geom.Area
import java.awt.geom.Path2D
import java.io.File
import kotlin.math.min

class TortoiseData(override val tools: ITools) : SaveFigure, SavableData {
    val figures = MutableStateFlow<IFigure>(FigureEmpty)
    val fig = mutableStateOf<IFigure>(FigureEmpty)
    val helpText = mutableStateOf(AnnotatedString(""))

    val matrix = mutableStateOf(Matrix())

    val text = mutableStateOf(TextFieldValue(""))

    val editorListener = TemplateFigureEditor(::temperedText,::toNextPosition)

    val moveListener = TemplateMoveListener(::insertText)

    private var tempe = ""
    val insertedText = mutableStateOf("")

    private fun temperedText(f: String) {
        if (tempe != f ){
            tempe = ""
            insertedText.value = f
            createTortoise()
        } else{
            insertedText.value = ""

        }
    }

    private fun insertText(f: String) {
        val tv = text.value
        val ntext =
            tv.getTextBeforeSelection(tv.text.length) + AnnotatedString(f) + tv.getTextAfterSelection(
                tv.text.length
            )
        text.value = tv.copy(
            annotatedString = ntext,
            selection = TextRange(tv.selection.min, tv.selection.min + f.length)
        )
        createTortoise()
    }

    private fun toNextPosition(){
        val tv = text.value
        val ins = insertedText.value
        val ntext =
            tv.getTextBeforeSelection(tv.text.length) +
            AnnotatedString(ins) +
            tv.getTextAfterSelection(tv.text.length)

        tempe = ins
        insertedText.value = ""
        text.value = tv.copy(annotatedString = ntext, selection = TextRange(tv.selection.end+ins.length))
    }

    suspend fun printCommand(): String {
        val lines = text.value.text

        val program = tortoiseProgram(lines)
        return program.commands.flatMap { a ->
            a.names.flatMap { name ->
                a.commands(name, tools.ds()).flatMap {
                    it.commands
                }
            }
        }.map { c -> c.print() }.joinToString("\n")
    }

    private fun tortoiseProgram(lines: String): TortoiseProgram {
        val f = lines.split("\n").map { line ->
            TortoiseParser.extractTortoiseCommands(line)
        }

        val (c, a) = f.partition { it.first == "" }
        val k = tools.algorithms()
        return TortoiseProgram(
            commands = c.map { it.second },
            algorithms = (k + a).toMap()
        )
    }

    fun createTortoise() {
        val lines = tortoiseLines()
        val dr = create(lines)
        fig.value = dr
        figures.value = dr
    }

    override suspend fun createFigure(): IFigure {
        val lines = tortoiseLines()
        return create(lines)
    }

    private fun tortoiseLines(): String {
        val tv = text.value
        tv.text.subSequence(tv.selection.min, tv.text.length)

        val lines =
            tv.getTextBeforeSelection(tv.text.length).text +
            insertedText.value +
            tv.text.subSequence(tv.selection.min, tv.text.length)  // text.value.text
        return lines
    }

    private fun create(lines: String): IFigure {
        val program = tortoiseProgram(lines)
        val t = TortoiseRunner(program)
        val state = TortoiseState()
        val dr = t.draw(state, tools.ds())
        return dr
    }

    private val helpSeparator = charArrayOf('\n', '\r')
    private val helpSpaceSeparator = charArrayOf('\n', '\r', ' ', '\t', ';', '@')

    fun findHelp(text: String, selection: TextRange) {
        val s = selection.min
        val p = text.lastIndexOfAny(helpSeparator, s) + 1
        val e = Math.min(text.length - 1, text.indexOfAny(helpSpaceSeparator, p + 1))

        var comEnd = s
        while (comEnd >= 0 && comEnd < text.length) {
            if (text[comEnd] in helpSpaceSeparator) {
                break
            }
            comEnd -= 1
        }

        var comStart = comEnd - 1
        while (comStart >= 0 && comStart < text.length) {
            if (text[comStart] !in helpSpaceSeparator) {
                break
            }
            comStart -= 1
        }

        val subStr = if (p in 0 until e) {
            text.substring(p, e)
        } else ""

        val com = if (comStart in 0 until comEnd) {
            text.substring(comStart, comEnd)
        } else ""

        val help = TortoiseParser.helpFor(subStr, com)
        helpText.value = help

    }

    fun rotate(x: Float, y: Float, z: Float) {
        val m = Matrix()
        m.reset()
        m.rotateX(x)
        m.rotateY(y)
        m.rotateZ(z)
        matrix.value = m
    }

    override fun saveState() {
        try {
            File("settings/tortoise.txt").bufferedWriter(Charsets.UTF_8).use { output ->
                val lines = text.value.text
                output.write(lines)
                output.flush()
            }
        } catch (_: Exception) {
        }
    }

    override fun loadState() {
        try {
            File("settings/tortoise.txt").bufferedReader(Charsets.UTF_8).use { output ->
                text.value = text.value.copy(output.readText())
            }
        } catch (_: Exception) {
        }
    }

}