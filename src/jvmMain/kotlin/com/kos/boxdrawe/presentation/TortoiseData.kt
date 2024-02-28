package com.kos.boxdrawe.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.IFigure
import turtoise.*
import turtoise.memory.SimpleTortoiseMemory

class TortoiseData(val tools: ITools) {
    val figures = mutableStateOf<IFigure>(Figure.Empty)
    val fig = mutableStateOf<IFigure>(Figure.Empty)
    val helpText = mutableStateOf(AnnotatedString(""))

    val matrix = mutableStateOf(Matrix())

    fun saveTortoise(fileName: String, lines: String) {
        val program = tortoiseProgram(lines)
        val t = TortoiseRunner( program)
        val state = TortoiseState()
        val fig = t.draw(state, tools.ds())
        tools.saveFigures(fileName, fig)
    }

    suspend fun printCommand(lines: String):String{
        val program = tortoiseProgram(lines)
        return program.commands.flatMap { a ->
                a.names.flatMap { name -> a.commands(name,tools.ds()).flatMap {
                    it.commands
                }
            }
        }.map { c -> c.print()}.joinToString("\n")
    }

    private fun tortoiseProgram(lines: String): TortoiseProgram {
        val f = lines.split("\n").map { line ->
            TortoiseParser.extractTortoiseCommands(line)
        }

        val (c, a) = f.partition { it.first == "" }
        return TortoiseProgram(
            commands = c.map { it.second },
            algorithms = a.toMap()
        )
    }

    fun createTortoise(lines: String) {
        val program = tortoiseProgram(lines)
        val t = TortoiseRunner( program)
        val state = TortoiseState()
        val dr = t.draw(state, tools.ds())


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

    fun rotate(x: Float, y: Float, z: Float) {
        val m = Matrix()
        m.reset()
        m.rotateX(x)
        m.rotateY(y)
        m.rotateZ(z)
        matrix.value = m
    }

    val text = mutableStateOf("")
}