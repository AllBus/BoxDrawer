package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import figure.CropSide
import figure.Figure
import figure.IFigure
import turtoise.SimpleTortoiseMemory
import turtoise.TortoiseParser
import turtoise.TortoiseProgram
import turtoise.TortoiseRunner
import vectors.Vec2

class                       TortoiseData(val tools: ITools) {
    val figures = mutableStateOf<IFigure>(Figure.Empty)
    val fig = mutableStateOf<IFigure>(Figure.Empty)
    val helpText = mutableStateOf(AnnotatedString(""))

    private val t = TortoiseRunner(SimpleTortoiseMemory())

    fun saveTortoise(fileName: String, lines: String) {
        val program = tortoiseProgram(lines)
        val fig = t.draw(program, Vec2.Zero, tools.ds())
        tools.saveFigures(fileName, fig)
    }

    private fun tortoiseProgram(lines: String): TortoiseProgram {
        return TortoiseProgram(commands = lines.split("\n").map { line ->
            TortoiseParser.extractTortoiseCommands(line)
        })
    }

    fun createTortoise(lines: String) {
        val program = tortoiseProgram(lines)
        val dr = t.draw(program, Vec2.Zero, tools.ds())
        fig.value = dr
        figures.value = dr
    }

    fun drop(dropValueX: Float, dropValueY: Float) {
        figures.value =
            fig.value.crop(dropValueX.toDouble(), CropSide.BOTTOM).crop(dropValueY.toDouble(), CropSide.LEFT)
    }


    private val helpSeparator = charArrayOf('\n', '\r')
    private val helpSpaceSeparator = charArrayOf('\n', '\r', ' ', '\t', ';', '@')
    fun findHelp(text: String, selection: TextRange) {
        val s = selection.start
        val p = text.lastIndexOfAny(helpSeparator, s) + 1
        val e = Math.min(text.length - 1, text.indexOfAny(helpSpaceSeparator, p + 1))


        val subStr = if (p in 0 until e) {
            text.substring(p, e)
        } else ""

        val help = TortoiseParser.helpFor(subStr)
        helpText.value = help

    }

    val text = mutableStateOf("")
}