package com.kos.boxdrawe.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getTextAfterSelection
import androidx.compose.ui.text.input.getTextBeforeSelection
import com.kos.boxdrawer.template.TemplateGeneratorSimpleListener
import com.kos.boxdrawer.template.TemplateMemory
import com.kos.figure.CropSide
import com.kos.figure.Figure
import com.kos.figure.FigureEmpty
import com.kos.figure.IFigure
import kotlinx.coroutines.flow.MutableStateFlow
import turtoise.TortoiseProgram
import turtoise.TortoiseRunner
import turtoise.TortoiseState
import turtoise.parser.TortoiseParser

class TortoiseData(override val tools: ITools) : SaveFigure {
    val figures = MutableStateFlow<IFigure>(FigureEmpty)
    val fig = mutableStateOf<IFigure>(FigureEmpty)
    val helpText = mutableStateOf(AnnotatedString(""))

    val matrix = mutableStateOf(Matrix())

    @OptIn(ExperimentalFoundationApi::class)
    val text = mutableStateOf(TextFieldValue(""))

    val editorListener = object : TemplateGeneratorSimpleListener {
        val memory = TemplateMemory()
        override fun put(arg: String, index: Int, count: Int, value: String) {
            if (arg == "pos"){
                reposition(value)
            }else {
                if (count > 1) {
                    memory.put(arg, index, count, value)
                    recalc()
                } else {
                    put(arg, value)
                }
            }
        }

        override fun put(arg: String, value: String) {
            if (arg == "pos"){
                reposition(value)
            }else {
                memory.put(arg, value)
                recalc()
            }
        }

        override fun get(arg: String): List<String> {
            return memory.get(arg)
        }

        fun reposition(value:String){
            val f = " $value"
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

        fun recalc() {
            val x = memory.get("xy").getOrElse(0) { "0" }
            val y = memory.get("xy").getOrElse(1) { "0" }
            val xa =  memory.get("axy").getOrElse(0) { "0" }
            val ya = memory.get("axy").getOrElse(1) { "0" }
            val a = memory.get("a").getOrElse(0) { "0" }

            val f =   if (xa.toDoubleOrNull() == 0.0 && ya.toDoubleOrNull() == 0.0){
                if (x.toDoubleOrNull() == 0.0 && y.toDoubleOrNull() == 0.0){
                    " q $a "
                }else {
                    " m $x $y $a "
                }
            }else{
                " m $x $y $a $xa $ya "
            }


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
    }

    override suspend fun createFigure(): IFigure {
        val lines = text.value.text
        return create(lines)
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
        val lines = text.value.text
        val dr = create(lines)

        fig.value = dr
        figures.value = dr
    }

    private fun create(lines: String): IFigure {
        val program = tortoiseProgram(lines)
        val t = TortoiseRunner(program)
        val state = TortoiseState()
        val dr = t.draw(state, tools.ds())
        return dr
    }

//    fun drop(dropValueX: Float, dropValueY: Float) {
//        figures.value =
//            fig.value.crop(dropValueX.toDouble(), CropSide.BOTTOM)
//                .crop(dropValueY.toDouble(), CropSide.LEFT)
//    }


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

}