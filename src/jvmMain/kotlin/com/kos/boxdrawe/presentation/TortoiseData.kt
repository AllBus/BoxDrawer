package com.kos.boxdrawe.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.IFigure
import kotlinx.coroutines.flow.MutableStateFlow
import turtoise.*

class TortoiseData(val tools: ITools) {
    val figures = MutableStateFlow<IFigure>(Figure.Empty)
    val fig = mutableStateOf<IFigure>(Figure.Empty)
    val helpText = mutableStateOf(AnnotatedString(""))

    val matrix = mutableStateOf(Matrix())

    @OptIn(ExperimentalFoundationApi::class)
    fun save(fileName: String) {
        val lines = text.value.text
        val fig = create(lines)
        tools.saveFigures(fileName, fig)
        tools.updateChooserDir(fileName)
    }

    @OptIn(ExperimentalFoundationApi::class)
    suspend fun printCommand():String{
        val lines = text.value.text
        val program = tortoiseProgram(lines)
        return program.commands.flatMap { a ->
                a.names.flatMap { name -> a.commands(name,tools.ds()).flatMap {
                    it.commands
                }
            }
        }.map { c -> c.print()}.joinToString("\n")
    }

    private fun tortoiseProgram(lines: CharSequence): TortoiseProgram {
        val f = lines.split("\n").map { line ->
            TortoiseParser.extractTortoiseCommands(line)
        }

        val (c, a) = f.partition { it.first == "" }
        val k = tools.algorithms()
        return TortoiseProgram(
            commands = c.map { it.second },
            algorithms = (k+a).toMap()
        )
    }

    fun createTortoise(lines: String) {
        val dr = create(lines)

        fig.value = dr
        figures.value = dr
    }

    private fun create(lines: CharSequence): IFigure {
        val program = tortoiseProgram(lines)
        val t = TortoiseRunner(program)
        val state = TortoiseState()
        val dr = t.draw(state, tools.ds())
        return dr
    }

    fun drop(dropValueX: Float, dropValueY: Float) {
        figures.value =
            fig.value.crop(dropValueX.toDouble(), CropSide.BOTTOM).crop(dropValueY.toDouble(), CropSide.LEFT)
    }


    private val helpSeparator = charArrayOf('\n', '\r')
    private val helpSpaceSeparator = charArrayOf('\n', '\r', ' ', '\t', ';', '@')
    fun findHelp(text: String, selection: TextRange) {
        val s = selection.min
        val p = text.lastIndexOfAny(helpSeparator, s) + 1
        val e = Math.min(text.length - 1, text.indexOfAny(helpSpaceSeparator, p + 1))

        var comEnd = s-1
        while (comEnd>= 0  && comEnd< text.length){
            if (text[comEnd] in helpSpaceSeparator){
                break
            }
            comEnd -= 1
        }

        var comStart = comEnd-1
        while (comStart>= 0  && comStart< text.length){
            if (text[comStart] !in helpSpaceSeparator){
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

    @OptIn(ExperimentalFoundationApi::class)
    val text = mutableStateOf(TextFieldValue(""))
}